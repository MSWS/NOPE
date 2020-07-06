package xyz.msws.nope.modules.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.PlayerManager;
import xyz.msws.nope.utils.MSG;

/**
 * Responsible for decrementing player VLs after a certain period has been
 * specified. See config for details.
 * 
 * @author imodm
 *
 *         # VL Decrement is responsible for decrementing VL levels for players
 *         that haven't flagged for a while # The key is the time in
 *         milliseconds since the player last flagged for a hack. For example #
 *         if a player hasn't flagged for 20000 milliseconds, their VL will be
 *         decreased by 2 every second VLDecrement: Enabled: true Rate: 20 #
 *         Ticks Periods: 1000: 0 10000: 1 20000: 2 60000: 5 120000: 10
 *
 */
public class VLDecrementer extends AbstractModule {

	private Map<Long, Integer> values = new HashMap<>();
	private long period = 20;
	private PlayerManager pmanager;

	public VLDecrementer(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		if (!plugin.getConfig().getBoolean("VLDecrement.Enabled", true))
			return;
		pmanager = plugin.getModule(PlayerManager.class);
		period = plugin.getConfig().getLong("VLDecrement.Period", 20);
		if (!plugin.getConfig().isConfigurationSection("VLDecrement")) {
			MSG.warn("VLDecrement is missing! Using defaults.");
			values.put(10000L, 1);
			values.put(20000L, 2);
			values.put(60000L, 5);
		} else {
			for (Entry<String, Object> entry : plugin.getConfig().getConfigurationSection("VLDecrement.Periods")
					.getValues(false).entrySet()) {
				// IsNumber instead of IsDigits to allow negatives
				if (!NumberUtils.isNumber(entry.getKey())) {
					MSG.warn("Invalid decrement key: " + entry.getKey());
					continue;
				}
				if (!NumberUtils.isNumber(entry.getValue() + "")) {
					MSG.warn("Invalid decrement value: " + entry.getValue());
					continue;
				}

				values.put(Long.parseLong(entry.getKey()), Integer.parseInt(entry.getValue() + ""));
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for (UUID uuid : pmanager.getLoadedPlayers()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					CPlayer cp = pmanager.getPlayer(uuid);
					ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
					if (vlSection == null)
						continue;

					long lastFlag = cp.timeSince(Stat.FLAGGED);

					int diff = 1;

					for (Entry<Long, Integer> entry : values.entrySet()) {
						if (lastFlag < entry.getKey())
							break;
						if (entry.getKey() == -1) {
							diff = entry.getValue();
							break;
						}
						diff = entry.getValue();
					}

					for (String hack : vlSection.getKeys(false)) {
						if (cp.getSaveData("vls." + hack, Integer.class) == 0)
							continue;
						cp.setSaveData("vls." + hack, cp.getSaveData("vls." + hack, Integer.class) - diff);
						if (cp.getSaveData("vls." + hack, Integer.class) < 0)
							cp.setSaveData("vls." + hack, 0);
						MSG.sendPluginMessage(null, "setvl:" + player.getName() + " " + hack + " "
								+ cp.getSaveData("vls." + hack, Integer.class));
					}
				}
			}
		}.runTaskTimer(plugin, 0, period);
	}

	@Override
	public void disable() {
	}

}
