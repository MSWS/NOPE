package xyz.msws.nope.checks.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

/**
 * Checks if player breaks block too quickly
 * 
 * @author imodm
 *
 */
public class FastBreak1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	private Map<UUID, Location> blockLoc = new HashMap<>();
	private Map<UUID, Long> blockTime = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private boolean ignoreMat(Material mat) {
		for (String res : new String[] { "BED", "GLASS", "LEAVES", "ICE", "PLATE" }) {
			if (mat.toString().contains(res))
				return true;
		}
		return mat == Material.SPAWNER;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		Block block = event.getClickedBlock();
		if (block == null || block.getType() == Material.AIR)
			return;
		if (ignoreMat(block.getType()))
			return;
		Location loc = block.getLocation();
		blockLoc.put(player.getUniqueId(), loc);
		blockTime.put(player.getUniqueId(), System.currentTimeMillis() + getDigTime(block, player));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		CPlayer cp = plugin.getCPlayer(player);
		if (!blockTime.containsKey(player.getUniqueId()))
			return;
		Block block = event.getBlock();
		if (ignoreMat(block.getType()))
			return;
		if (!blockLoc.get(player.getUniqueId()).equals(block.getLocation())) {
			cp.flagHack(this, 50, "Wrong Block");
			return;
		}
		double offset = System.currentTimeMillis() - blockTime.get(player.getUniqueId());
		if (offset > -100)
			return;
		cp.flagHack(this, Math.min((int) Math.abs(offset) / 10 + 25, 100),
				"Type: &e" + MSG.camelCase(block.getType().toString()) + "\n&7Time diff: &a" + offset
						+ "\n&7Hardness: &e" + block.getType().getHardness());
	}

	private long getDigTime(Block block, Player player) {
		ItemStack tool = player.getInventory().getItemInMainHand();
		boolean canHarvest = !block.getDrops(tool).isEmpty();
		double seconds = block.getType().getHardness() * (canHarvest ? 1.5f : 5);

		if (canHarvest(block.getType(), tool.getType()))
			seconds /= getToolMultiplier(tool.getType());

		if (tool.containsEnchantment(Enchantment.DIG_SPEED) && canHarvest) {
			seconds -= (Math.pow(tool.getEnchantmentLevel(Enchantment.DIG_SPEED), 2) + 1);
		}

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			seconds -= seconds * .20 * (player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier() + 1);
		}
		if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
			switch (player.getPotionEffect(PotionEffectType.SLOW_DIGGING).getAmplifier()) {
				case 1:
					seconds += seconds * .70;
					break;
				case 2:
					seconds += seconds * .91;
					break;
				case 3:
					seconds += seconds * .9973;
					break;
				default:
					seconds += seconds * .99919;
					break;
			}
		}

		water: if (player.getLocation().clone().add(0, 1, 0).getBlock().isLiquid()) {
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() != Material.AIR)
				if (helmet.containsEnchantment(Enchantment.WATER_WORKER))
					break water;

			seconds *= 5;
		}
//		if (!player.isOnGround())
//			seconds *= 5;

		return (long) (seconds * 1000);
	}

	private double getToolMultiplier(Material tool) {
		if (tool.toString().contains("DIAMOND"))
			return 8;
		if (tool.toString().contains("IRON"))
			return 6;
		if (tool.toString().contains("STONE"))
			return 4;
		if (tool.toString().contains("WOOD"))
			return 2;
		return 1;
	}

	private boolean canHarvest(Material type, Material item) {
		switch (item) {
			case DIAMOND_AXE:
			case GOLDEN_AXE:
			case IRON_AXE:
			case STONE_AXE:
			case WOODEN_AXE:
				for (String res : new String[] { "BANNER", "FENCE", "PLANKS", "SIGN", "WOOD", "LOG" }) {
					if (type.toString().contains(res))
						return true;
				}

				switch (type) {
					case BARREL:
					case BOOKSHELF:
					case BEEHIVE:
					case CAMPFIRE:
					case CARTOGRAPHY_TABLE:
					case COMPOSTER:
					case CRAFTING_TABLE:
					case DAYLIGHT_DETECTOR:
					case FLETCHING_TABLE:
					case JUKEBOX:
					case LADDER:
					case LECTERN:
					case LOOM:
					case NOTE_BLOCK:
					case SMITHING_TABLE:
					case BROWN_MUSHROOM_BLOCK:
					case RED_MUSHROOM_BLOCK:
					case COCOA:
					case JACK_O_LANTERN:
					case PUMPKIN:
					case VINE:
					case MELON:
					case BEE_NEST:
						return true;
					default:
						return false;
				}
			case DIAMOND_PICKAXE:
			case GOLDEN_PICKAXE:
			case IRON_PICKAXE:
			case STONE_PICKAXE:
			case WOODEN_PICKAXE:
				for (String res : new String[] { "ICE", "ORE", "CONCRETE", "TERRACOTTA", "SLAB", "WALL", "POLISHED" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case ANVIL:
					case BELL:
					case REDSTONE_BLOCK:
					case BREWING_STAND:
					case CAULDRON:
					case HOPPER:
					case IRON_BARS:
					case IRON_DOOR:
					case IRON_TRAPDOOR:
					case LANTERN:
					case LIGHT_WEIGHTED_PRESSURE_PLATE:
					case HEAVY_WEIGHTED_PRESSURE_PLATE:
					case IRON_BLOCK:
					case LAPIS_BLOCK:
					case DIAMOND_BLOCK:
					case EMERALD_BLOCK:
					case GOLD_BLOCK:
					case PISTON:
					case STICKY_PISTON:
					case CONDUIT:
					case SHULKER_BOX:
					case ACTIVATOR_RAIL:
					case DETECTOR_RAIL:
					case POWERED_RAIL:
					case RAIL:
					case ANDESITE:
					case GRANITE:
					case BLAST_FURNACE:
					case COAL_BLOCK:
					case QUARTZ_BLOCK:
					case BRICKS:
					case COAL:
					case COBBLESTONE:
					case COBBLESTONE_WALL:
					case DARK_PRISMARINE:
					case DIORITE:
					case DISPENSER:
					case DROPPER:
					case ENCHANTING_TABLE:
					case END_STONE:
					case ENDER_CHEST:
					case FURNACE:
					case GRINDSTONE:
					case MOSSY_COBBLESTONE:
					case NETHER_BRICKS:
					case NETHER_BRICK:
					case NETHER_BRICK_FENCE:
					case NETHERRACK:
					case OBSERVER:
					case PRISMARINE:
					case PRISMARINE_BRICKS:
					case POLISHED_ANDESITE:
					case POLISHED_DIORITE:
					case POLISHED_GRANITE:
					case RED_SANDSTONE:
					case SANDSTONE_SLAB:
					case SMOKER:
					case SPAWNER:
					case STONECUTTER:
					case STONE:
					case STONE_BRICKS:
					case STONE_BUTTON:
					case STONE_PRESSURE_PLATE:
					case OBSIDIAN:
					case BASALT:
					case BLACKSTONE:
					case GILDED_BLACKSTONE:
						return true;
					default:
						return false;
				}
			case SHEARS:
				for (String res : new String[] { "LEAVES", "WOOL" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case COBWEB:
						return true;
					default:
						return false;
				}
			case DIAMOND_SHOVEL:
			case GOLDEN_SHOVEL:
			case IRON_SHOVEL:
			case STONE_SHOVEL:
			case WOODEN_SHOVEL:
				for (String res : new String[] { "POWDER" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case CLAY:
					case DIRT:
					case COARSE_DIRT:
					case FARMLAND:
					case GRASS_BLOCK:
					case GRAVEL:
					case MYCELIUM:
					case PODZOL:
					case RED_SAND:
					case SAND:
					case SOUL_SAND:
					case SNOW_BLOCK:
					case SNOW:
						return true;
					default:
						return false;
				}
			case DIAMOND_SWORD:
			case GOLDEN_SWORD:
			case IRON_SWORD:
			case WOODEN_SWORD:
			case STONE_SWORD:
				switch (type) {
					case COBWEB:
					case BAMBOO:
						return true;
					default:
						return false;
				}
			case DIAMOND_HOE:
			case GOLDEN_HOE:
			case IRON_HOE:
			case WOODEN_HOE:
			case STONE_HOE:
				switch (type) {
					case HAY_BLOCK:
					case SPONGE:
					case WET_SPONGE:
						return true;
					default:
						return false;
				}
			default:
				switch (type) {
					case VINE:
						return true;
					default:
						return false;
				}
		}
	}

	@Override
	public String getCategory() {
		return "FastBreak";
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
