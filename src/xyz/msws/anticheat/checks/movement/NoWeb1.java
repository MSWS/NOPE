package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Checks the speed in a snapshot of time
 * 
 * @author imodm
 *
 */
public class NoWeb1 implements Check, Listener {

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
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.FLYING) < 500)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500)
			return;

		if (player.getLocation().getBlock().getType() != Material.COBWEB)
			return;

		double diff = event.getTo().distanceSquared(event.getFrom());

		if (diff < .012)
			return;

		cp.flagHack(this, (int) Math.round((diff / .013) * 50.0), "Diff: &e" + diff + "&7 >= &a.012");
	}

	@Override
	public String getCategory() {
		return "NoWeb";
	}

	@Override
	public String getDebugName() {
		return "NoWeb#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
