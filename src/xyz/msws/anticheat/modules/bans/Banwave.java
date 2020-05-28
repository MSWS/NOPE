package xyz.msws.anticheat.modules.bans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;
import xyz.msws.anticheat.utils.MSG;

/**
 * Responsible for banning a list of players on a timer.
 * 
 * @author imodm
 *
 */
public class Banwave extends AbstractModule {

	private long lastBanwave;

	private BukkitTask bw;

	public Banwave(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, BanwaveInfo> toBan = new HashMap<>();

	public BukkitRunnable runBanwave(boolean forced) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, BanwaveInfo> entry : toBan.entrySet()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
					plugin.getModule(BanHook.class).ban(player.getUniqueId(), entry.getValue().getReason(),
							entry.getValue().getDuration());
				}
				toBan.clear();
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

	/**
	 * Simple encapsulation of data to avoid excessive maps
	 * 
	 * @author imodm
	 *
	 */
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

	@Override
	public void enable() {
		if (plugin.getConfig().getInt("BanwaveRate", -1) == -1) {
			MSG.log("Banwaves are disabled, reset your config if this is not intended.");
			return;
		}
		bw = runBanwave(false).runTaskTimer(this.plugin, 0, plugin.getConfig().getInt("BanwaveRate"));
	}

	@Override
	public void disable() {
		if (bw != null)
			bw.cancel();
	}
}
