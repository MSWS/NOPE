package xyz.msws.anticheat.bans;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.maxgamer.maxbans.MaxBans;

public class MaxBansHook implements BanHook {
	private MaxBans bans;
	private org.maxgamer.maxbans.banmanager.BanManager manager;

	public MaxBansHook() {
		bans = (MaxBans) Bukkit.getPluginManager().getPlugin("MaxBans");
		manager = bans.getBanManager();
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		reason = reason == null ? "Hacking" : reason;
		if (time == -1) {
			manager.ban(Bukkit.getOfflinePlayer(player).getName(), reason, "NOPE");
			return;
		}
		manager.tempban(Bukkit.getOfflinePlayer(player).getName(), reason, "NOPE", System.currentTimeMillis() + time);
	}
}
