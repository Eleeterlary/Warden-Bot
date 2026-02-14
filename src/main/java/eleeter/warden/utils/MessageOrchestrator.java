package eleeter.warden.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MessageOrchestrator
{

    /**
     * Deletes the current message that triggered a filter.
     */
    public static void deleteCurrent(MessageReceivedEvent event)
    {
        event.getMessage().delete().queue(
                success ->
                {
                },
                error -> System.out.println("Failed to delete: " + error.getMessage())
        );
    }

    /**
     * Performs a deep scan of channel history and wipes similar messages from the user.
     */
    public static void purgeUserSpam(MessageReceivedEvent event, String targetContent)
    {
        long userId = event.getAuthor().getIdLong();

        event.getChannel().getHistory().retrievePast(50).queue(messages ->
        {
            List<Message> trash = messages.stream()
                    .filter(m -> m.getAuthor().getIdLong() == userId)
                    .filter(m -> Blocker.getSimilarity(m.getContentRaw().toLowerCase(), targetContent.toLowerCase()) > 0.85)
                    .collect(Collectors.toList());

            if (trash.size() > 1)
            {
                event.getGuildChannel().deleteMessages(trash).queue();
            } else if (trash.size() == 1)
            {
                trash.get(0).delete().queue();
            }
        });
    }


}