package org.mswsplex.anticheat.checks.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.Cuboid;

/**
 * 
 * Hides ores with fake block packets
 * 
 * @author imodm
 * @deprecated causes 1 tick delays between block modifications
 * 
 */
@Deprecated
public class XRay1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private List<Block> ignoreBlocks;

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		List<Material> ores = new ArrayList<>();
		ignoreBlocks = new ArrayList<>();

		for (String mat : plugin.getConfig().getStringList("AntiXRay1.Blocks")) {
			ores.add(Material.valueOf(mat.toUpperCase()));
		}

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		if (plugin.getConfig().getBoolean("AntiXRay1.Enabled"))
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Player player : Bukkit.getOnlinePlayers()) {
						for (int x = -200; x <= 200; x += rnd.nextInt(5) + 5) {
							for (int z = -200; z <= 200; z += rnd.nextInt(5) + 5) {
								yLoop: for (int y = -100; y <= 20; y += rnd.nextInt(5) + 5) {
									Block block = player.getLocation().clone().add(x, y, z).getBlock();
									for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH,
											BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN }) {
										if (!block.getRelative(face).getType().isSolid()
												|| block.getRelative(face).isLiquid()) {
											continue yLoop;
										}
									}
									player.sendBlockChange(block.getLocation(), ores.get(rnd.nextInt(ores.size())),
											(byte) 0);
								}
							}
						}
					}
				}
			}.runTaskTimer(plugin, 0, 20 * 30);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		revealBlocks(player, 10);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ignoreBlocks.add(event.getBlock());
	}

	private void revealBlocks(Player player, int range) {
		Cuboid cube = new Cuboid(player.getLocation().clone().subtract(range, range, range),
				player.getLocation().clone().add(range, range, range));

		for (Block block : cube) {
			if (ignoreBlocks.contains(block))
				continue;
			for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
					BlockFace.UP, BlockFace.DOWN }) {
				if (!block.getRelative(face).getType().isSolid()) {
					player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
					break;
				}
			}
		}
	}

	@Override
	public String getCategory() {
		return "XRay";
	}

	@Override
	public String getDebugName() {
		return "XRay#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
