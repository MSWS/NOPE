package org.mswsplex.anticheat.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Timing;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.Sounds;

public class Animation implements Listener {
	private List<AnimationKey> keys;
	private NOPE plugin;

	public Animation(NOPE plugin) {
		this.keys = new ArrayList<>();
		this.plugin = plugin;
		runAnimations().runTaskTimer(plugin, 0, 1);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.getEntity().hasMetadata("ignore"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!isInAnimation(player))
			return;
		Location to = event.getTo(), from = event.getFrom();
		if (to.getX() != from.getX() || to.getZ() != from.getZ())
			event.setTo(event.getFrom());
	}

	public void startAnimation(AnimationKey key) {
		keys.add(key);
	}

	public List<AnimationKey> getAnimations() {
		return keys;
	}

	public void stopAnimations() {
		Iterator<AnimationKey> it = keys.iterator();
		while (it.hasNext()) {
			AnimationKey key = it.next();
			it.remove();
			keys.remove(key);
			stopAnimation(key);
		}
	}

	public void stopAnimation(AnimationKey key) {
		keys.remove(key);
		// Clear all blocks/entities
		CPlayer cp = plugin.getCPlayer(key.getPlayer());
		if (cp.hasTempData("animationBlocks")) {
			Block[][] blocks = (Block[][]) cp.getTempData("animationBlocks");

			for (int pillar = 0; pillar < blocks.length; pillar++) {
				for (int y = 0; y < blocks[1].length; y++) {
					Location blockLoc = blocks[pillar][y].getLocation();
					blockLoc.getBlock().setType(Material.AIR);
				}
			}
		}

		if (cp.hasTempData("animationEntities")) {
			Entity[] entities = (Entity[]) cp.getTempData("animationEntities");
			for (Entity ent : entities) {
				ent.remove();
			}
		}

		cp.removeTempData("animationBlocks");
		cp.removeTempData("animationEntities");

		if (!key.doCommands())
			return;

		if (key.getTiming() == Timing.BANWAVE || key.getTiming() == Timing.MANUAL_BANWAVE) {
			for (String line : plugin.config.getStringList("CommandsForBanwave")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line.replace("%player%", key.getPlayer().getName())
						.replace("%hack%", key.getCheck()).replace("%token%", key.getToken()));
			}
		} else {
			for (String line : plugin.config.getStringList("CommandsForBan")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line.replace("%player%", key.getPlayer().getName())
						.replace("%hack%", key.getCheck()).replace("%token%", key.getToken()));
			}
		}

	}

	public boolean isInAnimation(Player player) {
		for (AnimationKey key : keys)
			if (key.getPlayer().equals(player))
				return true;
		return false;
	}

	public AnimationKey getKey(Player player) {
		for (AnimationKey key : keys)
			if (key.getPlayer().equals(player))
				return key;
		return null;
	}

	public BukkitRunnable runAnimations() {
		return new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Iterator<AnimationKey> it = keys.iterator();
				while (it.hasNext()) {
					AnimationKey key = it.next();
					Player player = key.getPlayer();
					CPlayer cp = plugin.getCPlayer(player);
					long time = key.getStartTime(), cTime = System.currentTimeMillis() - time;
					if (player == null || !player.isOnline() || cTime > 5000) {
						it.remove();
						stopAnimation(key);
						continue;
					}

					Material[] pillarContents = { Material.BEDROCK, Material.OBSIDIAN, Material.DIAMOND_BLOCK,
							Material.GLASS };

					float range = 5;

					Block[][] blocks = new Block[8][pillarContents.length];
					Entity[] entities = new Entity[blocks.length];

					if (cp.hasTempData("animationBlocks")) {
						blocks = (Block[][]) cp.getTempData("animationBlocks");
					} else {
						for (int pillar = 0; pillar < blocks.length; pillar++) {
							for (int y = 0; y < blocks[0].length; y++) {
								Location blockLoc = player.getLocation().clone().add(Math.sin(pillar + cTime) * range,
										y, Math.cos(pillar + cTime) * range);
								if (blockLoc.getBlock().getType() == Material.AIR)
									blockLoc.getBlock().setType(pillarContents[y]);
								blocks[pillar][y] = blockLoc.getBlock();
							}
						}
					}

					if (cp.hasTempData("animationEntities")) {
						entities = (Entity[]) cp.getTempData("animationEntities");
					} else {
						for (int ent = 0; ent < blocks.length; ent++) {
							Location entLoc = player.getLocation().clone().add(Math.sin(ent + cTime) * range,
									blocks[0].length, Math.cos(ent + cTime) * range);
							entities[ent] = entLoc.getWorld().spawnEntity(entLoc, EntityType.BLAZE);
						}
						cp.setTempData("animationEntities", entities);
					}

					for (int pillar = 0; pillar < blocks.length; pillar++) {
						Block bottom = blocks[pillar][0];
						if (Bukkit.getVersion().contains("1.8"))
							for (Player p : bottom.getWorld().getPlayers()) {
								p.spigot().playEffect(bottom.getLocation().clone().add(.5, 0, .5), Effect.TILE_BREAK,
										bottom.getTypeId(), 0, 0, 0, 0, 0, 0, 200);
							}
						if (cp.timeSince("lastAnimationSound") > 500) {
							bottom.getWorld().playSound(bottom.getLocation(), Sounds.DIG_STONE.bukkitSound(), 1, 2);
							cp.setTempData("lastAnimationSound", (double) System.currentTimeMillis());
						}

						for (int y = 0; y < blocks[0].length; y++) {
							Location blockLoc = blocks[pillar][y].getLocation();
							if (blockLoc.getBlock().getType() == pillarContents[y])
								blockLoc.getBlock().setType(Material.AIR);
						}
					}

					Block[][] newBlocks = new Block[blocks.length][blocks[0].length];

					double slow = 1000 - Math.min(800, cTime / 5);

					for (int pillar = 0; pillar < blocks.length; pillar++) {
						for (int y = 0; y < blocks[0].length; y++) {
							Location blockLoc = player.getLocation().clone().add(
									Math.sin((cTime / slow) + (pillar * 80)) * range, y,
									Math.cos((cTime / slow) + (pillar * 80)) * range);
							if (blockLoc.getBlock().getType() == Material.AIR)
								blockLoc.getBlock().setType(pillarContents[y]);
							newBlocks[pillar][y] = blockLoc.getBlock();
						}
						entities[pillar].teleport(newBlocks[pillar][blocks[0].length - 1].getLocation());
					}

					if (cp.timeSince("lastAnimationFire") > 1000 && cTime > 2000) {
						for (Entity ent : entities) {
							Blaze blaze = (Blaze) ent;
							Location loc = blaze.getLocation().clone();
							loc.setDirection(player.getLocation().toVector().subtract(blaze.getLocation().toVector()));
							blaze.teleport(loc);
							Fireball ball = blaze.launchProjectile(Fireball.class);
							ball.setMetadata("ignore", new FixedMetadataValue(plugin, true));
						}
						cp.setTempData("lastAnimationFire", (double) System.currentTimeMillis());
					}

					cp.setTempData("animationBlocks", newBlocks);

				}
			}
		};
	}

}
