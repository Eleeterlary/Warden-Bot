package eleeter.warden.ui;

import eleeter.warden.bot.ServiceManager;
import eleeter.warden.utils.config.Config;
import eleeter.warden.utils.config.EmbedConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class ControlPanel
{
    public static MessageEmbed getEmbed()
    {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🛰️ WARDEN CORE CONTROL PANEL")
                .setColor(0x2F3136)
                .setDescription(EmbedConfig.SERVICE_PANEL_DISC);

        ServiceManager.getServices().forEach((name, state) ->
                eb.addField(name.replace("_", " ").toUpperCase(), state ? EmbedConfig.GREEN_BUTTON : EmbedConfig.RED_BUTTON, true)
        );

        eb.setFooter("Authorized Warden Access Only");
        return eb.build();
    }

    public static List<ActionRow> getButtons()
    {
        List<ActionRow> rows = new ArrayList<>();
        List<Button> row1 = new ArrayList<>();
        List<Button> row2 = new ArrayList<>();

        int count = 0;
        for (String name : ServiceManager.getServices().keySet())
        {
            boolean state = ServiceManager.isEnabled(name);

            String clearName = switch (name.toLowerCase())
            {
                case Config.TRUST -> "Trust System";
                case Config.INCINDENT -> "Log Visuals";
                case Config.RAID -> "Anti-Raid";
                case Config.LENGTH -> "Block Long Msgs";
                case Config.SPAM -> "Anti-Spam";
                case Config.SCAM -> "Block Scams";
                case Config.PROFANITY -> "Bad Words";
                case Config.LANGUAGE -> "Foreign Lang";
                case Config.LINK -> "Block Links";
                default -> name.replace("_", " ");
            };

            String label = (state ? "✅ " : "❌ ") + clearName.toUpperCase();

            Button btn = state ?
                    Button.success("sys:" + name, label) :
                    Button.danger("sys:" + name, label);

            if (count < 5) row1.add(btn);
            else row2.add(btn);
            count++;
        }

        rows.add(ActionRow.of(row1));
        if (!row2.isEmpty()) rows.add(ActionRow.of(row2));

        return rows;
    }
}