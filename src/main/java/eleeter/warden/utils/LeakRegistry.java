package eleeter.warden.utils;

import java.util.List;
import java.util.regex.Pattern;

public class LeakRegistry
{

    private static final Pattern CHAPTER_PATTERN = Pattern.compile("(ch|chapter|chap|ep|episode)\\s*\\d+", Pattern.CASE_INSENSITIVE);

    private static final List<String> PHRASES = List.of(

            /** Specific Plot Beats */
            "gojodies", "gojodeath", "sukunawins", "luffogear6", "dekuisdead",
            "yutadiying", "itadoridies", "megumikilled", "shanksdies", "ripgojo",

            /** Leak Markers */
            "rawsareout", "chapterleaks", "mangaleak", "confirmedleaks",
            "leaksarehere", "spoilersareout", "readtheleak", "justreadraws",
            "leakconfirmed", "tcbscans", "leakready",

            /** The "How People Talk" Patterns  */
            "isdeadinthelatest", "diedinthelatest", "hediedinlast",
            "shediedinlast", "theyallkilled", "everyoneisdead",
            "endingisout", "readthespoiler", "isdeadinthemanga",
            "diedinthemanga", "mangaspoiler", "latestchapter",
            "didhediedintheend", "shediedinthend", "didyouwatchtheendhediedintheend",
            "didyouwatchtheendinghediedintheend",

            /**  General Spoiling Intent  */
            "actuallydies", "actuallykilled", "endsupdying",
            "isgoingtodie", "willdiein", "dieinginthelatest",
            "dieingintheseason", "whydidhekill", "whydidshekill",
            "isdeadforreal", "confirmedbygege", "gegekilled",

            /** Anime Season/Episode Leaks */
            "nextseason", "season2", "season3", "season4", "season5",
            "newseason", "upcomingseason", "releasedate", "airdate",
            "nextepisode", "newepisode", "episodepreview", "leakedepisode",
            "leakedscene", "leakedclip", "leakedfootage", "leakedimage",
            "leakedpic", "leakedphoto", "leakedvideo", "leakedaudio",
            "leakedscript", "leakedplot", "leakedstory", "leakedending",
            "leakedopening", "leakedending", "leakedop", "leakeded",
            "leakedost", "leakedtrack", "leakedmusic", "leakedvoice",
            "leakedcast", "leakedstaff", "leakedstudio", "leakedanimation",
            "leakedart", "leakeddesign", "leakedcharacter", "leakedvisual",
            "leakedposter", "leakedtrailer", "leakedteaser", "leakedpv",
            "leakedkv", "leakedkeyvisual", "leakedscan", "leakedmagazine",
            "leakedinterview", "leakedannouncement", "leakednews", "leakedinfo"
    );

    public static boolean isBlacklisted(String rawMessage)
    {
        String squished = rawMessage.toLowerCase().replaceAll("\\s+", "");

        if (CHAPTER_PATTERN.matcher(rawMessage).find())
        {
             return PHRASES.stream().anyMatch(squished::contains);
        }

        /**
         * random words (like "ok", "hey")
         * return false;
         *
         * */
        if (squished.length() < 6)
            return false;

        return PHRASES.stream().anyMatch(squished::contains);
    }
}