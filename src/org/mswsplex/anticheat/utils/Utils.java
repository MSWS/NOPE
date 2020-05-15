package org.mswsplex.anticheat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.mswsplex.anticheat.msws.NOPE;

import com.google.common.collect.Sets;

public class Utils {
	public static NOPE plugin;

	/**
	 * Returns a ranking of all the armor from value
	 * 
	 * @param mat Material to compare
	 * @return Diamond: 4 Iron: 3 Chain: 2 Gold: 1 Leather: 0 Default: 0
	 */
	public static int getArmorValue(Material mat) {
		switch (getArmorType(mat).toLowerCase()) {
			case "diamond":
				return 4;
			case "iron":
				return 3;
			case "chainmail":
				return 2;
			case "gold":
				return 1;
			case "leather":
				return 0;
			default:
				return 0;
		}
	}

	/**
	 * Gets the armor slot that a type of armor should be in
	 * 
	 * @param type Material type (DIAMOND_CHESTPLATE, IRON_LEGGINGS, etc)
	 * @return Armor slot Helmet: 3 Chestplate: 2 Leggings: 1 Boots: 0
	 */
	public static int getSlot(Material type) {
		if (!type.name().contains("_"))
			return 0;
		switch (type.name().split("_")[1]) {
			case "HELMET":
				return 3;
			case "CHESTPLATE":
				return 2;
			case "LEGGINGS":
				return 1;
			case "BOOTS":
				return 0;
		}
		return 0;
	}

	/**
	 * Returns type of armor
	 * 
	 * @param mat Material to get type of
	 * @return DIAMOND, IRON, GOLD, CHAINMAIL
	 */
	public static String getArmorType(Material mat) {
		if (!mat.name().contains("_")) {
			return "";
		}
		String name = mat.name().split("_")[0];
		return name;
	}

	/**
	 * Returns if the specified material is armor
	 * 
	 * @param mat Material to check
	 * @return True if armor, false otherwise
	 */

	private static HashSet<Material> armor = Sets.newHashSet(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
			Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

	public static boolean isArmor(Material mat) {
		return armor.contains(mat);
	}

	/**
	 * Gets a block based on the blockface
	 * 
	 * @param block Block to compare face to
	 * @param face  Relative face to get block
	 * @return
	 */
	public static Block blockFromFace(Block block, BlockFace face) {
		int x = 0, y = 0, z = 0;
		if (face == BlockFace.EAST)
			x = 1;
		if (face == BlockFace.WEST)
			x = -1;
		if (face == BlockFace.NORTH)
			z = -1;
		if (face == BlockFace.SOUTH)
			z = 1;
		if (face == BlockFace.UP)
			y = 1;
		if (face == BlockFace.DOWN)
			y = -1;
		return block.getLocation().add(x, y, z).getBlock();
	}

	/**
	 * Returns parsed Inventory from YAML config (guis.yml)
	 * 
	 * @param player Player to parse information with (%player% and other
	 *               placeholders)
	 * @param id     Name of the inventory to parse
	 * @param page   Page of the inventory
	 * @return
	 */
	public static Inventory getGui(OfflinePlayer player, String id, int page) {
		if (!plugin.gui.contains(id))
			return null;
		ConfigurationSection gui = plugin.gui.getConfigurationSection(id);
		if (!gui.contains("Size") || !gui.contains("Title"))
			return null;
		String title = gui.getString("Title").replace("%player%", player.getName());
		if (player.isOnline())
			title = title.replace("%world%", ((Player) player).getWorld().getName());
		title = title.replace("%world%", "");
		Inventory inv = Bukkit.createInventory(null, gui.getInt("Size"), MSG.color(title));
		ItemStack bg = null;
		boolean empty = true;
		for (String res : gui.getKeys(false)) {
			if (!gui.contains(res + ".Icon"))
				continue;
			empty = false;
			if (gui.contains(res + ".Page")) {
				if (page != gui.getInt(res + ".Page"))
					continue;
			} else if (page != 0)
				continue;
			if (player.isOnline()) {
				if (gui.contains(res + ".Permission")
						&& !((Player) player).hasPermission(gui.getString(res + ".Permission"))) {
					continue;
				}
			}
			ItemStack item = parseItem(plugin.gui, id + "." + res, player);
			if (res.equals("BACKGROUND_ITEM")) {
				bg = item;
				continue;
			}
			int slot = 0;
			if (!gui.contains(res + ".Slot")) {
				while (inv.getItem(slot) != null)
					slot++;
				inv.setItem(slot, item);
			} else {
				inv.setItem(gui.getInt(res + ".Slot"), item);
			}
		}
		if (empty)
			return null;
		if (bg != null) {
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
					inv.setItem(i, bg);
				}
			}
		}
		return inv;
	}

	/**
	 * Parses and returns an item from the specified YAML Path Supports
	 * enchantments, damage values, amounts, skulls, lores, and unbreakable
	 * 
	 * @param section Section to get item from
	 * @param path    Specified path after section
	 * @param player  Player to parse the items with (for %player% and other
	 *                placeholders)
	 * @return Parsed ItemStack
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack parseItem(ConfigurationSection section, String path, OfflinePlayer player) {
		ConfigurationSection gui = section.getConfigurationSection(path);
		ItemStack item = new ItemStack(Material.valueOf(gui.getString("Icon")));
		List<String> lore = new ArrayList<String>();
		if (gui.contains("Amount"))
			item.setAmount(gui.getInt("Amount"));
		if (gui.contains("Data"))
			((Damageable) item).setDamage(gui.getInt("Data"));
		if (gui.contains("Owner")) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(gui.getString("Owner")));
			item.setItemMeta(meta);
		}
		ItemMeta meta = item.getItemMeta();
		if (gui.contains("Name"))
			meta.setDisplayName(MSG.color("&r" + gui.getString("Name")));
		if (gui.contains("Lore")) {
			for (String temp : gui.getStringList("Lore"))
				lore.add(MSG.color("&r" + temp));
		}
		if (gui.getBoolean("Unbreakable")) {
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if (gui.contains("Cost")) {
			ConfigurationSection costs = gui.getConfigurationSection("Cost");
			lore.add("");
			if (costs.getKeys(false).size() == 1) {
				String id = costs.getKeys(false).toArray()[0].toString();
				int cost = (costs.getInt(costs.getKeys(false).toArray()[0].toString()));
				lore.add(MSG.color("&c* " + cost + " " + MSG.camelCase(id))
						+ ((cost == 1 || id.toLowerCase().endsWith("s")) ? "" : "s"));
			} else {
				lore.add(MSG.color("&aCost:"));
				for (String mat : costs.getKeys(false)) {
					if (mat.equals("XP") || mat.equals("COINS")) {
						lore.add(MSG.color("&c* " + costs.getInt(mat) + " " + MSG.camelCase(mat)));
					} else {
						lore.add(MSG.color("&c* " + costs.getInt(mat) + " " + MSG.camelCase(mat))
								+ ((costs.getInt(mat) == 1 || mat.toLowerCase().endsWith("s")) ? "" : "s"));
					}
				}
			}
		}
		if (gui.contains("Enchantments")) {
			ConfigurationSection enchs = gui.getConfigurationSection("Enchantments");
			for (String enchant : enchs.getKeys(false)) {
				int level = 1;
				if (enchs.contains(enchant + ".Level"))
					level = enchs.getInt(enchant + ".Level");
				if (enchs.contains(enchant + ".Visible") && !enchs.getBoolean(enchant + ".Visible"))
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.getByName(enchant.toUpperCase()), level);
				meta = item.getItemMeta();
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Calculates a player's total exp based on level and progress to next.
	 * 
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 * @param player the Player
	 * 
	 * @return the amount of exp the Player has
	 */
	public static int getExp(Player player) {
		return getExpFromLevel(player.getLevel()) + Math.round(getExpToNext(player.getLevel()) * player.getExp());
	}

	/**
	 * Calculates total experience based on level.
	 * 
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 *      "One can determine how much experience has been collected to reach a
	 *      level using the equations:
	 * 
	 *      Total Experience = [Level]2 + 6[Level] (at levels 0-15) 2.5[Level]2 -
	 *      40.5[Level] + 360 (at levels 16-30) 4.5[Level]2 - 162.5[Level] + 2220
	 *      (at level 31+)"
	 * 
	 * @param level the level
	 * 
	 * @return the total experience calculated
	 */
	public static int getExpFromLevel(int level) {
		if (level > 30) {
			return (int) (4.5 * level * level - 162.5 * level + 2220);
		}
		if (level > 15) {
			return (int) (2.5 * level * level - 40.5 * level + 360);
		}
		return level * level + 6 * level;
	}

	/**
	 * Calculates level based on total experience.
	 * 
	 * @param exp the total experience
	 * 
	 * @return the level calculated
	 */
	public static double getLevelFromExp(long exp) {
		if (exp > 1395) {
			return (Math.sqrt(72 * exp - 54215) + 325) / 18;
		}
		if (exp > 315) {
			return Math.sqrt(40 * exp - 7839) / 10 + 8.1;
		}
		if (exp > 0) {
			return Math.sqrt(exp + 9) - 3;
		}
		return 0;
	}

	/**
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 *      "The formulas for figuring out how many experience orbs you need to get
	 *      to the next level are as follows: Experience Required = 2[Current Level]
	 *      + 7 (at levels 0-15) 5[Current Level] - 38 (at levels 16-30) 9[Current
	 *      Level] - 158 (at level 31+)"
	 */
	private static int getExpToNext(int level) {
		if (level > 30) {
			return 9 * level - 158;
		}
		if (level > 15) {
			return 5 * level - 38;
		}
		return 2 * level + 7;
	}

	/**
	 * Change a Player's exp.
	 * <p>
	 * This method should be used in place of {@link Player#giveExp(int)}, which
	 * does not properly account for different levels requiring different amounts of
	 * experience.
	 * 
	 * @param player the Player affected
	 * @param exp    the amount of experience to add or remove
	 */
	public static void changeExp(Player player, int exp) {
		exp += getExp(player);

		if (exp < 0) {
			exp = 0;
		}

		double levelAndExp = getLevelFromExp(exp);

		int level = (int) levelAndExp;
		player.setLevel(level);
		player.setExp((float) (levelAndExp - level));
	}

	public enum Age {
		OUTDATED_VERSION, DEVELOPER_VERSION, SAME_VERSION;
	}

	public static Age outdated(String v1, String v2) {
		int vnum1 = 0, vnum2 = 0;

		// loop untill both String are processed
		for (int i = 0, j = 0; (i < v1.length() || j < v2.length());) {
			// storing numeric part of version 1 in vnum1
			while (i < v1.length() && v1.charAt(i) != '.') {
				vnum1 = vnum1 * 10 + (v1.charAt(i) - '0');
				i++;
			}

			// storing numeric part of version 2 in vnum2
			while (j < v2.length() && v2.charAt(j) != '.') {
				vnum2 = vnum2 * 10 + (v2.charAt(j) - '0');
				j++;
			}

			if (vnum1 > vnum2)
				return Age.DEVELOPER_VERSION;
			if (vnum2 > vnum1)
				return Age.OUTDATED_VERSION;

			// if equal, reset variables and go for next numeric
			// part
			vnum1 = vnum2 = 0;
			i++;
			j++;
		}
		return Age.SAME_VERSION;
	}

	/**
	 * Returns the bukkit name of an enchantment
	 * 
	 * @param name "Nickname" of enchant (sharpness, infinity, power, etc.)
	 * @return String og Enchantment Enum
	 */
	public static String getEnchant(String name) {
		switch (name.toLowerCase().replace("_", "")) {
			case "power":
				return "ARROW_DAMAGE";
			case "flame":
				return "ARROW_FIRE";
			case "infinity":
			case "infinite":
				return "ARROW_INFINITE";
			case "punch":
			case "arrowkb":
				return "ARROW_KNOCKBACK";
			case "sharpness":
				return "DAMAGE_ALL";
			case "arthropods":
			case "spiderdamage":
			case "baneofarthropods":
				return "DAMAGE_ARTHORPODS";
			case "smite":
				return "DAMAGE_UNDEAD";
			case "depthstrider":
			case "waterwalk":
				return "DEPTH_STRIDER";
			case "efficiency":
				return "DIG_SPEED";
			case "unbreaking":
				return "DURABILITY";
			case "fireaspect":
			case "fire":
				return "FIRE_ASPECT";
			case "knockback":
			case "kb":
				return "KNOCKBACK";
			case "fortune":
				return "LOOT_BONUS_BLOCKS";
			case "looting":
				return "LOOT_BONUS_MOBS";
			case "luck":
				return "LUCK";
			case "lure":
				return "LURE";
			case "waterbreathing":
			case "respiration":
				return "OXYGEN";
			case "prot":
			case "protection":
				return "PROTECTION_ENVIRONMENTAL";
			case "blastprot":
			case "blastprotection":
				return "PROTECTION_EXPLOSIONS";
			case "feather":
			case "featherfalling":
				return "PROTECTION_FALL";
			case "fireprot":
			case "fireprotection":
				return "PROTECTION_FIRE";
			case "projectileprot":
			case "projectileprotection":
			case "projprot":
				return "PROTECTION_PROJECTILE";
			case "silktouch":
			case "silk":
				return "SILK_TOUCH";
			case "thorns":
				return "THORNS";
			case "aquaaffinity":
			case "aqua":
			case "waterworker":
				return "WATER_WORKER";
		}
		return name.toUpperCase();
	}

	/**
	 * 
	 * @param data Wool dye value
	 * @return ChatColor (&a, &b, etc.) for the matching data
	 */
	public static String colorByWoolData(short data) {
		switch ((data) == -1 ? 15 : (data % 16)) {
			case 12:
				return "&b";
			case 11:
				return "&e";
			case 10:
				return "&a";
			case 9:
				return "&d";
			case 8:
				return "&8";
			case 7:
				return "&7";
			case 6:
				return "&3";
			case 5:
				return "&5";
			case 4:
				return "&9";
			case 3:
				return "&6";
			case 2:
				return "&2";
			case 1:
				return "&4";
			case 0:
				return "&0";
			case 15:
				return "&f";
			case 14:
				return "&6";
			case 13:
				return "&d";
		}
		return "";
	}

	/**
	 * Delete a world file from the worlds (World should be unloaded first)
	 * 
	 * @param path File to delete
	 * @return If the world was successfully deleted
	 */
	public static boolean deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteWorld(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Returns a list of all unloaded worlds
	 * 
	 * @param includeLoaded Whether or not to include loaded worlds
	 * @return List of the world names
	 */
	public static List<String> getUnloadedWorlds(boolean includeLoaded) {
		List<String> worlds = new ArrayList<>();
		if (includeLoaded) {
			for (World world : Bukkit.getWorlds())
				worlds.add(world.getName());
		}

		for (String res : Bukkit.getWorldContainer().list()) {
			File file = new File(Bukkit.getWorldContainer().toPath() + File.separator + res);
			if (isWorldFile(file) && !worlds.contains(file.getName()))
				worlds.add(file.getName());
		}
		return worlds;
	}

	/**
	 * Returns whether or not a file is a world file
	 * 
	 * @param file Path to check
	 * @return True/False
	 */
	public static boolean isWorldFile(File file) {
		if (file != null && file.list() != null)
			for (String r : file.list())
				if (r.equals("session.lock"))
					return true;
		return false;
	}
}
