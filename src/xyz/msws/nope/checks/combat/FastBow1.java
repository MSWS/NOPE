package xyz.msws.nope.checks.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Gets the average velocities of arrows shot within 100 ticks and compares them
 * to a regular max value
 * 
 * @author imodm
 *
 */
public class FastBow1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private Map<UUID, Double> velocities = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				velocities.put(player.getUniqueId(), 0d);
			}
		}, 0, 100);
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (event.getEntity() == null || event.getEntity().getShooter() == null
				|| !(event.getEntity().getShooter() instanceof Player))
			return;
		if (event.getEntityType() != EntityType.ARROW)
			return;

		Player player = (Player) event.getEntity().getShooter();
		CPlayer cp = plugin.getCPlayer(player);

		double v = velocities.getOrDefault(player.getUniqueId(), 0d);

		if (v > 50)
			cp.flagHack(this, (int) (v - 50) * 3, "Velocities: &e" + v);

		double vel = event.getEntity().getVelocity().lengthSquared();

		velocities.put(player.getUniqueId(), v + vel);
	}

	@Override
	public String getCategory() {
		return "FastBow";
	}

	@Override
	public String getDebugName() {
		return "FastBow#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
