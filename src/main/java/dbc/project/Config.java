package dbc.project;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Config {
    public String ip = "127.0.0.1";
    public int port = 5000;
    public String endpoint = "send";
    public String announcement_endpoint = "/announ";
    public String player_join_announcement = "true";

    public static Config load(File configFile) {
        Gson gson = new Gson();
        try {
            if (!configFile.exists()) {
                Config defaultConfig = new Config();
                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(defaultConfig, writer);
                }
                return defaultConfig;
            }
            try (FileReader reader = new FileReader(configFile)) {
                return gson.fromJson(reader, Config.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Config();
        }
    }
}
