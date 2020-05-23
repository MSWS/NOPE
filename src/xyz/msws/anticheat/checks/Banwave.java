package xyz.msws.anticheat.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.utils.MSG;

public class Banwave {

	private NOPE plugin;

	private long lastBanwave;

	public Banwave(NOPE plugin) {
		this.plugin = plugin;
		if (plugin.getConfig().getInt("BanwaveRate", -1) == -1) {
			MSG.log("Banwaves are disabled, reset your config if this is not intended.");
			return;
		}
		runBanwave(false).runTaskTimer(this.plugin, 0, plugin.getConfig().getInt("BanwaveRate"));
	}

	private Map<UUID, BanwaveInfo> toBan = new HashMap<>();

	public BukkitRunnable runBanwave(boolean forced) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, BanwaveInfo> entry : toBan.entrySet()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
					plugin.getBanManager().ban(player.getUniqueId(), entry.getValue().getReason(),
							entry.getValue().getDuration());
				}
				if (!forced)
					lastBanwave = System.currentTimeMillis();
			}
		};
	}

	public BanwaveInfo addPlayer(UUID player, BanwaveInfo info) {
		return toBan.put(player, info);
	}

	public boolean isInBanwave(UUID player) {
		return toBan.containsKey(player);
	}

	public BanwaveInfo removePlayer(UUID player) {
		return toBan.remove(player);
	}

	public Map<UUID, BanwaveInfo> getInBanwave() {
		return toBan;
	}

	public long timeToNextBanwave() {
		return plugin.getConfig().getInt("BanwaveRate") * 50 + lastBanwave - System.currentTimeMillis();
	}

	public class BanwaveInfo {
		private UUID uuid;
		private String reason;
		private long duration;

		public BanwaveInfo(UUID uuid, String reason, long duration) {
			this.uuid = uuid;
			this.reason = reason;
			this.duration = duration;
		}

		public UUID getUuid() {
			return uuid;
		}

		public void setUuid(UUID uuid) {
			this.uuid = uuid;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public long getDuration() {
			return duration;
		}

		public void setDuration(long duration) {
			this.duration = duration;
		}
	}
}
