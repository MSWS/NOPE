package xyz.msws.nope.modules.actions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

/**
 * Object for sending webhook messages
 * 
 * @author imodm
 *
 */
public class Webhook {

	private URL url;
	private Map<String, Object> data = new HashMap<>();
	private NOPE plugin;

	public Webhook(NOPE plugin, String url) {
		this.plugin = plugin;

		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Webhook(NOPE plugin, String url, String name) {
		this(plugin, url);
	}

	public void setAttribute(String key, Object value) {
		data.put(key, value);
	}

	public void setUsername(String name) {
		data.put("username", name);
	}

	public void setAvatarURL(String url) {
		data.put("avatar_url", url);
	}

	public Webhook(NOPE plugin, ConfigurationSection config) {
		if (!config.isSet("URL")) {
			MSG.error("Invalid webhook config for " + config.getName() + ", missing URL value.");
			return;
		}
		this.plugin = plugin;
		if (config.getString("URL").equals("https://discordapp.com/api/webhooks/"))
			return;

		data.put("username", config.getString("username", "NOPE"));
		data.put("avatar_url", config.getString("avatar_url", "https://i.imgur.com/U33fgPg.png"));

		if (config.contains("embeds")) {
			JSONArray embedContainer = new JSONArray();
			JSONObject embed = new JSONObject();

			if (config.isSet("embeds.author")) {
				JSONObject author = new JSONObject();
				author.put("name", config.getString("embeds.author.name", "NOPE"));
				author.put("url", config.getString("embeds.author.url", ""));
				author.put("icon_url", config.getString("embeds.author.icon_url", "https://i.imgur.com/U33fgPg.png"));
				embed.put("author", author);
			}

			if (config.isSet("embeds.title"))
				embed.put("title", config.getString("embeds.title", "NOPE Anti-Cheat"));
			if (config.isSet("embeds.url"))
				embed.put("url", config.getString("embeds.url"));
			if (config.isSet("embeds.description"))
				embed.put("description", config.getString("embeds.description"));
			if (config.isSet("embeds.color"))
				embed.put("color", config.getString("embeds.color"));
			if (config.isSet("embeds.footer")) {
				JSONObject footer = new JSONObject();
				footer.put("text", config.getString("embeds.footer.text"));
				if (config.isSet("embeds.footer.icon_url"))
					footer.put("icon_url", config.getString("embeds.footer.icon_url"));
				embed.put("footer", footer);
			}
			if (config.isSet("embeds.thumbnail")) {
				JSONObject thumb = new JSONObject();
				thumb.put("url", config.getString("embeds.thumbnail.url"));
				embed.put("thumbnail", thumb);
			}
			if (config.isSet("embeds.image")) {
				JSONObject image = new JSONObject();
				image.put("url", config.getString("embeds.image.url"));
				embed.put("image", image);
			}
			if (config.isConfigurationSection("embeds.fields")) {
				ConfigurationSection fields = config.getConfigurationSection("embeds.fields");
				JSONArray fieldArray = new JSONArray();
				for (String f : fields.getKeys(false)) {
					JSONObject field = new JSONObject();
					field.put("name", fields.getString(f + ".name"));
					field.put("value", fields.getString(f + ".value"));
					field.put("inline", fields.getBoolean(f + ".inline", true));
					fieldArray.put(field);
				}
				embed.put("fields", fieldArray);
			}

			embedContainer.put(embed);
			data.put("embeds", embedContainer);
		}

		try {
			this.url = new URL(config.getString("URL"));
		} catch (MalformedURLException e) {
			MSG.error("Invalid webhook config for " + config.getName() + ", invalid URL value.");
			e.printStackTrace();
		}
	}

	/**
	 * Asynchronously sends a POST request to the specified URL
	 * 
	 * @param content
	 * @param cp
	 * @param check
	 */
	public void sendMessage(String content, CPlayer cp, Check check) {
		if (url == null || url.toString().equals("https://discordapp.com/api/webhooks/"))
			return;
		data.put("content", content);
		if (!plugin.isEnabled())
			return;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					URLConnection con = url.openConnection();
					HttpURLConnection http = (HttpURLConnection) con;
					http.setRequestMethod("POST");
					http.setDoOutput(true);

					String d = new JSONObject(data).toString();
					d = MSG.replaceCheckPlaceholder(d, cp, check);

					byte[] out = d.getBytes(StandardCharsets.UTF_8);
					int length = out.length;

					http.setFixedLengthStreamingMode(length);
					http.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
					http.addRequestProperty("User-Agent", "Java-NOPEWebhook");
					http.connect();
					try (OutputStream os = http.getOutputStream()) {
						os.write(out);
					}
					if (http.getResponseCode() != 204) {
						MSG.warn("Got response code " + http.getResponseCode() + " when sending webhook.");
						MSG.warn("Message: &e" + http.getResponseMessage());
					}
				} catch (IOException e) {
					MSG.error("An error occured sending the webhook message");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	/**
	 * Asynchronously sends a POST request to the specified URL
	 * 
	 * @param content
	 * @param cp
	 * @param check
	 */
	public void sendMessage(String content) {
		if (url == null || url.toString().equals("https://discordapp.com/api/webhooks/"))
			return;
		data.put("content", content);
		if (!plugin.isEnabled())
			return;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					URLConnection con = url.openConnection();
					HttpURLConnection http = (HttpURLConnection) con;
					http.setRequestMethod("POST");
					http.setDoOutput(true);

					String d = new JSONObject(data).toString();

					byte[] out = d.getBytes(StandardCharsets.UTF_8);
					int length = out.length;

					http.setFixedLengthStreamingMode(length);
					http.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
					http.addRequestProperty("User-Agent", "Java-NOPEWebhook");
					http.connect();
					try (OutputStream os = http.getOutputStream()) {
						os.write(out);
					}
					if (http.getResponseCode() != 204) {
						MSG.warn("Got response code " + http.getResponseCode() + " when sending webhook.");
						MSG.warn("Message: &e" + http.getResponseMessage());
					}
				} catch (IOException e) {
					MSG.error("An error occured sending the webhook message");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);

	}
}
