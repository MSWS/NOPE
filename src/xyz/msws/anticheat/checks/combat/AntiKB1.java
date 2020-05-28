package xyz.msws.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.animations.AnimationManager;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Basically {@link NoFall} but when the player hits an entity
 * 
 * @author imodm
 *
 */
public class AntiKB1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();

		CPlayer cp = plugin.getCPlayer(player);

		if (plugin.getModule(AnimationManager.class).isInAnimation(player))
			return;

		if (player.isDead())
			return;

		if (player.isBlocking())
			return;

		if (player.isInsideVehicle())
			return;

		if (cp.isBlockAbove())
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (cp.isBlockNearby(Material.COBWEB))
			return;

		Location origin = player.getLocation();

		if (plugin.getTPS() < 18)
			return;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (player.isDead())
					return;
				if (!origin.getWorld().equals(player.getWorld()))
					return;
				double dist = player.getLocation().distanceSquared(origin);
				if (dist > 0)
					return;
				if (plugin.getTPS() < 15)
					return;
				cp.flagHack(AntiKB1.this, 10, "TPS: &e" + plugin.getTPS());
			}
		}.runTaskLater(plugin, 5);
	}

	@Override
	public String getCategory() {
		return "AntiKB";
	}

	@Override
	public String getDebugName() {
		return "AntiKB#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
