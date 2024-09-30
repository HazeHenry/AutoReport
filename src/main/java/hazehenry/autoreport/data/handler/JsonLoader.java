package hazehenry.autoreport.data.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hazehenry.autoreport.AutoReport;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class JsonLoader {

    @Getter
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static <T> T loadOrDefault(File folder, String name, Class<T> configType) {
        File configFile = new File(folder, name);
        if (configFile.exists())
            return loadConfig(folder, name, configType);

        T config;

        try {
            config = configType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            AutoReport.getInstance().getLogger().severe("Failed to do something");
            e.printStackTrace();
            return null;
        }

        try {
            if (!folder.exists())
                folder.mkdirs();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            gson.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            AutoReport.getInstance().getLogger().severe("Failed to create new json file");
            e.printStackTrace();
        }

        return config;
    }

    public static <T> T loadConfig(File folder, String name, Class<T> configType) {
        try {
            File configFile = new File(folder, name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
            T type = gson.fromJson(reader, configType);
            reader.close();
            return type;
        } catch (IOException e) {
            AutoReport.getInstance().getLogger().severe("Failed to load json");
            e.printStackTrace();
            return null;
        }
    }

    public static void saveConfig(File folder, String name, Object config) {
        try {
            File configFile = new File(folder, name);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            gson.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            AutoReport.getInstance().getLogger().severe("Failed to save json");
            e.printStackTrace();
        }
    }
}