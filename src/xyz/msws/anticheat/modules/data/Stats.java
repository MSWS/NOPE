package xyz.msws.anticheat.modules.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Checks;
import xyz.msws.anticheat.utils.MSG;

public class Stats extends AbstractModule {
	private File statsFile;

	private YamlConfiguration stats;
	private Checks checks;

	public Stats(NOPE plugin) {
		super(plugin);

		this.checks = plugin.getModule(Checks.class);
	}

	public Inventory getInventory() {
		int size;
		for (size = 9; size < 54; size += 9) {
			if (size > CheckType.values().length)
				break;
		}
		Inventory inv = Bukkit.createInventory(null, size, "All Checks");
		for (CheckType type : CheckType.values()) {
			ItemStack item = new ItemStack(Material.PAPER, Math.max(checks.getChecksWithType(type).size(), 1));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MSG.color(
					((plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled")) ? "&a" : "&c")
							+ "&l" + MSG.camelCase(type + "")));
			int vls = getTotalVl(type), triggers = getTotalTriggers(type);
			List<String> lore = new ArrayList<>();

			lore.add(MSG.color("&8" + type.getDescription()));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal Checks"));
			lore.add(MSG.color("&4" + checks.getChecksWithType(type).size()));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal VLs"));
			lore.add(MSG.color("&4" + vls));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal Triggers"));
			lore.add(MSG.color("&4" + triggers));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lRatio"));
			lore.add(MSG.color("&e" + ((triggers == 0) ? 0 : (vls / triggers))));
			lore.add(MSG.color(""));

			lore.add(MSG.color("&7Enabled: "
					+ MSG.TorF(plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled"))));
			lore.add(MSG.color("&7&o(Right-Click to toggle)"));

			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(item);
		}

		ItemStack bans = new ItemStack(Material.IRON_BLOCK);
		ItemMeta meta = bans.getItemMeta();

		meta.setDisplayName(MSG.color("&eBan Statistics"));

		List<String> lore = new ArrayList<>();

		Calendar date = Calendar.getInstance();

		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		lore.add(MSG.color("&b30 Days: &2" + getBans((long) (System.currentTimeMillis() - 2.628e+9))));
		lore.add(MSG.color("&b7 Days: &2" + getBans((long) (System.currentTimeMillis() - 6.048e+8))));
		lore.add(MSG.color("&b24 Hours: &2" + getBans((long) (System.currentTimeMillis() - 8.64e+7))));
		lore.add(MSG.color("&bStart of day: &2" + getBans(date.getTimeInMillis())));
		lore.add(MSG.color("&b1 Hour: &2" + getBans((long) (System.currentTimeMillis() - 3.6e+6))));
		meta.setLore(lore);
		bans.setItemMeta(meta);
		inv.addItem(bans);
		return inv;
	}

	public Inventory getInventory(CheckType type) {
		List<String> categories = new ArrayList<>();
		for (Check check : checks.getChecksWithType(type)) {
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
			ItemStack item = new ItemStack(Material.PAPER, checks.getChecksByCategory(category).size());
			ItemMeta meta = item.getItemMeta();
			boolean enabled = plugin.getConfig()
					.getBoolean("Checks." + MSG.camelCase(type + "") + "." + category + ".Enabled")
					&& plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled");
			meta.setDisplayName(MSG.color((enabled ? "&a" : "&c") + "&l" + category));
			int vls = getTotalVl(category), triggers = getTotalTriggers(category);
			List<String> lore = new ArrayList<>();

			lore.add(MSG.color("&c&lTotal Checks"));
			lore.add(MSG.color("&4" + checks.getChecksByCategory(category).size()));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal VLs"));
			lore.add(MSG.color("&4" + vls));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal Triggers"));
			lore.add(MSG.color("&4" + triggers));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lRatio"));
			lore.add(MSG.color("&e" + ((triggers == 0) ? 0 : (vls / triggers))));
			lore.add(MSG.color(""));

			lore.add(MSG.color("&7Enabled: " + MSG.TorF(enabled)));

			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(type + "") + ".Enabled")) {
				lore.add(MSG.color("&4&l&m========================"));
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
		List<Check> cs = checks.getChecksByCategory(category);
		int size;
		for (size = 9; size < 54; size += 9) {
			if (size > cs.size())
				break;
		}

		Inventory inv = Bukkit.createInventory(null, size, category);

		for (Check check : cs) {
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			boolean enabled = plugin.getConfig()
					.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "."
							+ check.getDebugName() + ".Enabled")
					&& plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled")
					&& plugin.getConfig().getBoolean(
							"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled");
			meta.setDisplayName(MSG.color((enabled ? "&a" : "&c") + "&l" + check.getDebugName()));
			int vls = getTotalVl(check), triggers = getTotalTriggers(check);
			List<String> lore = new ArrayList<>();
			lore.add(MSG.color("&c&lTotal VLs"));
			lore.add(MSG.color("&4" + vls));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lTotal Triggers"));
			lore.add(MSG.color("&4" + triggers));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&c&lRatio"));
			lore.add(MSG.color("&e" + ((triggers == 0) ? 0 : (vls / triggers))));
			lore.add(MSG.color(""));
			lore.add(MSG.color("&7Enabled: " + MSG.TorF(enabled)));

			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled")) {
				lore.add(MSG.color("&4&l&m========================"));
				lore.add(MSG.color("&cThe " + MSG.camelCase(check.getType() + "") + " category is disabled"));
				lore.add(MSG.color("&cenable it to modify these settings"));
			}

			if (!plugin.getConfig().getBoolean(
					"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled")) {
				lore.add(MSG.color("&4&l&m========================"));
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
		if (stats == null || statsFile == null)
			return;
		try {
			stats.save(statsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTotalVl(CheckType checkType) {
		int result = 0;
		for (Check check : checks.getChecksWithType(checkType)) {
			result += stats.getInt(check.getDebugName() + ".vl");
		}
		return result;
	}

	public int getTotalTriggers(CheckType checkType) {
		int result = 0;
		for (Check check : checks.getChecksWithType(checkType)) {
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
		for (Check check : checks.getChecksByCategory(category)) {
			vl += getTotalVl(check);
		}
		return vl;
	}

	public int getTotalTriggers(String category) {
		int vl = 0;
		for (Check check : checks.getChecksByCategory(category)) {
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

	public int getBans(long sinceTime) {
		int amo = 0;
		for (String ban : stats.getStringList("Bans.Timings")) {
			if (Double.parseDouble(ban) >= sinceTime)
				amo++;
		}
		return amo;
	}

	public int getAllBans() {
		return getBans(0);
	}

	public void addBan() {
		List<String> timings = stats.getStringList("Bans.Timings");
		if (timings == null)
			timings = new ArrayList<>();
		timings.add(System.currentTimeMillis() + "");
		stats.set("Bans.Timings", timings);
	}

	@Override
	public void enable() {
		new File(plugin.getDataFolder(), "/stats/").mkdir();

		statsFile = new File(plugin.getDataFolder(), "/stats/stats.yml");

		try {
			statsFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stats = YamlConfiguration.loadConfiguration(statsFile);
	}

	@Override
	public void disable() {
		saveData();
		statsFile = null;
		stats = null;
	}
}
