package xyz.msws.nope.checks.player;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import com.google.common.collect.Sets;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Definitely could be improved
 * 
 * @author imodm
 *
 *
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

	private HashSet<Material> mats = Sets.newHashSet(Material.CHEST, Material.ENDER_CHEST, Material.ACACIA_BUTTON,
			Material.BIRCH_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.OAK_BUTTON,
			Material.SPRUCE_BUTTON, Material.STONE_BUTTON);

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Block block = event.getClickedBlock();
		if (block == null || block.getType() == Material.AIR)
			return;

		if (!mats.contains(block.getType()))
			return;

		RayTraceResult result = player.rayTraceBlocks(6, FluidCollisionMode.NEVER);
		if (result == null)
			return;
		if (event.getBlockFace() == result.getHitBlockFace() && block.getType() == result.getHitBlock().getType())
			return;

		cp.flagHack(this, 5, "Origin: &a" + event.getBlockFace() + " | " + block.getType() + "\n&7Server: &e"
				+ result.getHitBlockFace() + " | " + result.getHitBlock().getType());

	}

	@Override
	public String getCategory() {
		return "GhostHand";
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
