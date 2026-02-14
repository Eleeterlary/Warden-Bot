package eleeter.warden.utils;

public class BannedContent
{

    // --- GIF TRIGGERS ---
    public static final String TENOR_URL = "tenor.com/view";
    public static final String GIPHY_URL = "giphy.com/gifs";
    public static final String GIF_EXTENSION = ".gif";

    // --- LINK TRIGGERS ---
    public static final String DISCORD_INVITE = "discord.gg/";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    // --- SPAM TRIGGERS
    public static final String SCAM_CRYPTO = "free crypto";
    public static final String SCAM_NITRO = "free nitro";
    public static final String NON_ENGLISH_PATTERN = "^[\\u0000-\\u007F\\u2000-\\u32FF\\u1F000-\\u1F9FF\\s]*$";
}