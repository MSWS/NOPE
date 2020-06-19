package xyz.msws.nope.checks.movement.flight;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * 
 * Checks boat fly
 * 
 * @author imodm
 * 
 */
public class Flight5 implements Check, Listener {

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

		if (!player.isInsideVehicle())
			return;

		if (player.getVehicle().getType() != EntityType.BOAT)
			return;

		if (player.getVehicle().getLocation().getBlock().getBlockData() instanceof Levelled)
			return;

		if (player.getVehicle().getLocation().clone().add(0, -1, 0).getBlock().getBlockData() instanceof Levelled)
			return;

		if (event.getFrom().getY() >= event.getTo().getY())
			return;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Block block = player.getVehicle().getLocation().clone().add(x, -1, z).getBlock();
				BlockData data = block.getBlockData();
				if (data instanceof Levelled)
					return;
			}
		}

		cp.flagHack(this, 20);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#5";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
