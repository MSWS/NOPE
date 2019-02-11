package org.mswsplex.anticheat.checks.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.Cuboid;

/**
 * 
 * Compares a player's previous fire ticks and flags if they've dropped too
 * suddenly this WILL flag commands such as /heal
 * 
 * @author imodm
 * 
 */
public class XRay1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		List<Material> ores = Arrays.asList(new Material[] { Material.COAL_ORE, Material.DIAMOND_ORE,
				Material.EMERALD_ORE, Material.GOLD_ORE, Material.QUARTZ_ORE, Material.IRON_ORE, Material.REDSTONE_ORE,
				Material.LAPIS_ORE, Material.CHEST, Material.MOSSY_COBBLESTONE, Material.TRAPPED_CHEST, Material.LAVA,
				Material.STATIONARY_LAVA, Material.MOB_SPAWNER, Material.SMOOTH_BRICK });
		
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					// CPlayer cp = plugin.getCPlayer(player);
					Cuboid cube = new Cuboid(player.getLocation().clone().subtract(100, 50, 100),
							player.getLocation().clone().add(100, 50, 100));

					List<Block> chunkOres = new ArrayList<>();

					List<Block> cubeBlocks = cube.getBlocks();

					int[] startPos = { 0 };

					final int iterate = 100000;

					new BukkitRunnable() {
						@Override
						public void run() {
							if (startPos[0] >= cubeBlocks.size()) {
								new BukkitRunnable() {
									@Override
									public void run() {
										for (Block block : chunkOres) {
											player.sendBlockChange(block.getLocation(), Material.STONE, (byte) 0);
										}
									}
								}.run();
								cancel();
								return;
							}

//							chunkOres.addAll(cubeBlocks
//									.subList(startPos[0], Math.min(startPos[0] + iterate, cubeBlocks.size())).stream()
//									.filter((block) -> ores.contains(block.getType())).collect(Collectors.toList()));

							for (int i = 0; i < iterate && startPos[0] + i < cubeBlocks.size(); i++) {
								Block block = cubeBlocks.get(startPos[0] + i);
								if (!ores.contains(block.getType()))
									continue;
								chunkOres.add(block);
							}
							startPos[0] += iterate;
						}
					}.runTaskTimer(plugin, 0, 1);
				}
			}
		}.runTaskTimer(plugin, 0, 500);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		int range = 10;

		Cuboid cube = new Cuboid(player.getLocation().clone().subtract(range, range, range),
				player.getLocation().clone().add(range, range, range));

		for (Block block : cube) {
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
		return "XRay#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
