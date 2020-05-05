package org.mswsplex.anticheat.checks.tick;

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
 * Checks how recently the last regen tick was
 * 
 * @author imodm
 * 
 * @deprecated Incompatible with 1.15.2 healing
 *
 */
public class Regen2 implements Check, Listener {

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

		double last = cp.timeSince("lastRegen");
		cp.setTempData("lastRegen", (double) System.currentTimeMillis());

		if (last > 3500)
			return;

		cp.flagHack(this, (int) Math.round(3500 - last) / 50 + 5, "LastRegen: &e" + last + "&7 <= &a3500");
	}

	@Override
	public String getCategory() {
		return "Regen";
	}

	@Override
	public String getDebugName() {
		return "Regen#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
