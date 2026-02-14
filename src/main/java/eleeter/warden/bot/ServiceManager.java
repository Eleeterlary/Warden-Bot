package eleeter.warden.bot;

import eleeter.warden.utils.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager
{
    private static final Map<String, Boolean> services = new HashMap<>();

    static
    {

        /**
         * All Buttons
         * You can call them "NOT GATE"
         * */
        services.put(Config.TRUST, true);
        services.put(Config.INCINDENT, true);
        services.put(Config.RAID, true);
        services.put(Config.LENGTH, true);
        services.put(Config.SPAM, true);
        services.put(Config.SCAM, true);
        services.put(Config.PROFANITY, true);
        services.put(Config.LANGUAGE, true);
        services.put(Config.LINK, true);

    }

    public static boolean isEnabled(String key)
    {
        return services.getOrDefault(key.toLowerCase(), true);

    }

    public static void toggle(String key)
    {
        services.put(key.toLowerCase(), !isEnabled(key));
    }

    public static Map<String, Boolean> getServices()
    {
        return services;
    }
}