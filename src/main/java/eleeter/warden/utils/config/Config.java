package eleeter.warden.utils.config;

import java.time.format.DateTimeFormatter;

public class Config
{
    /** WARN */
    public static final long WARN_1_ROLE_ID = 1461467210894807249L;
    public static final long WARN_2_ROLE_ID = 1463418202305331275L;
    public static final long WARN_3_ROLE_ID = 1463418299043020881L;
    public static final long ROLE_TRUSTED_ID = 1463562002533191947L;
    public static final long ROLE_RESIDENT_ID = 1463562104882593976L;


    /** COMMANDS */
    public static final String CMD_BAN = "ban";
    public static final String CMD_RULES = "rules";
    public static final String CMD_REPORT_DM = "report-dm";
    public static final String CMD_STATUS = "case-status";
    public static final String CMD_WARN = "warn";
    public static final String CMD_HUNTER = "hunter";
    public static final String CMD_WHOIS = "whois";
    public static final String CMD_UNWARN = "unwarn";
    public static final String CMD_PURGE = "purge";
    public static final String CMD_SECTOR_POLL = "sector_poll";


    /** COMMANDS DESCRIPTION */
    public static final String CMD_REPORT_DM_DESC = "Report a user for suspicious or scam DMs.";
    public static final String CMD_STATUS_DESC = "Check the live investigation status of a Case ID";
    public static final String CMD_BAN_DESC = "Owner only ban.";
    public static final String CMD_WARN_DESC = "Manually add a warning to a user.";
    public static final String CMD_HUNTER_DESC = "specific messages from a specific user.";
    public static final String CMD_WHOIS_DESC = "Investigate a user's details and warning count.";
    public static final String CMD_UNWARN_DESC = "Reset a user's warning count.";
    public static final String CMD_PURGE_DESC = "Deletes a specific amount of messages.";
    public static final String CMD_SECTOR_POLL_DESC = "Initiate a 10-minute Sector Authorization Poll";


    public static final String OPT_AMOUNT = "amount";
    public static final String OPT_TARGET = "user";
    public static final String OPT_STATUS = "The User ID / Case ID to track";

    public static final String OPT_REASON = "reason";
    public static final String OPT_REPORT_DESC = "Describe the scam or provide evidence links";
    public static final String OPT_ID = "id";
    public static final String OPT_PHRASE_DESC = "The specific text/pattern to hunt for.";
    public static final String OPT_PHRASE = "phrase";
    public static final String OPT_TARGET_DESC = "The user to manage";
    public static final String OPT_BAN_TARGET = "target";
    public static final String OPT_BAN_TARGET_DESC = "The user to ban";
    public static final String OPT_SECTOR_NAME = "name";
    public static final String OPT_SECTOR_NAME_DESC = "The name of the sector to authorize";


    /**
     * PURGE COMMAND
     */
    public static final String PURGE_WARN_MESSAGE = "❌ Please choose an amount between 1 and 100.";
    public static final String PURGE_FAILED_TO = "❌ Failed to purge: ";
    public static final String PURGE_AMOUNT_DESC = "Number of messages to delete (1-100)";
    public static final String PURGE_SUCCESS_MESSAGE = "✅ Successfully purged ";
    public static final int PURGE_AMOUNT = 100;


    /** WARN/UNWARN */
    public static final String WARN_MESSAGE = "Warning added for %s. Current warns: %d";
    public static final String UNWARN_MESSAGE = "Warnings cleared and role removed for %s.";


    /** SYSTEM */
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String CASE_STATUS_IMG_FORMAT = "warden_case_resolution.png";
    public static final String IMAGE_FORMAT = "png";


    /** JSON */
    public static final String OUTPUT_JSON = "warden_data.json";
    public static final String MESSAGE = " Messages";
    public static final String COUNT = "count";
    public static final String REASON = "last_reason";
    public static final String NAME = "name";
    public static final String TIME_STAMP = "time_stamp";


    /** FILTER */
    public static final String TRUST = "trust_orchestrator";
    public static final String INCINDENT = "incident_logging";
    public static final String RAID = "raid_protection";
    public static final String LENGTH = "length_filter";
    public static final String SPAM = "spam_filter";
    public static final String SCAM = "scam_filter";
    public static final String PROFANITY = "profanity_filter";
    public static final String LANGUAGE = "language_filter";
    public static final String LINK = "link_filter";
    public static final String LEAK = "leak_filter";


    /** BOT USERDATA */
    public static final String TOKEN = System.getenv("TOKEN");
    public static final String BOT_NAME = "warden";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    public static boolean IS_TESTING = false;
    public static final int RAID_THRESHOLD = 10; // Messages per 5 seconds
    public static final int RAID_WINDOW_MS = 5000;
    public static final int TRUSTED_ACCOUNT_AGE = 7;
    public static final int RESIDENT_ACCOUNT_AGE = 2;
    public static final String ONBUTTONINTERACTION = "sys:";
    public static final long OWNER_ID = 830790558179000331L;
    public static final long DEV_GUILD_ID = 1461066017118879911L;
    public static final long ID = 1461494742545207367L;
    public static final int UNI_ZERO = 0;
    public static final int SPAM_THRESHOLD = 10;
    public static final int AUTO_DELETE_SEC = 15;

}