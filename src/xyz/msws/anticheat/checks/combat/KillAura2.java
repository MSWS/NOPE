package xyz.msws.anticheat.checks.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * Compares yaw rates right before a player hits an entity
 * 
 * @author imodm
 *
 */
public class KillAura2 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	private Map<UUID, List<Double>> yaws = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		double yawDiff = Math.abs(Math.abs(event.getTo().getYaw()) - Math.abs(event.getFrom().getYaw()));

		if (yawDiff < 30)
			return;

		List<Double> fastTimings = yaws.getOrDefault(player.getUniqueId(), new ArrayList<>());

		fastTimings.add((double) System.currentTimeMillis());

		Iterator<Double> it = fastTimings.iterator();

		while (it.hasNext()) {
			double val = it.next();
			if (System.currentTimeMillis() - val > 250) {
				it.remove();
			}
		}
		yaws.put(player.getUniqueId(), fastTimings);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> fastTimings = yaws.getOrDefault(player.getUniqueId(), new ArrayList<>());

		Iterator<Double> it = fastTimings.iterator();

		while (it.hasNext()) {
			double val = it.next();
			if (System.currentTimeMillis() - val > 250) {
				it.remove();
			}
		}

		if (fastTimings.size() <= 4)
			return;

		cp.flagHack(this, 50, "Rotated too quickly: &e" + fastTimings.size());
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
