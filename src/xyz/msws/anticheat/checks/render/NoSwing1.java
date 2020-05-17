package xyz.msws.anticheat.checks.render;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Checks if a player hasn't sent a swing packet before interaction event
 * 
 * @author imodm
 * 
 * @deprecated
 *
 */
public class NoSwing1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getAction() == Action.RIGHT_CLICK_AIR)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.isBlockInHand())
			return;

		if (event.getAction() == Action.PHYSICAL)
			return;

		if (cp.timeSince("lastSwing") < 1000)
			return;

		cp.flagHack(this, 50, "LastSwing: &e" + cp.timeSince("lastSwing") + "&7 >= &a1000");
	}

	@Override
	public String getCategory() {
		return "NoSwing";
	}

	@Override
	public String getDebugName() {
		return "NoSwing#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
