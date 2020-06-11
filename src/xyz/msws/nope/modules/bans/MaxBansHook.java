package xyz.msws.nope.modules.bans;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.maxgamer.maxbans.MaxBans;

import xyz.msws.nope.NOPE;

public class MaxBansHook extends BanHook {
	private MaxBans bans;
	private org.maxgamer.maxbans.banmanager.BanManager manager;

	public MaxBansHook(NOPE plugin) {
		super(plugin);
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

	@Override
	public int bans(UUID player) {
		return MaxBans.instance.getBanManager()
				.getHistory(Bukkit.getOfflinePlayer(player).getName().toLowerCase()).length;
	}
}
