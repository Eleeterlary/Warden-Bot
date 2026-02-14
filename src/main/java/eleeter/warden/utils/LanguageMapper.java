package eleeter.warden.utils;

public class LanguageMapper
{
    public static String getLanguageFromBlock(String blockName)
    {
        String b = blockName.toUpperCase();

        if (b.contains("EMOJI") ||
                b.contains("SYMBOL") ||
                b.contains("LATIN") ||
                b.contains("DINGBAT") ||
                b.contains("PUNCTUATION") ||
                b.contains("PICTOGRAPH"))
        {
            return null;
        }

        if (b.contains("CYRILLIC")) return "Russian";
        if (b.contains("ARABIC")) return "Arabic";
        if (b.contains("GREEK")) return "Greek";
        if (b.contains("HEBREW")) return "Hebrew";
        if (b.contains("HAN") || b.contains("IDEOGRAPH")) return "Chinese";
        if (b.contains("HANGUL")) return "Korean";
        if (b.contains("COMBINING"))return "Glitch Text";
        if (b.contains("HIRAGANA") || b.contains("KATAKANA")) return "Japanese";

        return null;
    }
}