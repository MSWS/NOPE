package xyz.msws.anticheat.checks.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Checks timings between clicks, does some really fancy logic to compare double
 * clicks
 * 
 * @author imodm
 *
 */
public class ChestStealer1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, List<Long>> clicks = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (event.getInventory() == null)
			return;

		if (event.getInventory().getType() == InventoryType.PLAYER)
			return;

		List<Long> clickTimings = clicks.getOrDefault(player.getUniqueId(), new ArrayList<>());

		double cDiff = System.currentTimeMillis();

		if (!clickTimings.isEmpty())
			cDiff = System.currentTimeMillis() - clickTimings.get(0);

		clickTimings.add(0, System.currentTimeMillis());

		for (int i = SIZE; i < clickTimings.size(); i++)
			clickTimings.remove(i);

		clicks.put(player.getUniqueId(), clickTimings);
		double last = System.currentTimeMillis();

		int amo = 0;

		for (int i = 0; i < clickTimings.size(); i++) {
			double diff = last - clickTimings.get(i);
			if (cDiff == diff && (event.getClick() != ClickType.SHIFT_LEFT || diff <= 10))
				amo++;
			last = clickTimings.get(i);
		}

		if (amo <= SIZE / 4)
			return;

		cp.flagHack(this, (amo - (SIZE / 4)) * 5 + 10, "Amo: &e" + amo + "&7 > &a" + (SIZE / 4));

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
