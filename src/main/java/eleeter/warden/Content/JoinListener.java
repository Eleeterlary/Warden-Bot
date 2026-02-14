package eleeter.warden.Content;

import eleeter.warden.bot.PenaltyManager;
import eleeter.warden.data.WardenStore;
import eleeter.warden.utils.TrustOrchestrator;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class JoinListener extends ListenerAdapter
{

    private final PenaltyManager pm;

    public JoinListener(PenaltyManager pm)
    {
        this.pm = pm;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        long uid = event.getUser().getIdLong();
        int previousWarnings = WardenStore.getWarnings(uid);

        if (previousWarnings > 0)
        {
            var member = event.getMember();
            System.out.println("warn applied to the" + event.getUser());

            pm.applyInternalPunishment(member, previousWarnings);

            if (previousWarnings >= 3)
            {
                if (event.getGuild().getSelfMember().canInteract(member))
                {
                    member.timeoutFor(Duration.ofMinutes(10))
                            .reason("Persistent flag: Isolation bypass attempt")
                            .queue();
                }
            }
        }
        TrustOrchestrator.evaluateTrust(event.getMember());
    }
}