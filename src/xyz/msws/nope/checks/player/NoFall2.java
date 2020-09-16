package xyz.msws.nope.checks.player;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

public class NoFall2 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Vector vel = player.getVelocity();

		if (player.isRiptiding() || !player.isOnGround())
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 1000)
			return;

		if (cp.timeSince(Stat.SHULKER) < 500)
			return;

		double yDiff = event.getTo().getY() - event.getFrom().getY();

		if (yDiff <= 0 || vel.getY() >= -0.0784000015258789)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500 || cp.timeSince(Stat.FLYING) < 500)
			return;

		cp.flagHack(this, 5, String.format("&c%s &7vs &e%s", vel.getY(), yDiff));
	}

	@Override
	public String getCategory() {
		return "NoFall";
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
