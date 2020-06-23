package xyz.msws.nope.modules.bans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.actions.actions.BanAction;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.utils.MSG;

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

	public BukkitRunnable runBanwave() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, BanwaveInfo> entry : toBan.entrySet()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
					BanAction action = new BanAction(plugin, entry.getValue().getDuration(),
							entry.getValue().getReason());
					action.execute(player, entry.getValue().getCheck());
				}
				toBan.clear();
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
		private Check check;
		private long duration;
		private String reason;

		public BanwaveInfo(UUID uuid, Check check, long duration, String reason) {
			this.uuid = uuid;
			this.check = check;
			this.duration = duration;
			this.reason = reason;
		}

		public UUID getUuid() {
			return uuid;
		}

		public void setUuid(UUID uuid) {
			this.uuid = uuid;
		}

		public Check getCheck() {
			return check;
		}

		public void setCheck(Check check) {
			this.check = check;
		}

		public long getDuration() {
			return duration;
		}

		public void setDuration(long duration) {
			this.duration = duration;
		}

		public String getReason() {
			return reason;
		}
	}

	@Override
	public void enable() {
		if (plugin.getConfig().getInt("BanwaveRate", -1) == -1) {
			MSG.log("Banwaves are disabled, reset your config if this is not intended.");
			return;
		}
		bw = runBanwave().runTaskTimer(this.plugin, 0, plugin.getConfig().getInt("BanwaveRate"));
	}

	@Override
	public void disable() {
		if (bw != null && !bw.isCancelled())
			bw.cancel();
		runBanwave();
		toBan.clear();
	}
}
