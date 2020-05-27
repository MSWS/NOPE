package xyz.msws.anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

public class ElytraFlight1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, Vector> velocities = new HashMap<>();
	private Map<UUID, Long> fireworks = new HashMap<>();

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.isGliding())
			return;
		if (event.getTo().getY() < event.getFrom().getY())
			return;
		if (System.currentTimeMillis() - fireworks.getOrDefault(player.getUniqueId(), 0L) < 1000)
			return;

		Vector v = event.getTo().toVector().subtract(event.getFrom().toVector());
		Vector old = velocities.getOrDefault(player.getUniqueId(), v);
		double oldLength = old.lengthSquared(), newLength = v.lengthSquared();

		if (newLength < oldLength)
			return;

		CPlayer cp = plugin.getCPlayer(player);
		if (cp.timeSince(Stat.ON_GROUND) < 200)
			return;
		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500)
			return;
		cp.flagHack(this, (int) ((newLength - oldLength) * 100), "Old: &e" + oldLength + "&7\nNew: " + newLength);

		velocities.put(player.getUniqueId(), v);
	}

	@EventHandler
	public void playerFirework(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		if (event.getItem().getType() != Material.FIREWORK_ROCKET)
			return;

		FireworkMeta meta = (FireworkMeta) event.getItem().getItemMeta();

		fireworks.put(player.getUniqueId(), System.currentTimeMillis() + meta.getPower() * 1500);
	}

	@Override
	public String getCategory() {
		return "ElytraFlight";
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
