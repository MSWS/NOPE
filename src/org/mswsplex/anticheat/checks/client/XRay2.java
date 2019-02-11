package org.mswsplex.anticheat.checks.client;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.Cuboid;

/**
 * 
 * Hides ores with fake block packets
 * 
 * @deprecated causes obscene lag - highly recommended you don't use
 * 
 * @author imodm
 * 
 */
@Deprecated
public class XRay2 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.CLIENT;
	}

	// private AntiCheat plugin;

	@Override
	public void register(AntiCheat plugin) {
		// this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		List<Material> ores = new ArrayList<>();

		for (String mat : plugin.config.getStringList("AntiXRay2.Blocks")) {
			ores.add(Material.valueOf(mat.toUpperCase()));
		}

		if (!plugin.config.getBoolean("AntiXRay2.Enabled"))
			return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					// CPlayer cp = plugin.getCPlayer(player);
					Cuboid cube = new Cuboid(player.getLocation().clone().subtract(100, 50, 100),
							player.getLocation().clone().add(100, 50, 100));

					List<Block> chunkOres = new ArrayList<>();

					List<Block> cubeBlocks = cube.getBlocks();

					int[] startPos = { 0 };

					final int iterate = 10000;

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

							// Neither methods revealed substantial differences in timings

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
		}.runTaskTimer(plugin, 0, 1000);
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
