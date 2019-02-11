package org.mswsplex.anticheat.checks.client;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Checks timings between clicks, does some really fancy logic to compare double
 * clicks
 * 
 * @author imodm
 *
 */
public class ChestStealer1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getInventory() == null)
			return;

		if (event.getInventory().getType() == InventoryType.PLAYER && player.getGameMode() == GameMode.CREATIVE)
			return;

		List<Double> clickTimings = cp.getTempData("clickTimings", List.class);

		if (clickTimings == null)
			clickTimings = new ArrayList<>();

		double cDiff = System.currentTimeMillis();

		if (!clickTimings.isEmpty())
			cDiff = System.currentTimeMillis() - clickTimings.get(0);

		clickTimings.add(0, (double) System.currentTimeMillis());

		for (int i = SIZE; i < clickTimings.size(); i++)
			clickTimings.remove(i);

		cp.setTempData("clickTimings", clickTimings);
		double last = System.currentTimeMillis();

		int amo = 0;

		for (int i = 0; i < clickTimings.size(); i++) {
			double diff = last - clickTimings.get(i);
			if (cDiff == diff && (event.getClick() != ClickType.SHIFT_LEFT || diff != 0))
				amo++;
			last = clickTimings.get(i);
		}

		if (amo <= SIZE / 4)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&esame: " + amo);

		cp.flagHack(this, (amo - (SIZE / 4)) * 5 + 10);

	}

	@Override
	public String getCategory() {
		return "ChestStealer";
	}

	@Override
	public String getDebugName() {
		return "ChestStealer#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
