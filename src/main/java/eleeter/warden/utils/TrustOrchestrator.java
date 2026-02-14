package eleeter.warden.utils;

import eleeter.warden.utils.config.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class TrustOrchestrator
{

    /**
     * 
     * This method is intended to be called on join
     */


    public static void evaluateTrust(Member member)
    {
        if (member.getUser().isBot()) return;
        /**
         * Assigns roles based on account age:
         *  ≥ 7 days old → Trusted role
         *  ≥ 2 days old → Residen
         */

        long daysOld = ChronoUnit.DAYS.between(member.getUser().getTimeCreated(), OffsetDateTime.now());
        var guild = member.getGuild();

        if (daysOld >= Config.ROLE_TRUSTED_ID)
        {
            Role trustedRole = guild.getRoleById(Config.ROLE_TRUSTED_ID);
            if (trustedRole != null && !member.getRoles().contains(trustedRole))
            {
                guild.addRoleToMember(member, trustedRole).queue();
            }
        } else if (daysOld >= Config.ROLE_RESIDENT_ID)
        {
            Role residentRole = guild.getRoleById(Config.ROLE_RESIDENT_ID);
            if (residentRole != null && !member.getRoles().contains(residentRole))
            {
                guild.addRoleToMember(member, residentRole).queue();
            }
        }
    }
}