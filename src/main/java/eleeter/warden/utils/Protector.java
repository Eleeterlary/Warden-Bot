package eleeter.warden.utils;

import eleeter.warden.utils.config.Config;

import java.util.concurrent.atomic.AtomicInteger;

public class Protector
{

    /**
     * Detects whether the application is currently under a high-volume raid.
     * This logic is designed for emergency scenarios only, such as:
     * - Large-scale scammer attacks
     * - Coordinated bot raids
     * - Sudden abnormal traffic spikes
     *  And I don't want API Limit Ban Again, No, thanks
     */

    private static final AtomicInteger globalCounter = new AtomicInteger(0);
    private static long lastReset = System.currentTimeMillis();

    public static boolean isRaidActive()
    {
        long now = System.currentTimeMillis();

        if (now - lastReset > Config.RAID_WINDOW_MS)
        {
            globalCounter.set(0);
            lastReset = now;
        }

        return globalCounter.incrementAndGet() > Config.RAID_THRESHOLD;
    }
}