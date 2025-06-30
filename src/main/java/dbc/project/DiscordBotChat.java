package dbc.project;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.Objects;
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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("dbc")
					.requires(source -> source.hasPermissionLevel(2)) // Solo ops
					.then(CommandManager.literal("announ")
							.then(CommandManager.literal("set")
									.then(CommandManager.argument("value", BoolArgumentType.bool())
											.executes(context -> {
												boolean value = BoolArgumentType.getBool(context, "value");
												String valueString = String.valueOf(value);
												context.getSource().sendFeedback(
														() -> Text.literal("announ set a " + value), false
												);
												config.setPlayer_join_announcement(valueString, configFile);
												System.out.println("Announ set a " + valueString);
												return 1;
											})
									)
							)
					)
					.then(CommandManager.literal("message")
							.then(CommandManager.literal("set")
									.then(CommandManager.argument("value", BoolArgumentType.bool())
											.executes(context -> {
												boolean value = BoolArgumentType.getBool(context, "value");
												String valueString = String.valueOf(value);
												context.getSource().sendFeedback(
														() -> Text.literal("message set a " + value), false
												);
												config.setPlayer_message_announcement(valueString, configFile);
												System.out.println("Message set a " + valueString);
												return 1;
											})
									)
							)
					)
			);
		});

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			if (config.player_message_announcement.equals("true")) {
				String playerName = sender.getDisplayName().getString();
				String playerMessage = message.getContent().getString();

				String endpoint = config.endpoint.startsWith("/") ? config.endpoint : "/" + config.endpoint;
				String url = String.format("http://%s:%d%s", config.ip, config.port, endpoint);

				executor.submit(() -> {
					sendPost(url, playerMessage, playerName);
				});
			}
		});


		ServerPlayConnectionEvents.JOIN.register((message, sender, player) -> {
			if (config.player_join_announcement.equals("true")) {
				String endpoint = config.announcement_endpoint.startsWith("/") ? config.announcement_endpoint : "/" + config.announcement_endpoint;
				String url = String.format("http://%s:%d%s", config.ip, config.port, endpoint);
				String playerName = message.getPlayer().getDisplayName().getString();

				executor.submit(() -> {
					sendAnnouncement(url, "true", playerName);
				});

			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((message, sender) -> {
			if (config.player_join_announcement.equals("true")) {
				String endpoint = config.announcement_endpoint.startsWith("/") ? config.announcement_endpoint : "/" + config.announcement_endpoint;
				String url = String.format("http://%s:%d%s", config.ip, config.port, endpoint);
				String playerName = message.getPlayer().getDisplayName().getString();

				executor.submit(() -> {
					sendAnnouncement(url, "false", playerName);
				});
			}
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

	public static void sendAnnouncement(String urlStr, String status, String playerName) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");

			String jsonInputString = "{\"username\":\""+playerName+"\",\"status\":\"" + status + "\"}";

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int code = con.getResponseCode();
			System.out.println("POST send. Response Code: " + code);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

}