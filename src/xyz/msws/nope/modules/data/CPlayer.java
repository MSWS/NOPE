package xyz.msws.nope.modules.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

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
import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.player.PlayerFlagEvent;
import xyz.msws.nope.modules.actions.ActionManager;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.utils.MSG;
import xyz.msws.nope.utils.Utils;

/**
 * Oh my this class is quite the heap
 * 
 * @author imodm
 *
 */
public class CPlayer {
	private UUID uuid;
	private EnumMap<Stat, Long> tempData;
	private File saveFile, dataFile;
	private YamlConfiguration data;
	private Log log;
	private Location lastSafe;
	private NOPE plugin;
	private String currentInventory;
	private Map<String, PlayerOption> options;

	public CPlayer(UUID player, NOPE plugin) {
		this.plugin = plugin;
		this.uuid = player;
		this.log = new Log(player);

		this.tempData = new EnumMap<>(Stat.class);

		dataFile = new File(plugin.getDataFolder() + "/data");
		dataFile.mkdir();

		saveFile = new File(plugin.getDataFolder() + "/data/" + (uuid + "").replace("-", "") + ".yml");
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = YamlConfiguration.loadConfiguration(saveFile);

		options = new HashMap<>();
		options.put("scoreboard", new PlayerOption(this, "scoreboard", true));
		options.put("notifications", new PlayerOption(this, "notifications", true));
		options.put("reports", new PlayerOption(this, "reports", true));
	}

	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Set the current open inventory (for internal plugin usage only)
	 * 
	 * @param inv
	 */
	public void setInventory(String inv) {
		this.currentInventory = inv;
	}

	@Nullable
	public String getInventory() {
		return this.currentInventory;
	}

	/**
	 * Returns the {@link Bukkit#getOfflinePlayer(UUID)} result of the
	 * {@link CPlayer#uuid}.
	 * 
	 * @return
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	/**
	 * Get the temporary entries of data.
	 * 
	 * @return
	 */
	public List<Stat> getTempEntries() {
		return new ArrayList<>(tempData.keySet());
	}

	/**
	 * Get the map entries of data.
	 * 
	 * @return
	 */
	public Map<Stat, Long> getTempData() {
		return tempData;
	}

	/**
	 * Get the data file of the player.
	 * 
	 * @return
	 */
	public YamlConfiguration getDataFile() {
		return data;
	}

	/**
	 * Check whether or not the player can bypass the specified check.
	 * 
	 * @param check
	 * @return
	 */
	public boolean bypassCheck(Check check) {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();
		if (online.hasPermission("nope.bypass." + check.getType()))
			return true;
		if (online.hasPermission("nope.bypass." + check.getCategory()))
			return true;
		return online.hasPermission("nope.bypass." + check.getType() + "." + check.getDebugName());
	}

	public void setTempData(Stat id, Long obj) {
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

	public Object getTempData(Stat id) {
		return tempData.get(id);
	}

	public String getTempString(Stat id) {
		return (String) getTempData(id).toString();
	}

	public double getTempDouble(Stat id) {
		return hasTempData(id) ? (double) getTempData(id) : 0;
	}

	public int getTempInteger(Stat id) {
		return hasTempData(id) ? (int) getTempData(id) : 0;
	}

	public boolean hasTempData(Stat id) {
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

	public void removeTempData(Stat id) {
		tempData.remove(id);
	}

	public <T> T getSaveData(String id, Class<T> cast) {
		return cast.cast(getSaveData(id));
	}

	public <T> T getTempData(Stat id, Class<T> cast) {
		return cast.cast(getTempData(id));
	}

	public <T> T getTempData(Stat id, Class<T> cast, T def) {
		if (getTempData(id) == null)
			return def;
		return cast.cast(getTempData(id));
	}

	@Nullable
	public PlayerOption getOption(String key) {
		return options.get(key);
	}

	public Map<String, PlayerOption> getOptionMappings() {
		return options;
	}

	/**
	 * Adds up and returns the total VL of all hacks
	 * 
	 * @return
	 */
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

	/**
	 * Returns the category that the player has the highest VL for
	 * 
	 * @return
	 */
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

	public int getVL(String category) {
		return getDataFile().getInt("vls." + category);
	}

	public void setVL(String category, int vl) {
		getDataFile().set("vls." + category, vl);
	}

	/**
	 * Returns the list of categories the player has VLs for
	 * 
	 * @return
	 */
	public List<String> getHackVls() {
		List<String> result = new ArrayList<>();

		ConfigurationSection vlSection = getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return result;
		for (String hack : vlSection.getKeys(false))
			result.add(hack);
		return result;
	}

	/**
	 * Flags the player for the specified hack, if possible you s hould use
	 * {@link CPlayer#flagHack(Check, int, String)} to provide more detail when
	 * possible
	 * 
	 * @param check
	 * @param vl
	 */
	public void flagHack(Check check, int vl) {
		flagHack(check, vl, null);
	}

	/**
	 * Flags the player for the specified hack and adds detail to how the player
	 * flagged the hack
	 * 
	 * @param check
	 * @param vl
	 * @param debug
	 */
	@SuppressWarnings("deprecation")
	public void flagHack(Check check, int vl, String debug) {
		if (!plugin.getConfig().getBoolean("Global"))
			return;
		PlayerFlagEvent pfe = new PlayerFlagEvent(this, check);
		Bukkit.getPluginManager().callEvent(pfe);

		if (timeSince(Stat.JOIN_TIME) < 5000)
			return;

		if (pfe.isCancelled())
			return;

		if (!check.getDebugName().contains("ManuallyIssued")) {
			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled"))
				return;
			if (!plugin.getConfig().getBoolean(
					"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "." + ".Enabled"))
				return;
			if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "."
					+ check.getCategory() + "." + check.getDebugName() + ".Enabled"))
				return;
		}

		if (bypassCheck(check)) {
			addLogMessage("Flagged check:" + check.getDebugName() + " [PERM] time:" + System.currentTimeMillis());
			return;
		}

		setTempData(Stat.FLAGGED, System.currentTimeMillis());
		setSaveData("lastFlag", System.currentTimeMillis());
		addLogMessage("Flagged " + check.getDebugName() + ": " + ChatColor.stripColor(MSG.color(debug)));

		if (plugin.getOption("dev").asBoolean()) {
			TextComponent component = new TextComponent(MSG.color("&4&l[&c&lDEV&4&l] &e" + getPlayer().getName()
					+ " &7failed &c" + check.getDebugName() + " &4+" + vl));

			if (debug != null && !debug.isEmpty()) {
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(MSG.color("&7" + debug)).create()));
				component.setClickEvent(
						new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ChatColor.stripColor(MSG.color(debug))));

			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("nope.message.dev"))
					p.spigot().sendMessage(component);
			}
		}

		int nVl = hasSaveData("vls." + check.getCategory())
				? getSaveData("vls." + check.getCategory(), Integer.class) + vl
				: vl;

		MSG.sendPluginMessage(null, "setvl:" + getPlayer().getName() + " " + check.getCategory() + " " + nVl);
		setSaveData("vls." + check.getCategory(), nVl);

		plugin.getModule(ActionManager.class).runActions(getPlayer(), check);

		plugin.getModule(Stats.class).addTrigger(check);
		plugin.getModule(Stats.class).addVl(check, vl);
	}

	/**
	 * This method should be called asynchronously
	 * 
	 * @param check
	 * @return
	 */
	public String saveLog(Check check) {
		if (plugin.getConfig().getString("Log", "none").equalsIgnoreCase("none"))
			return null;

		List<String> lines = new ArrayList<>();
		List<String> prefix = new ArrayList<>();
		prefix.add("Starting new log for " + getPlayer().getName() + " (" + getPlayer().getUniqueId() + ")");
		prefix.add("Server: " + plugin.getServerName());
		prefix.add("Server Version: " + Bukkit.getVersion() + " Bukkit: " + Bukkit.getBukkitVersion());
		prefix.add(
				"NOPE Version: " + plugin.getDescription().getVersion() + " (Online: " + plugin.getNewVersion() + ")");
		prefix.add("");
		prefix.add(getPlayer().getName() + " was ultimately banned for " + check.getDebugName() + " at a VL of "
				+ getVL(check.getCategory()));
		String cat = plugin.getConfig().isConfigurationSection("Actions." + check.getCategory()) ? check.getCategory()
				: "Default";
		prefix.add("The actions listed for " + check.getCategory() + " (" + cat + ")" + " are:");
		prefix.addAll(plugin.getConfig().getStringList("Actions." + check.getCategory()));

		prefix.add("");
		prefix.add("Temporary Data Dumb:");
		for (Entry<Stat, Long> entry : this.getTempData().entrySet()) {
			prefix.add("  " + entry.getKey() + ": " + entry.getValue() + " ("
					+ (System.currentTimeMillis() - entry.getValue()) + ")");
		}
		prefix.add("");
		prefix.add(getPlayer().getName() + "'s flags:");
		for (String hack : this.getHackVls()) {
			if (this.getVL(hack) == 0)
				continue;
			prefix.add(hack + ": " + this.getVL(hack));
		}
		prefix.add("");

		lines.addAll(prefix);
		lines.addAll(log.getLinesFrom(0));

		if (plugin.getConfig().getString("Log").equalsIgnoreCase("hastebin")) {
			try {
				String token = Utils.uploadHastebin(String.join("\n", lines));
				return token;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new File(plugin.getDataFolder(), "logs/").mkdirs();

		String token = "";

		token = MSG.genUUID(16);
		File logFile = new File(plugin.getDataFolder(), "logs/" + token + ".log");

		try {
			Files.write(logFile.toPath(), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return token;
	}

	public void addLogMessage(String msg) {
		log.addLine(msg);
	}

	public Log getLog() {
		return log;
	}

	@Nullable
	public Location getLastSafeLocation() {
		return lastSafe;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public boolean isOnGround() {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();
		if (isInWeirdBlock())
			return true;
		if (online.isFlying())
			return false;

		return online.getLocation().getY() % .5 == 0;
	}

	public void clearVls() {
		data.set("vls", null);
	}

	/**
	 * Returns whether or not the player is in a strange block that has awkward
	 * collision
	 * 
	 * @return
	 */
	public boolean isInWeirdBlock() {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();

		String[] nonfull = { "FENCE", "SOUL_SAND", "CHEST", "BREWING_STAND", "END_PORTAL_FRAME", "ENCHANTMENT_TABLE",
				"BED", "SLAB", "STEP", "CAKE", "DAYLIGHT_SENSOR", "CAULDRON", "DIODE", "REDSTONE_COMPARATOR",
				"TRAP_DOOR", "TRAPDOOR", "WATER_LILLY", "SNOW", "CACTUS", "WEB", "HOPPER", "SWEET_BERRY_BUSH",
				"SCAFFOLDING" };

		if (timeSince(Stat.COBWEB) < 100)
			return true;

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

	/**
	 * Returns if the specified Material is within a 3x1x3 range of the player
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(Material mat) {
		return isBlockNearby(mat, 0);
	}

	/**
	 * Returns if any of the materials within a 3x1x3 range of the player contain
	 * the specified string, eg: GLASS, BED, etc. would detect all forms of GLASS
	 * and BED.
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(String mat) {
		return isBlockNearby(mat, 0);
	}

	/**
	 * Returns if the specified Material is within a rangex1xrange range of the
	 * player
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(Material mat, int range) {
		return isBlockNearby(mat, 1, 0);
	}

	/**
	 * Returns if any of the materials within a rangex1xrange range of the player
	 * contain the specified string, eg: GLASS, BED, etc. would detect all forms of
	 * GLASS and BED.
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(String mat, int range) {
		return isBlockNearby(mat, 1, 0);
	}

	/**
	 * Returns if the specified Material is within a 3x1x3 range of the player,
	 * offset by @param yOffset
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(Material mat, double yOffset) {
		return isBlockNearby(mat, 1, yOffset);
	}

	/**
	 * Returns if any of the materials within a 3x1x3 (offset by @param yOffset)
	 * range of the player contain the specified string, eg: GLASS, BED, etc. would
	 * detect all forms of GLASS and BED.
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(String mat, double yOffset) {
		return isBlockNearby(mat, 1, yOffset);
	}

	/**
	 * Returns if the specified Material is within a rangex1xrange range of the
	 * player, offset by @param yOffset
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(Material mat, int range, double yOffset) {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();
		for (int x = -range; x <= range; x++) {
			for (int z = -range; z <= range; z++) {
				Material material = online.getLocation().clone().add(x, yOffset, z).getBlock().getType();
				if (material == mat)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns if any of the materials within a rangex1xrange (offset by @param
	 * yOffset) range of the player contain the specified string, eg: GLASS, BED,
	 * etc. would detect all forms of GLASS and BED.
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isBlockNearby(String mat, int range, double yOffset) {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();
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

	public long timeSince(Stat action) {
		return System.currentTimeMillis() - getTempData(action, Number.class, 0L).longValue();
	}

	/**
	 * Check if the player has speed, jump, or levitation
	 * 
	 * @return
	 */
	private final PotionEffectType[] movement = new PotionEffectType[] { PotionEffectType.SPEED, PotionEffectType.JUMP,
			PotionEffectType.LEVITATION };

	public boolean hasMovementRelatedPotion() {
		if (!getPlayer().isOnline())
			return false;

		Player online = getPlayer().getPlayer();

		for (PotionEffectType type : movement) {
			if (online.hasPotionEffect(type))
				return true;
		}

		return false;
	}

	/**
	 * Check if the player is in ladder, vine, or scaffolding
	 * 
	 * @return
	 */
	public boolean isInClimbingBlock() {
		if (!getPlayer().isOnline())
			return false;

		Player online = getPlayer().getPlayer();

		Block block = online.getLocation().getBlock();

		return block.getType() == Material.LADDER || block.getType() == Material.VINE
				|| block.getType() == Material.SCAFFOLDING || block.getType() == Material.TWISTING_VINES_PLANT
				|| block.getType() == Material.WEEPING_VINES_PLANT;
	}

	/**
	 * Checks any <b>surrounding</b> blocks to see if they are
	 * {@link Material#isSolid()}.
	 * 
	 * @return
	 */
	public boolean isBlockAbove() {
		if (!getPlayer().isOnline())
			return false;
		Player online = getPlayer().getPlayer();

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Block b = online.getEyeLocation().clone().add(x, 0, z).getBlock();
				if (b.getType().isSolid() || b.getRelative(BlockFace.UP).getType().isSolid())
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns the distance to the first {@link Material#isSolid()} block this does
	 * not take into account edges.
	 * 
	 * @return
	 */
	public double distanceToGround() {
		if (!getPlayer().isOnline())
			return 0;
		Player online = getPlayer().getPlayer();

		Location vertLine = online.getLocation();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}
		return (online.getLocation().getY() - vertLine.getBlockY() - 1) + online.getLocation().getY() % 1;
	}
}
