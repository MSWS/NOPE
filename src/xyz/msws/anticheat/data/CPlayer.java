package xyz.msws.anticheat.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import xyz.msws.anticheat.checks.Global.Stat;
import xyz.msws.anticheat.events.PlayerFlagEvent;
import xyz.msws.anticheat.utils.MSG;
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

	private Map<Stat, Object> tempData;

	private File saveFile, dataFile;
	private YamlConfiguration data;

	private Log log;

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

	public List<Stat> getTempEntries() {
		return new ArrayList<>(tempData.keySet());
	}

	public Map<Stat, Object> getTempData() {
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

	public void setTempData(Stat id, Object obj) {
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

	public int getVL(String category) {
		return getDataFile().getInt("vls." + category);
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

		PlayerFlagEvent pfe = new PlayerFlagEvent(this, check);
		Bukkit.getPluginManager().callEvent(pfe);

		if (timeSince(Stat.JOIN_TIME) < 5000)
			return;

		if (pfe.isCancelled())
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

		if (bypassCheck(check)) {
			addLogMessage("Flagged check:" + check.getDebugName() + " [PERM] time:" + System.currentTimeMillis());
			return;
		}

		setTempData(Stat.FLAGGED, (double) System.currentTimeMillis());

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

		MSG.sendPluginMessage(null, "setvl:" + player.getName() + " " + check.getCategory() + " " + nVl);
		setSaveData("vls." + check.getCategory(), nVl);

		plugin.getActionManager().runActions(player, check.getCategory(), check);

		plugin.getStats().addTrigger(check);
		plugin.getStats().addVl(check, vl);

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

	public String saveLog(Check check) {
		new File(plugin.getDataFolder(), "logs/").mkdirs();

		String token = "";

		if (plugin.getConfig().getString("Log", "none").equalsIgnoreCase("none")) {
			return null;
		}

		List<String> lines = log.getLinesFrom(0);

		if (plugin.getConfig().getString("Log").equalsIgnoreCase("file")) {
			token = MSG.genUUID(16);
			File logFile = new File(plugin.getDataFolder(), "logs/" + token + ".log");

			try {
				Files.write(logFile.toPath(), lines, StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				token = Utils.post(String.join("\n", lines), false);
				token = token.substring(token.indexOf("/") + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return token;
	}

	public void addLogMessage(String msg) {
		log.addLine(msg);
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
		for (Check h : plugin.getChecks().getAllChecks())
			this.setSaveData("vls." + h.getCategory(), 0);
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

	public long timeSince(Stat action) {
		return System.currentTimeMillis() - getTempData(action, Number.class, 0L).longValue();
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
		return (online.getLocation().getY() - vertLine.getBlockY() - 1) + online.getLocation().getY() % 1;
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
