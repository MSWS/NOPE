package xyz.msws.nope.modules.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

/**
 * The general VL Scoreboard to show VLs
 * 
 * @author imodm
 *
 */
public class VLScoreboard extends CScoreboard {

	public VLScoreboard(NOPE plugin, Player player) {
		super(plugin, player);
		setTitle(MSG.color("&c&l[&4&lNOPE&c&l]"));
	}

	@Override
	public void onTick() {
		lines.clear();

		Map<UUID, Integer> vls = new HashMap<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			CPlayer cp = plugin.getCPlayer(player);
			String highest = cp.getHighestHack();
			int vl = cp.getVL(highest);
			if (vl == 0)
				continue;
			vls.put(player.getUniqueId(), vl);
		}

		vls = sortByValue(vls);
		lines.add("");

		lines.add("&9" + MSG.getTime(plugin.getModule(Banwave.class).timeToNextBanwave()));
		lines.add("&bBanwave in...");
		lines.add("");

		int i = 0;
		for (Entry<UUID, Integer> entry : vls.entrySet()) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());
			CPlayer cp = plugin.getCPlayer(p);
			String line = "&7" + p.getName() + " ";
			line += "&e" + cp.getHighestHack() + " &c" + entry.getValue();
			lines.add(line);
			if (i > 10)
				break;
			i++;
		}
	}

	private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
}
