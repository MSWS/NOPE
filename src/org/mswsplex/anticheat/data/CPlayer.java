package org.mswsplex.anticheat.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.Timing;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class CPlayer {
	private OfflinePlayer player;
	private UUID uuid;

	private HashMap<String, Object> tempData;

	private File saveFile, dataFile;
	private YamlConfiguration data;

	private Location lastSafe;

	private AntiCheat plugin;

	public CPlayer(OfflinePlayer player, AntiCheat plugin) {
		this.plugin = plugin;
		this.player = player;
		this.uuid = player.getUniqueId();

		this.tempData = new HashMap<>();

		dataFile = new File(plugin.getDataFolder() + "/data");
		dataFile.mkdir();

		saveFile = new File(plugin.getDataFolder() + "/data/" + (uuid + "").replace("-", "") + ".yml");
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = YamlConfiguration.loadConfiguration(saveFile);
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	public List<String> getTempEntries() {
		return new ArrayList<>(tempData.keySet());
	}

	public YamlConfiguration getDataFile() {
		return this.data;
	}

	public boolean bypassCheck(Check check) {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;
		return online.hasPermission("anticheat.bypass." + check.getDebugName());
	}

	public void setTempData(String id, Object obj) {
		tempData.put(id, obj);
	}

	public void setSaveData(String id, Object obj) {
		data.set(id, obj);
	}

	public void setSaveData(String id, Object obj, boolean save) {
		setSaveData(id, obj);
		if (save)
			saveData();
	}

	public void saveData() {
		try {
			data.save(saveFile);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void clearTempData() {
		tempData.clear();
	}

	public void clearSaveData() {
		saveFile.delete();
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = YamlConfiguration.loadConfiguration(saveFile);
	}

	public Object getTempData(String id) {
		return tempData.get(id);
	}

	public String getTempString(String id) {
		return (String) getTempData(id).toString();
	}

	public double getTempDouble(String id) {
		return hasTempData(id) ? (double) getTempData(id) : 0;
	}

	public int getTempInteger(String id) {
		return hasTempData(id) ? (int) getTempData(id) : 0;
	}

	public boolean hasTempData(String id) {
		return tempData.containsKey(id);
	}

	public Object getSaveData(String id) {
		return data.get(id);
	}

	public boolean hasSaveData(String id) {
		return data.contains(id);
	}

	public String getSaveString(String id) {
		return (String) getSaveData(id).toString();
	}

	public double getSaveDouble(String id) {
		return hasSaveData(id) ? (double) getSaveData(id) : 0;
	}

	public void removeSaveData(String id) {
		data.set(id, null);
	}

	public void removeTempData(String id) {
		tempData.remove(id);
	}

	public int getSaveInteger(String id) {
		return hasSaveData(id) ? (int) getSaveData(id) : 0;
	}

	public <T> T getSaveData(String id, Class<T> cast) {
		return cast.cast(getSaveData(id));
	}

	public <T> T getTempData(String id, Class<T> cast) {
		return cast.cast(getTempData(id));
	}

	public int getTotalVL() {
		ConfigurationSection vlSection = getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return 0;

		int amo = 0;
		for (String hack : vlSection.getKeys(false)) {
			amo += vlSection.getInt(hack);
		}
		return amo;
	}

	public void flagHack(Check check, int vl) {
		if (timeSince("joinTime") < 5000) {
			if (plugin.devMode())
				MSG.tell("anticheat.message.dev", "&4&l[&c&lDEV&4&l] &e" + player.getName() + " &7failed &c"
						+ check.getDebugName() + " &8[CANCELLED]");
			return;
		}
		if (plugin.devMode())
			MSG.tell("anticheat.message.dev",
					"&4&l[&c&lDEV&4&l] &e" + player.getName() + " &7failed &c" + check.getDebugName() + " &4+" + vl);

		int nVl = getSaveInteger("vls." + check.getCategory().toLowerCase()) + vl;
		String color = MSG.getVlColor(nVl);

		double lastSent = timeSince(color + check.getCategory());

		if (lastSafe != null && player.isOnline() && plugin.config.getBoolean("LagBack") && check.lagBack())
			((Player) player).teleport(lastSafe);

		if (!plugin.devMode()) {
			if (lastSent > plugin.config.getDouble("SecondsMinimum") && nVl > plugin.config.getInt("Minimum")) {
				MSG.tell("anticheat.message.normal",
						"&4&l[&c&lNOPE&4&l] &e" + player.getName() + " &7failed a"
								+ ((check.getCategory().toLowerCase().charAt(0) + "").matches("(a|e|i|o|u)") ? "n" : "")
								+ " " + color + check.getCategory() + " &7check. &7(VL: &e&o" + nVl + "&7)");
				setTempData(color + check.getCategory(), (double) System.currentTimeMillis());
			}
		}

		setSaveData("vls." + check.getCategory().toLowerCase(), nVl);

		if (nVl >= plugin.config.getInt("VlForInstaBan")) {
			ban(check, Timing.INSTANT);
		}
		if (nVl >= plugin.config.getInt("VlForBanwave") && !hasSaveData("isBanwaved")) {
			MSG.tell("anticheat.message.banwave",
					"&4&l[&c&lNOPE&4&l] &e" + player.getName() + " &7is now queued for a banwave.");
			setSaveData("isBanwaved", check.getCategory());
		}
	}

	public void ban(Check check, Timing timing) {
		ban(check.getCategory(), timing);
	}

	public void ban(String check, Timing timing) {
		clearSaveData();
		removeTempData("autoClickerTimes");

		if (plugin.devMode())
			return;

		if (timing == Timing.BANWAVE) {
			for (String line : plugin.config.getStringList("CommandsForBanwave")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						line.replace("%player%", player.getName()).replace("%hack%", check));
			}
		} else {
			for (String line : plugin.config.getStringList("CommandsForBan")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						line.replace("%player%", player.getName()).replace("%hack%", check));
			}
		}
	}

	public Location getLastSafeLocation() {
		if (lastSafe == null)
			return ((Player) player).getLocation();
		return lastSafe;
	}

	public boolean isOnGround() {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;
		if (isInWeirdBlock())
			return true;
		if (online.isFlying())
			return false;

		return online.getLocation().getY() % .5 == 0;
	}

	public boolean isInWeirdBlock() {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;

		String[] nonfull = { "FENCE", "SOUL_SAND", "CHEST", "BREWING_STAND", "END_PORTAL_FRAME", "ENCHANTMENT_TABLE",
				"BED", "SLAB", "STEP", "CAKE", "DAYLIGHT_SENSOR", "CAULDRON", "DIODE", "REDSTONE_COMPARATOR",
				"TRAP_DOOR", "TRAPDOOR", "WATER_LILLY", "SNOW", "CACTUS", "WEB", "HOPPER" };
		Material type = online.getLocation().getBlock().getType();
		for (String mat : nonfull) {
			if (type.toString().contains(mat))
				return true;
		}

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y <= 1; y++) {
					Material material = online.getLocation().clone().add(x, y, z).getBlock().getType();
					for (String mat : nonfull) {
						if (material.toString().contains(mat))
							return true;
					}
				}

			}
		}
		return false;
	}

	public boolean isBlockNearby(Material mat) {
		return isBlockNearby(mat, 0);
	}

	public boolean isBlockNearby(String mat) {
		return isBlockNearby(mat, 0);
	}

	public boolean isBlockNearby(Material mat, int range) {
		return isBlockNearby(mat, 1, 0);
	}

	public boolean isBlockNearby(String mat, int range) {
		return isBlockNearby(mat, 1, 0);
	}

	public boolean isBlockNearby(Material mat, double yOffset) {
		return isBlockNearby(mat, 1, yOffset);
	}

	public boolean isBlockNearby(String mat, double yOffset) {
		return isBlockNearby(mat, 1, yOffset);
	}

	public boolean isBlockNearby(Material mat, int range, double yOffset) {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;
		for (int x = -range; x <= range; x++) {
			for (int z = -range; z <= range; z++) {
				Material material = online.getLocation().clone().add(x, yOffset, z).getBlock().getType();
				if (material == mat)
					return true;
			}
		}
		return false;
	}

	public boolean isBlockNearby(String mat, int range, double yOffset) {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;
		for (int x = -range; x <= range; x++) {
			for (int z = -range; z <= range; z++) {
				Material material = online.getLocation().clone().add(x, yOffset, z).getBlock().getType();
				if (material.toString().contains(mat))
					return true;
			}
		}
		return false;
	}

	public void setLastSafeLocation(Location loc) {
		this.lastSafe = loc;
	}

	public double timeSince(String action) {
		return System.currentTimeMillis() - getTempDouble(action);
	}

	public boolean hasMovementRelatedPotion() {
		if (!player.isOnline())
			return false;
		PotionEffectType[] movement = { PotionEffectType.SPEED, PotionEffectType.JUMP, PotionEffectType.SLOW };

		Player online = (Player) player;

		for (PotionEffectType type : movement) {
			if (online.hasPotionEffect(type))
				return true;
		}

		return false;
	}

	public boolean isInClimbingBlock() {
		if (!player.isOnline())
			return false;

		Player online = (Player) player;

		Block block = online.getLocation().getBlock();

		return block.getType() == Material.LADDER || block.getType() == Material.VINE;
	}

	public boolean isBlockAbove() {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (online.getLocation().clone().add(x, 2, z).getBlock().getType().isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

	public double distanceToGround() {
		if (!player.isOnline())
			return 0;
		Player online = (Player) player;

		Location vertLine = online.getLocation();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}
		return online.getLocation().distance(vertLine);
	}

	public boolean isRedstoneNearby() {
		if (!player.isOnline())
			return false;
		Player online = (Player) player;

		List<Material> blockTypes = Arrays.asList(Material.PISTON_BASE, Material.PISTON_STICKY_BASE);
		int range = 2;
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {
					if (blockTypes.contains(online.getLocation().clone().add(x, y, z).getBlock().getType()))
						return true;
				}
			}
		}
		return false;
	}
}
