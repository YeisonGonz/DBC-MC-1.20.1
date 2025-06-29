package dbc.project;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordBotChat implements ModInitializer {
	private static final ExecutorService executor = Executors.newFixedThreadPool(4);
	public static final String MOD_ID = "discordbotchat";
	public static Config config;

	@Override
	public void onInitialize() {
		File configFile = new File("config/discord_bridge.json");
		config = Config.load(configFile);

		System.out.println("Discord API IP: " + config.ip);
		System.out.println("Discord API Port: " + config.port);

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			String playerName = sender.getDisplayName().getString();
			String playerMessage = message.getContent().getString();

			String endpoint = config.endpoint.startsWith("/") ? config.endpoint : "/" + config.endpoint;
			String url = String.format("http://%s:%d%s", config.ip, config.port, endpoint);

			executor.submit(() -> {
				sendPost(url, playerMessage, playerName);
			});
		});
	}

	public static void sendPost(String urlStr, String message, String playerName) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");

			String jsonInputString = "{\"username\":\""+playerName+"\",\"content\":\"" + message + "\"}";

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int code = con.getResponseCode();
			System.out.println("POST send. Response Code: " + code);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}