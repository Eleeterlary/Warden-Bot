package eleeter.warden;

import eleeter.warden.Content.CommandListener;
import eleeter.warden.Content.JoinListener;
import eleeter.warden.Content.WardenListener;
import eleeter.warden.data.WardenStore;
import eleeter.warden.bot.CommandRegistry;
import eleeter.warden.utils.config.Config;
import eleeter.warden.bot.PenaltyManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        PenaltyManager sharedPenaltyManager = new PenaltyManager();

        WardenStore.load();

        JDA jda = JDABuilder.createDefault(Config.TOKEN).enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS).setChunkingFilter(ChunkingFilter.ALL).setMemberCachePolicy(MemberCachePolicy.ALL).addEventListeners(new WardenListener(sharedPenaltyManager), new CommandListener(sharedPenaltyManager), new JoinListener(sharedPenaltyManager)).build().awaitReady();
        CommandRegistry.registerCommands(jda);
    }
}