package org.mswsplex.anticheat.checks.client;

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
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * Checks if a block that a player clicked on is the same that the player is
 * looking at In addition, checks if the block above the clicked block is air
 * (if it is, return - this is to avoid false flags)
 * 
 * @author imodm
 *
 */
public class GhostHand1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	@Override
	public void register(AntiCheat plugin) {
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
		if (!event.getClickedBlock().getRelative(BlockFace.UP).getType().isSolid())
			return;

		cp.flagHack(this, 5);
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
