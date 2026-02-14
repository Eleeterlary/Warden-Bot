package eleeter.warden.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Blocker handles the normalization of input strings to prevent
 * homoglyph-based bypasses of the profanity filter.
 * Slap them hard!
 */
public class Blocker
{

    private static final Map<Character, Character> HOMOGLYPH_MAP = new HashMap<>();
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    static
    {
        String cyrillic = "аеіјорсух";
        String latin = "aeijopcyx";
        for (int i = 0; i < cyrillic.length(); i++)
        {
            HOMOGLYPH_MAP.put(cyrillic.charAt(i), latin.charAt(i));
        }

        String greek = "αεορτυνχ";
        String gLat = "aeoptuvx";
        for (int i = 0; i < greek.length(); i++)
        {
            HOMOGLYPH_MAP.put(greek.charAt(i), gLat.charAt(i));
        }

        HOMOGLYPH_MAP.put('0', 'o');
        HOMOGLYPH_MAP.put('1', 'l');
        HOMOGLYPH_MAP.put('3', 'e');
        HOMOGLYPH_MAP.put('4', 'a');
        HOMOGLYPH_MAP.put('5', 's');
        HOMOGLYPH_MAP.put('!', 'i');
        HOMOGLYPH_MAP.put('@', 'a');
    }

    /**
     * TODAY: I Just Saw a Video on YouTube about Homoglyph.
     * Normalizes text by removing accents flattening fancy fonts
     * and mapping look-alike characters to standard Latin.
     */
    public static String normalize(String input)
    {
        if (input == null || input.isEmpty()) return "";

        String result = Normalizer.normalize(input, Normalizer.Form.NFKC);

        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = DIACRITICS_PATTERN.matcher(result).replaceAll("");

        StringBuilder sb = new StringBuilder();
        for (char c : result.toLowerCase().toCharArray())
        {
            sb.append(HOMOGLYPH_MAP.getOrDefault(c, c));
        }

        return sb.toString();
    }


    /**
     * This is the most Method powerful Ever I made
     * This WIll Detect Curse word Even if someone is oversmart
     * <p>
     * Eg: If Someone say S    h       i      t This new method will Detect it!
     */
    public static String squish(String input)
    {
        String clean = input.replaceAll("[\\p{Punct}\\s]+", "");

        StringBuilder sb = new StringBuilder();
        if (clean.length() > 0)
        {
            sb.append(clean.charAt(0));
            for (int i = 1; i < clean.length(); i++)
            {
                if (clean.charAt(i) != clean.charAt(i - 1))
                {
                    sb.append(clean.charAt(i));
                }
            }
        }
        return sb.toString();
    }


    /**
     * Kind of Fucked Up Math But, It's working that's it
     * Because I'm Bad at math
     * If you want pain, then try to edit it :)
     */
    public static double getSimilarity(String s1, String s2)
    {
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int longer = Math.max(s1.length(), s2.length());
        int editDistance = editDistance(s1, s2);
        return (longer - editDistance) / (double) longer;
    }

    private static int editDistance(String s1, String s2)
    {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
        {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++)
            {
                if (i == 0) costs[j] = j;
                else if (j > 0)
                {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1))
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}