package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.data.CPlayer;

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

		if (cp.timeSince(Stat.TELEPORT) < 1000)
			return;
		if (cp.timeSince(Stat.IN_LIQUID) < 1000)
			return;
		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 300)
			return;
		if (cp.timeSince(Stat.HORIZONTAL_BLOCKCHANGE) > 500)
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
