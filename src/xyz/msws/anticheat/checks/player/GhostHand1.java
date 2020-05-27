package xyz.msws.anticheat.checks.player;

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
import org.bukkit.potion.PotionEffectType;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

/**
 * Checks if a block that a player clicked on is the same that the player is
 * looking at In addition, checks if the block above the clicked block is air
 * (if it is, return - this is to avoid false flags)
 * 
 * @author imodm
 *
 *
 * @deprecated inaccurate
 */
public class GhostHand1 implements Check, Listener {

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
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
		if (targetBlock.equals(event.getClickedBlock()))
			return;
		if (!targetBlock.getType().isSolid())
			return;
		if (targetBlock.getLocation().distanceSquared(player.getLocation()) < 2)
			return;
		if (!event.getClickedBlock().getRelative(BlockFace.UP).getType().isSolid())
			return;

		if (targetBlock.getType().toString().contains("TRAPDOOR"))
			return;

		if (targetBlock.getType().toString().contains("DOOR")
				&& targetBlock.getType() == event.getClickedBlock().getType())
			return;

		cp.flagHack(this, 5, "Event: &e" + MSG.camelCase(event.getClickedBlock().getType() + "") + "\n&7Server: &e"
				+ MSG.camelCase(targetBlock.getType() + ""));
	}

	@Override
	public String getCategory() {
		return "GhostHand";
	}

	@Override
	public String getDebugName() {
		return "GhostHand#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
