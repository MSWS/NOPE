package xyz.msws.nope.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;

public class Utils {
	public static NOPE plugin;

	private static Method getHandle;
	private static Field pingMethod;

	public static String nms;

	static {
		try {
			switch (Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().lastIndexOf("."))) {
				case "1.13-R0":
					nms = "v1_13_R1";
					break;
				case "1.13.2-R0":
				case "1.13.1-R0":
					nms = "v1_13_R2";
					break;
				case "1.14-R0":
				case "1.14.1-R0":
				case "1.14.2-R0":
				case "1.14.3-R0":
				case "1.14.4-R0":
					nms = "v1_14_R1";
					break;
				case "1.15-R0":
				case "1.15.1-R0":
				case "1.15.2-R0":
					nms = "v1_15_R1";
					break;
				case "1.16-R0":
				case "1.16.1-R0":
					nms = "v1_16_R1";
					break;
				case "1.16.2-R0":
					nms = "v1_16_R2";
					break;
				case "1.16.3-R0":
					nms = "v1_16_R2";
					break;
				default:
					MSG.warn("Unknown NMS version: " + Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().lastIndexOf(".")));
					break;
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public static int getPing(Player player) {
		try {
			if (getHandle == null) {
				getHandle = player.getClass().getMethod("getHandle");
				getHandle.setAccessible(true);
			}
			Object entityPlayer = getHandle.invoke(player);
			if (pingMethod == null) {
				pingMethod = entityPlayer.getClass().getField("ping");
				pingMethod.setAccessible(true);
			}
			return pingMethod.getInt(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public enum Age {
		OUTDATED_VERSION, DEVELOPER_VERSION, SAME_VERSION;
	}

	public static Age outdated(String v1, String v2) {
		int vnum1 = 0, vnum2 = 0;

		// loop untill both String are processed
		for (int i = 0, j = 0; (i < v1.length() || j < v2.length());) {
			// storing numeric part of version 1 in vnum1
			while (i < v1.length() && v1.charAt(i) != '.') {
				vnum1 = vnum1 * 10 + (v1.charAt(i) - '0');
				i++;
			}

			// storing numeric part of version 2 in vnum2
			while (j < v2.length() && v2.charAt(j) != '.') {
				vnum2 = vnum2 * 10 + (v2.charAt(j) - '0');
				j++;
			}

			if (vnum1 > vnum2)
				return Age.DEVELOPER_VERSION;
			if (vnum2 > vnum1)
				return Age.OUTDATED_VERSION;

			// if equal, reset variables and go for next numeric
			// part
			vnum1 = vnum2 = 0;
			i++;
			j++;
		}
		return Age.SAME_VERSION;
	}

	/**
	 * Uploads the text to hastebin and return the ID.
	 * 
	 * @param text
	 * @param raw
	 * @return
	 * @throws IOException
	 */
	public static String uploadHastebin(String text) throws IOException {
		if (Bukkit.isPrimaryThread())
			MSG.warn("Possible synchronous call to upload.");
		byte[] postData = text.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		String requestURL = "https://hastebin.com/documents";
		URL url = new URL(requestURL);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "Hastebin Java API");
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		conn.setUseCaches(false);

		String response = null;
		DataOutputStream wr;
		try {
			wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			response = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response == null)
			return null;

		if (response.contains("\"key\"")) {
			response = response.substring(response.indexOf(":") + 2, response.length() - 2);
			return response;
		}

		return response;
	}
}
