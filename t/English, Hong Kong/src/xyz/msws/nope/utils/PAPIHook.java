package xyz.msws.nope.utils;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;

public class PAPIHook {
	public String setPlaceholders(OfflinePlayer player, String string) {
		return PlaceholderAPI.setPlaceholders(player, string);
	}
}
