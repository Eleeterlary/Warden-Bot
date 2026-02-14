package eleeter.warden.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.List;
import java.util.stream.Collectors;


public class SanitizationEngine {

    /**
     * Checks if the new message is similar to ANY of the previous messages in the buffer.
     */
    public static boolean isPatternMatching(String current, List<String> userHistory) {
        String cleanCurrent = Blocker.squish(Blocker.normalize(current));
        for (String oldMsg : userHistory) {
            String cleanOld = Blocker.squish(Blocker.normalize(oldMsg));
            if (Blocker.getSimilarity(cleanCurrent, cleanOld) > 0.80) {
                return true;
            }
        }
        return false;
    }

    public static void deepPurge(GuildMessageChannel channel, String targetContent) {
        String cleanTarget = Blocker.squish(Blocker.normalize(targetContent));

        channel.getHistory().retrievePast(100).queue(messages ->
        {
            List<Message> hits = messages.stream()
                    .filter(m ->
                    {
                        String historicalClean = Blocker.squish(Blocker.normalize(m.getContentRaw()));
                        return historicalClean.contains(cleanTarget) || Blocker.getSimilarity(historicalClean, cleanTarget) > 0.70;
                    })
                    .collect(Collectors.toList());

            if (!hits.isEmpty()) {
                if (hits.size() > 1) {
                    channel.deleteMessages(hits).queue();
                } else {
                    hits.get(0).delete().queue();
                }
            }
        });
    }

    /**
     * Wipes specific messages from a specific user.
     */
    public static void targetedHunter(net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel channel, net.dv8tion.jda.api.entities.User target, String matchPhrase, int depth) {
        String cleanMatch = Blocker.squish(Blocker.normalize(matchPhrase));

        java.time.OffsetDateTime safetyLine = java.time.OffsetDateTime.now().minusDays(14).plusMinutes(15);

        channel.getIterableHistory()
                .takeAsync(depth)
                .thenAccept(messages -> {
                    java.util.List<net.dv8tion.jda.api.entities.Message> hits = messages.stream()
                            .filter(m -> m.getAuthor().getIdLong() == target.getIdLong())
                            .filter(m -> m.getTimeCreated().isAfter(safetyLine))
                            .filter(m -> {
                                String cleanContent = Blocker.squish(Blocker.normalize(m.getContentRaw()));
                                return cleanContent.contains(cleanMatch) || Blocker.getSimilarity(cleanContent, cleanMatch) > 0.80;
                            })
                            .collect(java.util.stream.Collectors.toList());

                    if (!hits.isEmpty()) {
                        for (int i = 0; i < hits.size(); i += 100) {
                            int end = Math.min(hits.size(), i + 100);
                            channel.deleteMessages(hits.subList(i, end)).queue(null, err -> {
                            });
                        }
                    }
                });
    }


}