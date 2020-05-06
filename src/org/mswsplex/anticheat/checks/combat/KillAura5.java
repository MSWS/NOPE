package org.mswsplex.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * 
 * @author imodm
 *
 */
public class KillAura5 implements Check, Listener {

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

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.hasTempData("lastEntityHitDirection"))
			return;

		Location eLoc = player.getLocation(), packet = cp.getTempData("lastEntityHitDirection", Location.class);

		double diff = Math.abs(eLoc.getYaw() - packet.getYaw()) + Math.abs(eLoc.getPitch() - packet.getPitch());

		if (diff == 0)
			return;

		if (plugin.getTPS() < 18)
			return;

		cp.flagHack(this, (int) Math.round(diff * 5) + 20, "Invalid Packet Diff: &e" + diff);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#5";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
