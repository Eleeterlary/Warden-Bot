package eleeter.warden.Content;

import eleeter.warden.bot.PenaltyManager;
import eleeter.warden.bot.ServiceManager;
import eleeter.warden.utils.BannedContent;
import eleeter.warden.utils.config.Config;
import eleeter.warden.utils.Blocker;
import eleeter.warden.utils.WordRegistry;
import eleeter.warden.utils.LanguageMapper;
import eleeter.warden.utils.MessageOrchestrator;
import eleeter.warden.utils.SanitizationEngine;
import eleeter.warden.utils.Protector;
import eleeter.warden.utils.TrustOrchestrator;
import eleeter.warden.ui.renderer.WardenRenderer;
import eleeter.warden.utils.LeakRegistry;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WardenListener extends ListenerAdapter
{

    private final PenaltyManager penalties;

    private final ExecutorService renderPool = Executors.newFixedThreadPool(4);


    private final Map<Long, Integer> repeatTracker = createLRUMap(1000);
    private final Map<Long, List<String>> userMemory = createLRUMap(1000);




    public WardenListener(PenaltyManager penalties)
    {
        this.penalties = penalties;
    }




    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;

        if (ServiceManager.isEnabled(Config.TRUST))
        {
            TrustOrchestrator.evaluateTrust(event.getMember());
        }

        String rawMsg = event.getMessage().getContentRaw();
        String msgLower = rawMsg.toLowerCase();
        long uid = event.getAuthor().getIdLong();


        if (ServiceManager.isEnabled(Config.LEAK) && LeakRegistry.isBlacklisted(rawMsg)) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("⚠️ " + event.getAuthor().getAsMention() + ", no spoilers/leaks allowed!").queue();
            return;
        }


        if (uid == Config.OWNER_ID) return;

        if (ServiceManager.isEnabled(Config.INCINDENT))
        {
            var mentions = event.getMessage().getMentions().getMembers();

            if (!mentions.isEmpty())
            {
                renderPool.submit(() ->
                {
                    try
                    {
                        String target = mentions.get(0).getEffectiveName();
                        String violation = event.getAuthor().getName() + " pinged " + target;
                        String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

                        FileUpload proof = WardenRenderer.render(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl(), event.getMessage().getContentDisplay(), time, violation);

                        var channel = event.getGuild().getTextChannelById(1461494742545207367L);
                        if (channel != null && proof != null)
                        {
                            channel.sendFiles(proof).queue();
                        }
                    } catch (Exception ignored)
                    {
                    }
                });
            }
        }


        if (ServiceManager.isEnabled(Config.RAID) && Protector.isRaidActive())
        {
            if (msgLower.contains(BannedContent.HTTP_PREFIX) || msgLower.contains(BannedContent.DISCORD_INVITE))
            {
                event.getMessage().delete().queue();
                return;
            }
        }

        if (ServiceManager.isEnabled(Config.LENGTH) && rawMsg.length() > 125)
        {
            MessageOrchestrator.deleteCurrent(event);
            if (uid != Config.OWNER_ID) penalties.applySpamWarn(event);
            return;
        }

        if (ServiceManager.isEnabled(Config.SPAM))
        {
            var mentions = event.getMessage().getMentions().getMembers();
            if (!mentions.isEmpty())
            {
                List<String> historyBuffer = userMemory.computeIfAbsent(uid, k -> new ArrayList<>());

                boolean isPattern = SanitizationEngine.isPatternMatching(rawMsg, historyBuffer);

                if (isPattern)
                {
                    MessageOrchestrator.deleteCurrent(event);

                    int count = repeatTracker.getOrDefault(uid, 1) + 1;
                    repeatTracker.put(uid, count);

                    if (count >= Config.SPAM_THRESHOLD)
                    {
                        if (uid == Config.OWNER_ID) return;

                        SanitizationEngine.deepPurge(event.getGuildChannel(), rawMsg);
                        penalties.applySpamWarn(event);

                        repeatTracker.put(uid, 0);
                        return;
                    }
                    return;
                } else
                {
                    historyBuffer.add(rawMsg);
                    if (historyBuffer.size() > 5) historyBuffer.remove(0);

                    if (rawMsg.length() > 3) repeatTracker.put(uid, 1);
                }
            }
        }


        if (ServiceManager.isEnabled(Config.SCAM))
        {
            if (msgLower.contains(BannedContent.SCAM_CRYPTO) || msgLower.contains(BannedContent.SCAM_NITRO))
            {
                MessageOrchestrator.deleteCurrent(event);
                if (uid == Config.OWNER_ID) return;
                penalties.applySpamWarn(event);
                return;
            }
        }

        if (ServiceManager.isEnabled(Config.PROFANITY))
        {
            String normalizedMsg = Blocker.normalize(rawMsg);
            String fullyCleaned = Blocker.squish(normalizedMsg);

            if (WordRegistry.isProfane(fullyCleaned))
            {
                MessageOrchestrator.deleteCurrent(event);
                if (uid == Config.OWNER_ID) return;
                penalties.applyWordWarn(event);
                return;
            }
        }

        if (ServiceManager.isEnabled(Config.LANGUAGE))
        {
            String language = detectLanguageName(rawMsg);
            if (language != null)
            {
                MessageOrchestrator.deleteCurrent(event);
                if (uid == Config.OWNER_ID) return;
                penalties.applyLangWarn(event, language);
                return;
            }
        }

        if (ServiceManager.isEnabled(Config.LINK))
        {
            if (msgLower.contains(BannedContent.TENOR_URL) ||
                    msgLower.contains(BannedContent.GIPHY_URL) ||
                    msgLower.contains(BannedContent.GIF_EXTENSION) ||
                    msgLower.contains(BannedContent.DISCORD_INVITE) ||
                    msgLower.contains(BannedContent.HTTP_PREFIX) ||
                    msgLower.contains(BannedContent.HTTPS_PREFIX))
            {
                MessageOrchestrator.deleteCurrent(event);
                if (uid == Config.OWNER_ID) return;
                penalties.applyGifWarn(event);
                return;
            }
        }
    }

    private String detectLanguageName(String text)
    {
        /** keep this logic but it will run much smoother now that the main thread isn't rendering PNGs */
        for (int i = 0; i < text.length(); i++)
        {
            int codePoint = text.codePointAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);

            if (block == null) continue;

            String language = LanguageMapper.getLanguageFromBlock(block.toString());

            if (language != null) return language;
        }
        return null;
    }

    /**
     * this will prevent my Maps from growing infinitely and crashing my VPS memory.
     */
    private static <K, V> Map<K, V> createLRUMap(final int maxEntries)
    {
        return new java.util.LinkedHashMap<K, V>(maxEntries, 0.75f, true)
        {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
            {
                return size() > maxEntries;
            }
        };
    }
}