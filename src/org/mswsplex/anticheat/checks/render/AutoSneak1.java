package org.mswsplex.anticheat.checks.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class AutoSneak1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> sneakTimings = (List<Double>) cp.getTempData("autoSneakTimings");
		if (sneakTimings == null)
			sneakTimings = new ArrayList<>();

		sneakTimings.add((double) System.currentTimeMillis());
		Iterator<Double> it = sneakTimings.iterator();
		while (it.hasNext()) {
			double d = it.next();
			if (System.currentTimeMillis() - d > 1000) {
				it.remove();
				continue;
			}
		}

		cp.setTempData("autoSneakTimings", sneakTimings);

		if (sneakTimings.size() < 20)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&csneak times: " + sneakTimings.size());
		cp.flagHack(this, (sneakTimings.size() - 20) * 2);
	}

	@Override
	public String getCategory() {
		return "AutoSneak";
	}

	@Override
	public String getDebugName() {
		return "AutoSneak#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
