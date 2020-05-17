package xyz.msws.anticheat.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.Timing;
import xyz.msws.anticheat.utils.MSG;
//import xyz.msws.punish.managers.BanManager;
import xyz.msws.anticheat.utils.Utils;

/**
 * Oh my this class is quite the heap
 * 
 * @author imodm
 *
 */
public class CPlayer {
	private OfflinePlayer player;
	private UUID uuid;

	private Map<String, Object> tempData;

	private File saveFile, dataFile;
	private YamlConfiguration data;

	private Location lastSafe;

	private NOPE plugin;

	public CPlayer(OfflinePlayer player, NOPE plugin) {
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

	public Map<String, Object> getTempData() {
		return tempData;
	}

	public YamlConfiguration getDataFile() {
		return data;
	}

	public boolean bypassCheck(Check check) {
		if (!player.isOnline())
			return false;
		Player online = player.getPlayer();
		if (online.hasPermission("nope.bypass." + check.getType()))
			return true;
		if (online.hasPermission("nope.bypass." + check.getCategory()))
			return true;
		if (online.hasPermission("nope.bypass." + check.getType() + "." + check.getCategory()))
			return true;
		return online.hasPermission("nope.bypass." + check.getType() + "." + check.getDebugName());
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

	public boolean usingElytra() {
		if (!player.isOnline())
			return false;
		return player.getPlayer().isGliding();
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

	public String getHighestHack() {
		ConfigurationSection vlSection = getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return "None";

		int high = 0;
		String highest = "None";
		for (String hack : vlSection.getKeys(false)) {
			if (vlSection.getInt(hack) > high) {
				high = vlSection.getInt(hack);
				highest = hack;
			}
		}
		return highest;
	}

	public List<String> getHackVls() {
		List<String> result = new ArrayList<>();

		ConfigurationSection vlSection = getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return result;
		for (String hack : vlSection.getKeys(false)) {
			result.add(hack);
		}

		return result;
	}

	public void flagHack(Check check, int vl) {
		flagHack(check, vl, null);
	}

	@SuppressWarnings("unchecked")
	public void flagHack(Check check, int vl, String debug) {
		if (!plugin.getConfig().getBoolean("Global"))
			return;

		if (!check.getDebugName().equals("ManuallyIssued")) {
			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled"))
				return;
			if (!plugin.getConfig().getBoolean(
					"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "." + ".Enabled"))
				return;
			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "."
					+ check.getCategory() + "." + check.getDebugName() + ".Enabled"))
				return;
		}

		if (timeSince("joinTime") < 5000) {
			if (plugin.devMode())
				MSG.tell("nope.message.dev", "&4&l[&c&lDEV&4&l] &e" + player.getName() + " &7failed &c"
						+ check.getDebugName() + " &8[CANCELLED]");
			return;
		}

		if (bypassCheck(check)) {
			addLogMessage("Flagged check:" + check.getDebugName() + " [PERM] time:" + System.currentTimeMillis());
			return;
		}

		setTempData("lastFlag", (double) System.currentTimeMillis());

		if (plugin.devMode()) {

			TextComponent component = new TextComponent(MSG.color(
					"&4&l[&c&lDEV&4&l] &e" + player.getName() + " &7failed &c" + check.getDebugName() + " &4+" + vl));

			if (debug != null && !debug.isEmpty()) {
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(MSG.color("&7" + debug)).create()));
				component.setClickEvent(
						new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ChatColor.stripColor(MSG.color(debug))));

			}

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("nope.message.dev"))
					player.spigot().sendMessage(component);
			}
		}

		int nVl = hasSaveData("vls." + check.getCategory())
				? getSaveData("vls." + check.getCategory(), Integer.class) + vl
				: vl;

		String color = MSG.getVlColor(nVl);

		double lastSent = timeSince(color + check.getCategory());

		teleport: if (lastSafe != null && player.isOnline() && plugin.getConfig().getBoolean("SetBack")
				&& check.lagBack()) {

			ThreadLocalRandom rnd = ThreadLocalRandom.current();

			if (rnd.nextDouble() < .3)
				break teleport;

			player.getPlayer().teleport(lastSafe);
		}

		if (!plugin.devMode()) {
			if (lastSent > plugin.getConfig().getDouble("SecondsMinimum")
					&& nVl > plugin.getConfig().getInt("Minimum")) {
				String message = MSG.getString("Format.Regular",
						"&4&l[&c&lNOPE&4&l] &e%player%&7 failed a%n% %vlCol%%hack%&7check. (VL: &e&o%vl%&7)"),
						bungee = MSG.getString("Format.Bungee",
								"&4&l[&c&lNOPE&4&l] &e%player%&7 failed a%n% %vlCol%%hack%&7check. (VL: &e&o%vl%&7) &9[&b%server%&9]");

				message = message.replace("%player%", player.getName())
						.replace("%n%",
								(check.getCategory().toLowerCase().charAt(0) + "").matches("(a|e|i|o|u)") ? "n" : "")
						.replace("%vlCol%", color).replace("%hack%", check.getCategory()).replace("%vl%", nVl + "")
						.replace("%addVl%", vl + "");

				bungee = bungee.replace("%player%", player.getName())
						.replace("%n%",
								(check.getCategory().toLowerCase().charAt(0) + "").matches("(a|e|i|o|u)") ? "n" : "")
						.replace("%vlCol%", color).replace("%hack%", check.getCategory()).replace("%vl%", nVl + "")
						.replace("%addVl%", vl + "").replace("%server%", plugin.getServerName());

				MSG.tell("nope.message.normal", message);

				MSG.sendPluginMessage(null, "perm:nope.message.normal " + bungee);
				setTempData(color + check.getCategory(), (double) System.currentTimeMillis());
			}
		}

		MSG.sendPluginMessage(null, "setvl:" + player.getName() + " " + check.getCategory() + " " + nVl);
		setSaveData("vls." + check.getCategory(), nVl);

		plugin.getStats().addTrigger(check);
		plugin.getStats().addVl(check, vl);

		if (nVl >= plugin.getConfig().getInt("VlForBanwave") && !hasSaveData("isBanwaved")) {
			String message = MSG.getString("Format.Banwave",
					"&4&l[&c&lNOPE&4&l] &e%player%&7is now queued for a banwave.");

			message = message.replace("%player%", player.getName());

			MSG.tell("nope.message.banwave", message);
			MSG.sendPluginMessage(null, "perm:nope.message.banwave " + message);
			addLogMessage("");
			addLogMessage("BANWAVE check:" + check.getDebugName() + " VL: " + (nVl - vl) + " (+" + vl + ") TPS: "
					+ plugin.getTPS() + " time:" + System.currentTimeMillis());
			String token = MSG.genUUID(16);
			saveLog(check.getCategory(), Timing.BANWAVE, token);
			setSaveData("isBanwaved", check.getCategory());
			MSG.sendPluginMessage(null, "banwave:" + player.getName() + " " + check.getCategory());
			addLogMessage("BANWAVE Log ID: " + token);
			addLogMessage("");
		} else {
			addLogMessage("Flagged check:" + check.getDebugName() + " VL: " + (nVl - vl) + " (+" + vl + ") TPS: "
					+ plugin.getTPS() + " time:" + System.currentTimeMillis());
		}

		if (nVl >= plugin.getConfig().getInt("VlForInstaBan"))
			ban(check, Timing.INSTANT);

		List<String> lines = getSaveData("log", List.class);
		if (lines == null)
			lines = new ArrayList<>();

		setSaveData("log", lines);
	}

	public int getPing() {
		int ping = 0;

		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return ping;
	}

	public void ban(Check check, Timing timing) {
		ban(check.getCategory(), timing);
	}

	public void ban(String check, Timing timing) {
		String token = MSG.genUUID(16);

		if (!plugin.getConfig().getString("Log", "NONE").equalsIgnoreCase("NONE"))
			saveLog(check, timing, token);

		plugin.getStats().addBan();

		removeSaveData("log");
		removeSaveData("isBanwaved");
		removeTempData("autoClickerTimes");

		if (plugin.devMode())
			return;

		long time = 0;

		if (plugin.getConfig().getBoolean("UseBanManager")) {
			ConfigurationSection durations = plugin.getConfig().getConfigurationSection("BanDurations");
			if (durations == null) {
				MSG.error("BanDurations is not defined in the config.yml");
				return;
			}
			time = durations.getLong(check, durations.getLong("Default"));

			String format = String.join("\n", plugin.getConfig().getStringList("TempBanFormat"));

			if (time == -1)
				format = String.join("\n", plugin.getConfig().getStringList("PermBanFormat"));
			else
				format = format.replace("%duration%", MSG.getTime(time));

			format = placeholder(format, player, check, token, timing);
			plugin.getBanManager().ban(player.getUniqueId(), MSG.color(format), time);
		}

		if (timing == Timing.BANWAVE || timing == Timing.MANUAL_BANWAVE)
			for (String line : plugin.getConfig().getStringList("CommandsForBanwave"))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholder(line, player, check, token, timing));
		else
			for (String line : plugin.getConfig().getStringList("CommandsForBan"))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholder(line, player, check, token, timing));

		clearVls();
	}

	private String placeholder(String s, OfflinePlayer p, String check, String token, Timing timing) {
		s = s.replace("%hack%", check).replace("%vl%", getSaveData("vls." + check, Integer.class) + "")
				.replace("%world%", player.isOnline() ? player.getPlayer().getWorld().getName() : "Offline")
				.replace("%token%", token).replace("%timing%", timing.toString());
		return s;
	}

	@SuppressWarnings("unchecked")
	public void saveLog(String check, Timing timing, String token) {
		new File(plugin.getDataFolder(), "logs/instant").mkdirs();
		new File(plugin.getDataFolder(), "logs/banwaves").mkdirs();

		File logFile;

		if (timing == Timing.INSTANT) {
			logFile = new File(plugin.getDataFolder(), "logs/instant/" + token + ".log");
		} else {
			logFile = new File(plugin.getDataFolder(), "logs/banwaves/" + token + ".log");
		}

		List<String> prefix = new ArrayList<>();

		List<String> lines = getSaveData("log", List.class);
		if (lines == null)
			lines = new ArrayList<>();

		List<String> revised = new ArrayList<>();

		int longest = 0;

		long timeElapsed = 0;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			long time = 0;
			int tmpLong = 0;

			for (String k : line.split(" ")) {
				if (k.startsWith("time:")) {
					time = System.currentTimeMillis() - Long.parseLong(k.substring("time:".length()));
					line = line.replace(k,
							"[" + MSG.getTime(time).replace("milliseconds", "ms").replace("milliseconds", "ms").trim()
									+ "]");
					break;
				}
				if (k.startsWith("check:")) {
					if (k.split("check:")[1].length() > longest)
						tmpLong = k.split("check:")[1].length();
				}
			}
			if (time < 120000) {
				if (time > timeElapsed && line.startsWith("Flagged "))
					timeElapsed = time;
				revised.add(line);
				if (tmpLong > longest)
					longest = tmpLong;
			}
		}

		longest++;

		Map<String, Integer> flags = new HashMap<>();

		for (int i = 0; i < revised.size(); i++) {
			String line = revised.get(i);

			for (String k : line.split(" ")) {
				if (!k.startsWith("check:"))
					continue;

				String checkName = k.substring("check:".length());

				flags.put(checkName, flags.containsKey(checkName) ? flags.get(checkName) + 1 : 1);

				String replace = "";

				for (int a = checkName.length(); a < longest; a++) {
					replace += " ";
				}

				line = line.replace(k, checkName + replace + "|");

				revised.set(i, line);
			}
		}

		Date now = new Date(System.currentTimeMillis());

		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aaa z '-' MM/dd/yyyy");

//		int totalFlags = 0;
//		for (Entry<String, Integer> entry : flags.entrySet()) {
//			totalFlags += entry.getValue();
//		}

		prefix.add("Beginning log for " + player.getName() + " (" + uuid + ")");
		if (plugin.devMode())
			prefix.add("[WARNING] Developer Mode WAS Enabled During This Ban");
//		prefix.add("Hack: " + check + " (" + getSaveInteger("vls." + check) + "/" + getTotalVL() + ")");
		prefix.add("Hack: " + check + " (" + getSaveData("vls." + check, Integer.class) + "/" + getTotalVL() + ")");
		prefix.add("Timing: " + MSG.camelCase(timing + ""));
		prefix.add("Date: " + format.format(now));
		prefix.add("Time elapsed: " + MSG.getTime(timeElapsed));

//		double hackScore = totalFlags * getTotalVL();
//
//		prefix.add("Hack Score: " + hackScore / (timeElapsed / 120000.0) / 100000.0);

		prefix.add("");

		prefix.add("Total amount of checks");

		flags = flags.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e2, e1) -> e1, LinkedHashMap::new));

		for (Entry<String, Integer> entry : flags.entrySet()) {
			prefix.add(entry.getKey() + ": " + entry.getValue());
		}

		prefix.add("");

		revised.addAll(0, prefix);

		revised.add("");
		revised.add("Banning " + player.getName() + " for " + check + " (VL: "
				+ getSaveData("vls." + check, Integer.class) + ")");

		for (int i = 1; i < revised.size(); i++) {
			if (revised.get(i).isEmpty() && revised.get(i - 1).isEmpty()) {
				revised.remove(i);
				i--;
			}
		}

		// Saving files

		if (plugin.getConfig().getString("Log").equalsIgnoreCase("hastebin") && !plugin.devMode()) {
			// Don't upload a new hastebin file for every ban when dev mode is on, it's
			// extremely spammy
			String data = String.join("\n", revised);
			try {
				String link = Utils.post(data, false);
				revised.clear();
				revised.add("Hastebin Link: " + link);
				MSG.log("Generated new hastebin for banwave token &e" + token + "&7: &b" + link);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Files.write(logFile.toPath(), revised, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void addLogMessage(String msg) {
		List<String> lines = getSaveData("log", List.class);
		if (lines == null)
			lines = new ArrayList<>();

		lines.add(msg);
		setSaveData("log", lines);
	}

	public Location getLastSafeLocation() {
		if (lastSafe == null)
			return ((Player) player).getLocation();
		return lastSafe;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public boolean isOnGround() {
		if (!player.isOnline())
			return false;
		Player online = player.getPlayer();
		if (isInWeirdBlock())
			return true;
		if (online.isFlying())
			return false;

		return online.getLocation().getY() % .5 == 0;
	}

	public void clearVls() {
		for (String entry : getHackVls())
			removeSaveData("vls." + entry);
	}

	public boolean isInWeirdBlock() {
		if (!player.isOnline())
			return false;
		Player online = player.getPlayer();

		String[] nonfull = { "FENCE", "SOUL_SAND", "CHEST", "BREWING_STAND", "END_PORTAL_FRAME", "ENCHANTMENT_TABLE",
				"BED", "SLAB", "STEP", "CAKE", "DAYLIGHT_SENSOR", "CAULDRON", "DIODE", "REDSTONE_COMPARATOR",
				"TRAP_DOOR", "TRAPDOOR", "WATER_LILLY", "SNOW", "CACTUS", "WEB", "HOPPER", "SWEET_BERRY_BUSH",
				"SCAFFOLDING" };

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
		Player online = player.getPlayer();
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
		Player online = player.getPlayer();
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
		PotionEffectType[] movement = { PotionEffectType.SPEED, PotionEffectType.JUMP, PotionEffectType.SLOW,
				PotionEffectType.LEVITATION };

		Player online = player.getPlayer();

		for (PotionEffectType type : movement) {
			if (online.hasPotionEffect(type))
				return true;
		}

		return false;
	}

	public boolean isInClimbingBlock() {
		if (!player.isOnline())
			return false;

		Player online = player.getPlayer();

		Block block = online.getLocation().getBlock();

		return block.getType() == Material.LADDER || block.getType() == Material.VINE
				|| block.getType() == Material.SCAFFOLDING;
	}

	public boolean isBlockAbove() {
		if (!player.isOnline())
			return false;
		Player online = player.getPlayer();

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Block b = online.getEyeLocation().clone().add(x, 0, z).getBlock();
				if (b.getType().isSolid() || b.getRelative(BlockFace.UP).getType().isSolid())
					return true;
			}
		}
		return false;
	}

	public double distanceToGround() {
		if (!player.isOnline())
			return 0;
		Player online = player.getPlayer();

		Location vertLine = online.getLocation();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}
		return online.getLocation().distance(vertLine);
	}

	public boolean isRedstoneNearby() {
		if (!player.isOnline())
			return false;
		Player online = player.getPlayer();

		List<Material> blockTypes = Arrays.asList(Material.PISTON_HEAD, Material.STICKY_PISTON);
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
