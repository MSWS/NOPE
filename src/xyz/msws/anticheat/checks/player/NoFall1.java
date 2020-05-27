package xyz.msws.anticheat.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Uh I have no idea how this check works. Will likely be rewritten soon
 * 
 * @author imodm
 * @deprecated Outdated and unreliable - likely to be rewritten
 *
 */
@Deprecated
public class NoFall1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.isOnGround())
			return;

		if (cp.isBlockNearby(Material.COBWEB) || cp.isBlockNearby(Material.COBWEB, 1.0))
			return;

		if (cp.isBlockNearby("SLAB") || cp.isBlockNearby("STEP") || cp.isInWeirdBlock())
			return;

		if (cp.timeSince(Stat.TELEPORT) < 500)
			return;

		if (player.getFallDistance() == 0)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "NoFall";
	}

	@Override
	public String getDebugName() {
		return "NoFall#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
