package xyz.msws.nope.checks.combat;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Compares if the player's direction is the same as the player's vector -
 * entity's vector
 * 
 * @author imodm
 *
 */
public class KillAura1 implements Check, Listener {

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

	@EventHandler
	public void onMove(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();

		CPlayer cp = plugin.getCPlayer(player);

		Vector real = event.getEntity().getLocation().toVector().subtract(player.getEyeLocation().toVector());
		Vector pvec = player.getEyeLocation().getDirection();

		real.setY(0);
		pvec.setY(0);

		real.normalize();
		pvec.normalize();

		double diff = real.dot(pvec);

		if (diff > .8)
			return;
		cp.flagHack(this, (int) ((.8 - diff) * 10), "Diff: &e" + diff);
	}

	@Override
	public String getCategory() {
		return "KillAura";
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
