package eleeter.warden.bot;

import eleeter.warden.utils.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandRegistry
{
    public static void registerCommands(JDA jda)
    {
        Guild devGuild = jda.getGuildById(Config.DEV_GUILD_ID);
        if (devGuild != null)
        {

            devGuild.updateCommands().addCommands(

                    Commands.slash(Config.CMD_WHOIS, Config.CMD_WHOIS_DESC)
                            .addOption(OptionType.USER, Config.OPT_TARGET, Config.OPT_TARGET_DESC, true),


                    Commands.slash(Config.CMD_REPORT_DM, Config.CMD_REPORT_DM_DESC)
                            .addOption(OptionType.USER, Config.OPT_TARGET, Config.OPT_TARGET_DESC, true)
                            .addOption(OptionType.STRING, Config.OPT_REASON, Config.OPT_REPORT_DESC, true),


                    Commands.slash(Config.CMD_STATUS, Config.CMD_STATUS_DESC)
                            .addOption(OptionType.STRING, Config.OPT_ID, Config.OPT_STATUS, true),


                    Commands.slash(Config.CMD_HUNTER, Config.CMD_HUNTER_DESC)
                            .addOption(OptionType.USER, Config.OPT_TARGET, Config.OPT_TARGET_DESC, true)
                            .addOption(OptionType.STRING, Config.OPT_PHRASE, Config.OPT_PHRASE_DESC, true)
                            .addOption(OptionType.INTEGER, Config.OPT_AMOUNT, Config.PURGE_AMOUNT_DESC, true),


                    Commands.slash(Config.CMD_SECTOR_POLL, Config.CMD_SECTOR_POLL_DESC)
                            .addOption(OptionType.STRING, Config.OPT_SECTOR_NAME, Config.OPT_SECTOR_NAME_DESC, true),


                    Commands.slash(Config.CMD_PURGE, Config.CMD_PURGE_DESC)
                            .addOption(OptionType.INTEGER, Config.OPT_AMOUNT, Config.PURGE_AMOUNT_DESC, true),


                    Commands.slash(Config.CMD_BAN, Config.CMD_BAN_DESC)
                            .addOption(OptionType.USER, Config.OPT_BAN_TARGET, Config.OPT_BAN_TARGET_DESC, true),


                    Commands.slash(Config.CMD_WARN, Config.CMD_WARN_DESC)
                            .addOption(OptionType.USER, Config.OPT_TARGET, Config.OPT_TARGET_DESC, true),


                    Commands.slash(Config.CMD_UNWARN, Config.CMD_UNWARN_DESC)
                            .addOption(OptionType.USER, Config.OPT_TARGET, Config.OPT_TARGET_DESC, true)
            ).queue();
        }
    }
}