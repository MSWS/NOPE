package org.mswsplex.anticheat.checks.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks how many regen ticks within a time period
 * 
 * @author imodm
 * 
 * @deprecated Incompatible with 1.15.2 healing
 *
 */
public class Regen1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onHealthChange(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getRegainReason() != RegainReason.SATIATED)
			return;

		if (player.hasPotionEffect(PotionEffectType.SATURATION))
			return;

		List<Double> regenTimings = cp.getTempData("regenTimings", List.class);

		if (regenTimings == null)
			regenTimings = new ArrayList<>();

		regenTimings.add((double) System.currentTimeMillis());

		regenTimings = regenTimings.stream().filter((d) -> System.currentTimeMillis() - d < 5000)
				.collect(Collectors.toList());

		cp.setTempData("regenTimings", regenTimings);

		if (regenTimings.size() <= 2)
			return;

		cp.flagHack(this, (regenTimings.size() - 2) * 10, "Ticks: &e" + regenTimings.size() + "&7 > &a2");
	}

	@Override
	public String getCategory() {
		return "Regen";
	}

	@Override
	public String getDebugName() {
		return "Regen#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
