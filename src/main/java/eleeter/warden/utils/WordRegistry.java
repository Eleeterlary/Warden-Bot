package eleeter.warden.utils;



public class WordRegistry
{

    /**
     * The following list contains highly offensive language.
     * These words are included strictly for the purpose of automated
     * content moderation to protect the community.
     * Inclusion does not reflect the values of the developer.
     */



    public static final String[] BLACKLIST = {

            /* A */
            "ass",
            "arsehead",
            "asshole",
            "arsehead",
            "arse",

            /* B */
            "bastard",
            "bitch",
            "bloody",
            "bollocks",
            "brotherfucker",
            "bugger",
            "bullshit",

            /* C */
            "child-fucker",
            "cock",
            "cocksucker",
            "crap",
            "cunt",

            /* D */
            "dick",
            "dick-head",
            "dickhead",
            "dickhead",
            "dumbass",
            "dyke",

            /* F */
            "fag",
            "fagrot",
            "fatherfucker",
            "fuck",
            "fucked",
            "fucker",
            "fucking",

            /* J */
            "jackass",

            /* M */
            "motherfucker",

            /* N */
            "nigga",
            "niger",
            "nigra",

            /* P */
            "pigfucker",
            "piss",
            "prick",
            "pussy",

            /* S */
            "shit",
            "sisterfuck",
            "sisterfucker",
            "slut",


    };

    /** I know these words are too bad,
     * But it's Very important To save The Community
     */

    public static boolean isProfane(String input) {
        if (input == null || BLACKLIST.length == 0) return false;

        String[] words = input.toLowerCase().split("\\s+");

        for (String rawWord : words) {
            String cleanWord = rawWord.replaceAll("[^a-zA-Z]", "");

            String squashedWord = cleanWord.replaceAll("([a-z])\\1+", "$1");

            for (String badWord : BLACKLIST) {
                String squashedBadWord = badWord.toLowerCase().replaceAll("([a-z])\\1+", "$1");

                if (squashedWord.equals(squashedBadWord)) {
                    return true;
                }
            }
        }
        return false;
    }
}
