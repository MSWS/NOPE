package xyz.msws.nope.checks.world;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks if the placed blockface is the same as the face interacted iwth
 * 
 * @author imodm
 *
 */
public class Scaffold3 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockPlace(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Block target = player.getTargetBlock((Set<Material>) null, 10);

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!event.isBlockInHand())
			return;

		if (player.isFlying())
			return;

		if (player.getLocation().getY() <= target.getLocation().getY())
			return;

		if (target.isLiquid())
			return;

		if (event.getBlockFace() == BlockFace.UP)
			return;

		List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>) null, 10);
		BlockFace face = null;
		if (blocks.size() > 1) {
			face = blocks.get(1).getFace(blocks.get(0));
		} else {
			return;
		}

		if (event.getBlockFace() == face)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#3";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

}
