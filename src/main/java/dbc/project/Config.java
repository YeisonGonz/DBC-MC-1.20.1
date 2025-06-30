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
    public String player_message_announcement = "true";

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

    public void save(File configFile) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlayer_join_announcement(String player_join_announcement, File configFile) {
        this.player_join_announcement = player_join_announcement;
        save(configFile);
    }

    public void setPlayer_message_announcement(String player_message_announcement, File configFile) {
        this.player_message_announcement = player_message_announcement;
        save(configFile);
    }
}
