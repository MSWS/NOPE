package org.mswsplex.anticheat.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class Stats {
	private File statsFile;

	private YamlConfiguration stats;

	private AntiCheat plugin;

	public Stats(AntiCheat plugin) {
		this.plugin = plugin;
		new File(plugin.getDataFolder(), "/stats/").mkdir();

		statsFile = new File(plugin.getDataFolder(), "/stats/stats.yml");

		try {
			statsFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stats = YamlConfiguration.loadConfiguration(statsFile);
	}

	public Inventory getInventory() {
		int size;
		for (size = 9; size < 54; size += 9) {
			if (size > CheckType.values().length)
				break;
		}
		Inventory inv = Bukkit.createInventory(null, size, "All Checks");
		for (CheckType type : CheckType.values()) {
			ItemStack item = new ItemStack(Material.PAPER,
					Math.max(plugin.getChecks().getChecksWithType(type).size(), 1));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MSG.color("&a&l" + MSG.camelCase(type + "")));
			int vls = getTotalVl(type), triggers = getTotalTriggers(type);
			List<String> lore = new ArrayList<>();
			lore.add(MSG.color("&7Number of checks: &e" + plugin.getChecks().getChecksWithType(type).size()));
			lore.add(MSG.color("&7" + type.getDescription()));
			lore.add(MSG.color("&7VLs: &e" + vls));
			lore.add(MSG.color("&7Triggers: &e" + triggers));
			lore.add(MSG.color("&7Ratio: &e" + ((triggers == 0) ? 0 : (vls / triggers))));
			lore.add(MSG.color("&7Enabled: "
					+ MSG.TorF(plugin.config.getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"))));

			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		return inv;
	}

	public Inventory getInventory(CheckType type) {
		List<String> categories = new ArrayList<>();

		for (Check check : plugin.getChecks().getChecksWithType(type)) {
			if (!categories.contains(check.getCategory()))
				categories.add(check.getCategory());
		}

		int size;
		for (size = 9; size < 54; size += 9) {
			if (size > categories.size())
				break;
		}

		Inventory inv = Bukkit.createInventory(null, size, MSG.camelCase(type + ""));

		for (String category : categories) {
			ItemStack item = new ItemStack(Material.PAPER, plugin.getChecks().getChecksByCategory(category).size());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MSG.color("&a&l" + category));
			int vls = getTotalVl(category), triggers = getTotalTriggers(category);
			List<String> lore = new ArrayList<>();

			lore.add(MSG.color("&7Number of checks: &e" + plugin.getChecks().getChecksByCategory(category).size()));
			lore.add(MSG.color("&7" + type.getDescription()));
			lore.add(MSG.color("&7VLs: &e" + vls));
			lore.add(MSG.color("&7Triggers: &e" + triggers));
			lore.add(MSG.color("&7Ratio: &e" + ((triggers == 0) ? 0 : (vls / triggers))));
			boolean enabled = plugin.config
					.getBoolean("Checks." + MSG.camelCase(type + "") + "." + category + ".Enabled")
					&& plugin.config.getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled");
			lore.add(MSG.color("&7Enabled: " + MSG.TorF(enabled)));

			if (!plugin.config.getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled")) {
				lore.add(MSG.color("&cThe " + MSG.camelCase(type + "") + " check type is disabled"));
				lore.add(MSG.color("&cenable it to modify these settings."));
			}

			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		return inv;
	}

	public Inventory getInventory(String category) {
		List<Check> checks = new ArrayList<>();

		checks.addAll(plugin.getChecks().getActiveChecks().stream()
				.filter((check) -> check.getCategory().equals(category)).collect(Collectors.toList()));
		int size;
		for (size = 9; size < 54; size += 9) {
			if (size > checks.size())
				break;
		}

		Inventory inv = Bukkit.createInventory(null, size, category);

		for (Check check : checks) {
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MSG.color("&a&l" + check.getDebugName()));
			int vls = getTotalVl(check), triggers = getTotalTriggers(check);
			List<String> lore = new ArrayList<>();
			lore.add(MSG.color("&7VLs: &e" + vls));
			lore.add(MSG.color("&7Triggers: &e" + triggers));
			lore.add(MSG.color("&7Ratio: " + ((triggers == 0) ? 0 : (vls / triggers))));

			lore.add(MSG.color(
					"&7Enabled: " + MSG.TorF(plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "")
							+ "." + check.getCategory() + "." + check.getDebugName() + ".Enabled")
							&& plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled")
							&& plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "."
									+ check.getCategory() + ".Enabled"))));

			if (!plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled")) {
				lore.add(MSG.color("&cThe " + MSG.camelCase(check.getType() + "") + " category is disabled"));
				lore.add(MSG.color("&cenable it to modify these settings"));
			}

			if (!plugin.config.getBoolean(
					"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled")) {
				lore.add(MSG.color("&cThe " + check.getCategory() + " hack is disabled"));
				lore.add(MSG.color("&cenable it to modify these settings"));
			}

			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(item);
		}

		return inv;
	}

	public YamlConfiguration getStatsFile() {
		return stats;
	}

	public void saveData() {
		try {
			stats.save(statsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTotalVl(CheckType checkType) {
		int result = 0;
		for (Check check : plugin.getChecks().getChecksWithType(checkType)) {
			result += stats.getInt(check.getDebugName() + ".vl");
		}
		return result;
	}

	public int getTotalTriggers(CheckType checkType) {
		int result = 0;
		for (Check check : plugin.getChecks().getChecksWithType(checkType)) {
			result += stats.getInt(check.getDebugName() + ".triggers");
		}
		return result;
	}

	public int getTotalVl(Check check) {
		return stats.getInt(check.getDebugName() + ".vl");
	}

	public int getTotalTriggers(Check check) {
		return stats.getInt(check.getDebugName() + ".triggers");
	}

	public int getTotalVl(String category) {
		int vl = 0;
		for (Check check : plugin.getChecks().getActiveChecks()) {
			if (!check.getCategory().equals(category))
				continue;
			vl += getTotalVl(check);
		}
		return vl;
	}

	public int getTotalTriggers(String category) {
		int vl = 0;
		for (Check check : plugin.getChecks().getActiveChecks()) {
			if (!check.getCategory().equals(category))
				continue;
			vl += getTotalTriggers(check);
		}
		return vl;
	}

	public void addVl(Check check, int vl) {
		stats.set(check.getDebugName() + ".vl", stats.getInt(check.getDebugName() + ".vl") + vl);
	}

	public void addTrigger(Check check) {
		stats.set(check.getDebugName() + ".triggers", stats.getInt(check.getDebugName() + ".triggers") + 1);
	}

}
