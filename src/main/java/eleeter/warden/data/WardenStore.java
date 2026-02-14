package eleeter.warden.data;


import eleeter.warden.utils.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.*;

public class WardenStore
{
    private static final String FILE_PATH = Config.OUTPUT_JSON;
    private static Map<Long, Integer> warningData = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void load()
    {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file))
        {
            Map<Long, Object> temp = gson.fromJson(reader, new TypeToken<Map<Long, Object>>()
            {
            }.getType());
            if (temp != null)
            {
                temp.forEach((id, val) ->
                {
                    if (val instanceof Map)
                    {
                        Double count = (Double) ((Map<?, ?>) val).get(Config.COUNT);
                        warningData.put(Long.parseLong(id.toString()), count.intValue());
                    } else if (val instanceof Double)
                    {
                        warningData.put(Long.parseLong(id.toString()), ((Double) val).intValue());
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void save(Member member, int count, String reason)
    {
        long uid = member.getIdLong();
        warningData.put(uid, count);

        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put(Config.NAME, member.getEffectiveName());
        logEntry.put(Config.COUNT, count);
        logEntry.put(Config.REASON, reason);
        logEntry.put(Config.TIME_STAMP, java.time.LocalDateTime.now().format(Config.DATE_FORMATTER));

        try
        {
            Map<String, Object> fullFile = new HashMap<>();
            File file = new File(FILE_PATH);
            if (file.exists())
            {
                try (Reader r = new FileReader(file))
                {
                    fullFile = gson.fromJson(r, new TypeToken<Map<String, Object>>()
                    {
                    }.getType());
                }
            }
            if (fullFile == null) fullFile = new HashMap<>();
            fullFile.put(String.valueOf(uid), logEntry);

            try (Writer writer = new FileWriter(FILE_PATH))
            {
                gson.toJson(fullFile, writer);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static int getWarnings(long uid)
    {
        return warningData.getOrDefault(uid, Config.UNI_ZERO);
    }

    public static Map<Long, Integer> getWarningData()
    {
        return warningData;
    }
}