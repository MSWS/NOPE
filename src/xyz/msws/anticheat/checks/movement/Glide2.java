package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;

/**
 * TODO
 * 
 * @author imodm
 *
 */
public class Glide2 implements Check, Listener {

	@SuppressWarnings("unused")
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
//		Player player = event.getPlayer();
//		CPlayer cp = plugin.getCPlayer(player);
	}

	@Override
	public String getCategory() {
		return "Glide";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
