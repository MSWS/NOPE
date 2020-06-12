package xyz.msws.nope.checks.combat.killaura;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * This KillAura check compares if the player perfectly attacks as their attack
 * cooldown expires
 * 
 * @author imodm
 *
 */
public class KillAura2 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	private Map<UUID, List<Long>> timings = new HashMap<>();
	private Map<UUID, Long> next = new HashMap<>();

	private static Method cMethod;
	private static Method getHandle;

	private final int SIZE = 10;

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();

		if (event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK)
			return;

		float cd = 0;

		try {
			if (getHandle == null || cMethod == null) {
				getHandle = player.getClass().getMethod("getHandle");
				getHandle.setAccessible(true);
				Object entityPlayer = getHandle.invoke(player);

				cMethod = entityPlayer.getClass().getMethod("getCooldownPeriod");
				cMethod.setAccessible(true);
			}
			Object entityPlayer = getHandle.invoke(player);

			Object cooldown = cMethod.invoke(entityPlayer);
			cd = (float) cooldown;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return;
		}

		if (!next.containsKey(player.getUniqueId())) {
			next.put(player.getUniqueId(), System.currentTimeMillis() + (long) cd * 50);
			return;
		}

		List<Long> times = timings.getOrDefault(player.getUniqueId(), new ArrayList<>());
		times.add(0, System.currentTimeMillis() - next.get(player.getUniqueId()));
		times = times.subList(0, Math.min(times.size(), SIZE));

		timings.put(player.getUniqueId(), times);
		next.put(player.getUniqueId(), System.currentTimeMillis() + (long) cd * 50);

		if (times.size() < SIZE)
			return;

		double avg = 0;
		for (long l : times)
			avg += l;
		avg /= times.size();

		double avd = 0;
		for (long l : times)
			avd += Math.abs(avg - l);
		avd /= times.size();

		if (avd > 20)
			return;

		CPlayer cp = plugin.getCPlayer(player);
		cp.flagHack(this, 10, "Average: &e" + avg + "&7\nAVD: &a" + avd);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
