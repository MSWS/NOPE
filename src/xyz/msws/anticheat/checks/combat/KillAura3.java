package xyz.msws.anticheat.checks.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * 
 * 
 * @author imodm
 * 
 * @deprecated
 *
 */
public class KillAura3 implements Check, Listener {

	private final int SIZE = 20;

	private NOPE plugin;

	private Map<UUID, List<Double>> offsets = new HashMap<>();

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		double off = getDotDiff(player, event.getEntity().getLocation());

		List<Double> values = offsets.getOrDefault(player.getUniqueId(), new ArrayList<>());

		values.add(0, off);

		if (values.size() > SIZE)
			values = values.subList(0, SIZE);

//		cp.setTempData("KillAuraOffsets", values);
		offsets.put(player.getUniqueId(), values);

		if (values.size() < SIZE)
			return;

		double avg = 0;
		for (double v : values)
			avg += v;
		avg /= values.size();
		if (avg > .3)
			return;

		cp.flagHack(this, (int) ((1 - off) * 50), String.format("Avg: &e%.2f", avg));
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#3";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	private double getDotDiff(Player player, Location target) {
		Location eye = player.getLocation();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());
		return dot;
	}
}
