package eleeter.warden.bot;

import eleeter.warden.data.WardenStore;
import eleeter.warden.utils.config.BotMessages;
import eleeter.warden.utils.config.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PenaltyManager
{
    private final java.util.concurrent.ConcurrentHashMap<Long, Integer> warns = new java.util.concurrent.ConcurrentHashMap<>(WardenStore.getWarningData());

    public void applyWordWarn(MessageReceivedEvent event)
    {
        process(event.getMember(), event.getChannel().asTextChannel(), BotMessages.BAD_WORD_DETECTED, event.getMessage());
    }

    public void applyGifWarn(MessageReceivedEvent event)
    {
        process(event.getMember(), event.getChannel().asTextChannel(), BotMessages.GIF_DETECTED, event.getMessage());
    }

    public void applySpamWarn(MessageReceivedEvent event)
    {
        process(event.getMember(), event.getChannel().asTextChannel(), BotMessages.SPAM_DETECTED, event.getMessage());
    }

    public void applyLangWarn(MessageReceivedEvent event, String reason)
    {

        process(event.getMember(), event.getChannel().asTextChannel(), reason, event.getMessage());
    }
/**
 *  [WARN 1] I've told you before, Keep out %r From this space. this is an English-only space. The 'Warned' role has been assigned.
 */
    public int addManualWarnOnly(Member member)
    {
        long userId = member.getIdLong();
        int count = warns.merge(userId, 1, Integer::sum);

        WardenStore.save(member, count, "Manual Warning");

        applyInternalPunishment(member, count);
        return count;
    }

    public void resetWarns(Member member)
    {
        long userId = member.getIdLong();
        warns.put(userId, 0);

        WardenStore.save(member, 0, "Warns Reset by Staff");

        var guild = member.getGuild();
        long[] roleIds = {
                Config.WARN_1_ROLE_ID,
                Config.WARN_2_ROLE_ID,
                Config.WARN_3_ROLE_ID
        };


        var memberRoles = member.getRoles();
        for (long id : roleIds)
        {
            if (id <= 0) continue;
            Role r = guild.getRoleById(id);

            if (r != null && memberRoles.contains(r))
            {
                guild.removeRoleFromMember(member, r).queue();
            }
        }
    }

    private int process(Member member, TextChannel channel, String reason, Message userMsg)
    {

        if (member.getIdLong() == Config.OWNER_ID)
        {
            return 0;
        }

        if (member.isTimedOut()) return warns.getOrDefault(member.getIdLong(), 0);

        long userId = member.getIdLong();
        int count = warns.merge(userId, 1, Integer::sum);

        if (count >= 2)
        {
            WardenStore.save(member, count, reason);
            applyInternalPunishment(member, count);
        }

        String rawTemplate = switch (count)
        {
            case 1 -> BotMessages.FIRST_WARNING;
            case 2 -> BotMessages.SECOND_WARNING;
            case 3 -> BotMessages.THIRD_WARNING;
            default -> "";
        };

        if (!rawTemplate.isEmpty())
        {
            var action = channel.sendMessage(rawTemplate.replace("%s", member.getAsMention()).replace("%r", reason));
            if (count < 3)
            {
                action.queue(m -> m.delete().queueAfter(Config.AUTO_DELETE_SEC, TimeUnit.SECONDS));
            } else
            {
                action.queue();
            }
        }
        return count;
    }

    public void applyInternalPunishment(Member member, int count)
    {
        var guild = member.getGuild();
        Role r1 = guild.getRoleById(Config.WARN_1_ROLE_ID);
        Role r2 = guild.getRoleById(Config.WARN_2_ROLE_ID);
        Role r3 = guild.getRoleById(Config.WARN_3_ROLE_ID);

        switch (count)
        {
            case 2 ->
            {
                if (r1 != null) guild.addRoleToMember(member, r1).queue();
            }
            case 3 ->
            {
                if (r1 != null) guild.removeRoleFromMember(member, r1).queue();
                if (r3 != null) guild.addRoleToMember(member, r3).queue();

                if (guild.getSelfMember().canInteract(member))
                {
                    member.timeoutFor(Duration.ofMinutes(10)).queue();
                }
            }
        }
    }

    public int getWarnCount(Member member)
    {
        return warns.getOrDefault(member.getIdLong(), 0);
    }
}