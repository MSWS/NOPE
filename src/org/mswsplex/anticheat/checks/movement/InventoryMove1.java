package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks for everytime the player clicks within their inventory are on the
 * ground and has moved
 * 
 * @author imodm
 *
 */
public class InventoryMove1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getClick() == ClickType.CREATIVE && event.getAction() == InventoryAction.PLACE_ALL)
			return;

		if (cp.timeSince("lastTeleport") < 1000)
			return;
		if (cp.timeSince("lastOnGround") > 100)
			return;
		if (cp.timeSince("lastLiquid") < 1000)
			return;
		if (cp.timeSince("lasstDamageTaken") < 300)
			return;

		event.setCancelled(true);

		cp.flagHack(this, 10);
	}

	@Override
	public String getCategory() {
		return "InventoryMove";
	}

	@Override
	public String getDebugName() {
		return "InventoryMove#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
