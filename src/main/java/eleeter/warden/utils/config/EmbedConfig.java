package eleeter.warden.utils.config;

public class EmbedConfig
{

    /** COMMAND LISTENER */
    public static final String WARDEN_REPORT_LOGO = "WARDEN SECURITY TERMINAL";
    public static final String WARDEN_UPLOAD_STATUS = "SECURE UPLOAD COMPLETE";
    public static final String ENCRYPYION = "### ENCRYPTION: AES-256\n";
    public static final String CASE_ID = "**Case ID:** `";
    public static final String REASON = "reason";
    public static final String RANDOM_SYMBOL_1 = "`\n";
    public static final String RANDOM_SYMBOL_2 = " (@";
    public static final String RANDOM_SYMBOL_3 = ")";
    public static final String RANDOM_SYMBOL_4 = ":";
    public static final String SYSTEM_NODE = "System Node: ";
    public static final String HEADER = "**Status:** `DISPATCHED TO WARDEN`\n\n";
    public static final String TIME_STAMP = "MMM dd, yyyy";
    public static final String DATE_TIME_FORMAT = "MMM dd, yyyy HH:mm:ss";
    public static final String TIME_HOUR_STAMP = "MMM dd, yyyy | HH:mm:ss";
    public static final String REPORT_ERROR_1 = "🛑 **TERMINAL ERROR:** Recursive reporting is prohibited.";
    public static final String REPORT_REPLY = "**CRITICAL ERROR:** Missing required parameters.";
    public static final String REPORT_TEXT = "> The submitted evidence has been logged and transmitted. Your identity remains protected under protocol.";
    public static final String WHOIS = "🔎 **User Report: %s**\n" + "ID: `%s`\n" + "Joined: <t:%d:R>\n" + "Account Created: <t:%d:R>\n" + "Current Warnings: **%d**";
    public static final String SEND_TEXT = "**Your report has been processed.**";
    public static final String LOG_EMBED = "🚨 CRITICAL INCIDENT REPORT";
    public static final String LOG_DIS = "Incoming report for **";
    public static final String LOG_2_DIS = "**\nProceed with immediate action below.";
    public static final String EVENT_TITLE = "⚠️ **SYSTEM ALERT:** Target user is no longer reachable within this guild.";


    /** EMBED BUTTON */
    public static final String WARDEN_BAN = "warden:ban:";
    public static final String WARDEN_BAN_BUTTON = "🔨 PERMANENT BAN";
    public static final String WARDEN_KICK = "warden:kick:";
    public static final String WARDEN_KICK_BUTTON = "👢 KICK USER";
    public static final String WARDEN_CLEAR = "warden:clear:";
    public static final String WARDEN_CLEAR_BUTTON = "✅ RESOLVE CASE";
    public static final String SERVICE_PANEL_DISC = "Toggle individual system modules. \n🟢 = ONLINE | 🔴 = OFFLINE";
    public static final String RED_BUTTON = "🔴";
    public static final String GREEN_BUTTON = "🟢";

    /**
     *  HANDLE CLOSED DMs.
     *  in short > "HD"
     *  */
    public static final String HD_HEADER_TEXT = "WARDEN CASES";


    /**
     * CREATE CASE CHANNEL
     * */
    public static final String CSC_CASE = "case-";
    public static final String CSC_REPORT = "Report Update";
    public static final String CSC_DM_MSG = "Your DMs are closed, so this private channel was created to deliver your Report update.\n\n";




    /** HUNTER */
    public static final String HUNTER_EVENT = "Scanned last ";
    public static final String HUNTER_EVENT_2 = " messages and purged matches from ";
    public static final String HUNTER_EVENT_OFFLINE = "❌ Hunter Protocol is currently OFFLINE.";
    public static final String HUNTER_FAILED = "Extraction failed: ";


}
