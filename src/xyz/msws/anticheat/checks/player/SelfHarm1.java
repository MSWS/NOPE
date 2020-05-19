package xyz.msws.anticheat.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * 
 * This hack is COMPLETELY useless, why would you ever use this
 * 
 * @author imodm
 * 
 */
public class SelfHarm1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.getFallDistance() == 0)
			return;
		if (!player.isOnGround())
			return;

		Location lastSafe = cp.getLastSafeLocation();

		double yDiff = lastSafe.getY() - player.getLocation().getY();

		if (yDiff > 0)
			return;

		cp.flagHack(this, plugin.getConfig().getInt("VlForBanwave"), "YDiff: &e" + yDiff);
	}

	@Override
	public String getCategory() {
		return "SelfHarm";
	}

	@Override
	public String getDebugName() {
		return "SelfHarm#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
