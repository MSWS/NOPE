package xyz.msws.nope.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

public class NoFall1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, Double> highest = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Location loc = player.getLocation();
		Vector vel = player.getVelocity();

		if (cp.timeSince(Stat.COBWEB) < 100 || player.isGliding()) {
			highest.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 1000)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (vel.getY() >= 0 || player.isFlying()) {
			if (highest.getOrDefault(player.getUniqueId(), 0d) < loc.getY())
				highest.put(player.getUniqueId(), loc.getY());
			return;
		}

		if (player.isOnGround()) {
			if (!highest.containsKey(player.getUniqueId()))
				return;

			double dist = highest.get(player.getUniqueId()) - loc.getY();
			double diff = (highest.get(player.getUniqueId()) - loc.getY()) - player.getFallDistance();
			highest.put(player.getUniqueId(), loc.getY());

			if (diff < .3)
				return;

			cp.flagHack(this, Math.min((int) Math.abs(diff * 10) + 5, 30),
					String.format("Expected: &e%.3f&7\nReceived: &a%.3f", dist, player.getFallDistance()));
			return;
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		highest.put(player.getUniqueId(), player.getLocation().getY());
	}

	@EventHandler
	public void onSwap(PlayerChangedWorldEvent event) {
		highest.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public String getCategory() {
		return "NoFall";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
