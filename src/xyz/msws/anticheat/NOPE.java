package xyz.msws.anticheat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.anticheat.bans.AdvancedBanHook;
import xyz.msws.anticheat.bans.BanHook;
import xyz.msws.anticheat.bans.BanManagementHook;
import xyz.msws.anticheat.bans.LiteBansHook;
import xyz.msws.anticheat.bans.MaxBansHook;
import xyz.msws.anticheat.bans.NativeBanHook;
import xyz.msws.anticheat.checks.Banwave;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.Checks;
import xyz.msws.anticheat.checks.Global;
import xyz.msws.anticheat.checks.TPSChecker;
import xyz.msws.anticheat.commands.AntiCheatCommand;
import xyz.msws.anticheat.compatability.AbstractHook;
import xyz.msws.anticheat.compatability.CrazyEnchantsHook;
import xyz.msws.anticheat.compatability.McMMOHook;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.data.PlayerManager;
import xyz.msws.anticheat.data.Stats;
import xyz.msws.anticheat.listeners.GUIListener;
import xyz.msws.anticheat.listeners.LogImplementation;
import xyz.msws.anticheat.listeners.LoginAndQuit;
import xyz.msws.anticheat.listeners.MessageListener;
import xyz.msws.anticheat.listeners.UpdateCheckerListener;
import xyz.msws.anticheat.utils.MSG;
import xyz.msws.anticheat.utils.Metrics;
import xyz.msws.anticheat.utils.Metrics.CustomChart;

public class NOPE extends JavaPlugin {
	private FileConfiguration config, data, lang;
	private File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml");

	private PlayerManager pManager;
	private TPSChecker tpsChecker;
	private Checks checks;
	private Banwave banwave;
	private Stats stats;
	private BanHook banManager;

	private String serverName = "Unknown Server";

	private PluginInfo pluginInfo;

	private String newVersion = null;

	private Collection<AbstractHook> compatabilities = new ArrayList<>();

	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);

		MSG.plugin = this;

		MSG.log(checkConfigVersion());

		pManager = new PlayerManager(this);
		tpsChecker = new TPSChecker(this);

		banwave = new Banwave(this);

		checks = new Checks(this);
		checks.registerChecks();

		stats = new Stats(this);

		new Global(this);
		new AntiCheatCommand(this);

		new LogImplementation(this);
		new LoginAndQuit(this);
		new GUIListener(this);

//		new SBoard(this);

		banManager = hookBans();

		uploadCustomCharts();
		runUpdateCheck();

		compatabilities = loadCompatabilities();
		for (AbstractHook comp : compatabilities)
			MSG.log("Successfully registerd compatability modifier for " + MSG.FORMAT_INFO + comp.getName() + "&7.");

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));

		MSG.log("&aPlease report any bugs at the github. (https://github.com/MSWS/AntiCheat)");
	}

	private Collection<AbstractHook> loadCompatabilities() {
		Set<AbstractHook> cs = new HashSet<>();
		if (Bukkit.getPluginManager().isPluginEnabled("mcMMO"))
			cs.add(new McMMOHook(this));
		if (Bukkit.getPluginManager().isPluginEnabled("CrazyEnchantments"))
			cs.add(new CrazyEnchantsHook(this));
		return cs;
	}

	private String checkConfigVersion() {
		if (config.getString("ConfigVersion", "").equals(getDescription().getVersion()))
			return "You are using an up-to-date version of the config.";
		switch (config.getString("ConfigVersion", "")) {
			case "1.3.4":
				return "You are using a config from an old version however nothing has changed.";
			case "1.3.3":
				return "You are using an outdated config, the language file also has been changed.";
			case "1.3.2":
			case "1.3.1":
				return "You are using an outdated config, certain checks will be logged as disabled.";
			default:
				return "Your config version is unknown, it is strongly recommended you reset your config.";
		}
	}

	private BanHook hookBans() {
		if (Bukkit.getPluginManager().isPluginEnabled("AdvancedBan")) {
			MSG.log("Successfully hooked into AdvancedBans.");
			return new AdvancedBanHook();
		} else if (Bukkit.getPluginManager().isPluginEnabled("MaxBans")) {
			MSG.log("Successfully hooked into MaxBans.");
			return new MaxBansHook();
		} else if (Bukkit.getPluginManager().isPluginEnabled("BanManagement")) {
			MSG.log("Successfully hooked into BanManagement.");
			return new BanManagementHook();
		} else if (Bukkit.getPluginManager().isPluginEnabled("LiteBans")) {
			MSG.log("Successfully hooked into LiteBans.");
			return new LiteBansHook();
		} else {
			MSG.log("Unable to find a ban management plugin, using native support.");
			return new NativeBanHook();
		}
	}

	private void uploadCustomCharts() {
		Metrics metrics = new Metrics(this, 7422);
		CustomChart chart = new Metrics.SingleLineChart("bans", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return stats.getAllBans();
			}
		});
		metrics.addCustomChart(chart);

		chart = new Metrics.AdvancedBarChart("checkweights", new Callable<Map<String, int[]>>() {
			@Override
			public Map<String, int[]> call() throws Exception {
				Map<String, int[]> result = new HashMap<>();
				for (Check check : checks.getAllChecks()) {
					int[] arr = new int[2];
					arr[0] = stats.getTotalVl(check);
					arr[1] = stats.getTotalTriggers(check);
					result.put(check.getDebugName(), arr);
				}
				return result;
			}
		});
	}

	private void runUpdateCheck() {
		if (config.getBoolean("UpdateChecker.Enabled", true)) {
			if (config.getBoolean("UpdateChecker.InGame", true))
				new UpdateCheckerListener(this);
			pluginInfo = new PluginInfo(this, 64671);
			pluginInfo.fetch(pi -> {
				newVersion = pi.getVersion();
				String info = "";
				switch (pi.outdated()) {
					case DEVELOPER_VERSION:
						info = "You are using a developer version.";
						break;
					case OUTDATED_VERSION:
						info = "You are using an outdated version. Version &e" + newVersion
								+ " &7is now available (&bhttps://www.spigotmc.org/resources/64671&7)";
						break;
					case SAME_VERSION:
						info = "You are using an up-to-date version.";
						break;
				}
				MSG.log(info);
			});
		}
	}

	public BanHook getBanManager() {
		return banManager;
	}

	public TPSChecker getTPSChecker() {
		return tpsChecker;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public Stats getStats() {
		return stats;
	}

	public float getTPS() {
		return tpsChecker.getTPS();
	}

	public String getServerName() {
		if (!config.getString("BungeeNameOverride", "").isEmpty())
			return config.getString("BungeeNameOverride");
		return serverName;
	}

	public void setServerName(String name) {
		this.serverName = name;
	}

	public void onDisable() {
		stats.saveData();

		for (OfflinePlayer p : pManager.getLoadedPlayers())
			pManager.removePlayer(p); // Clear all loaded player data and save to files

	}

	public void saveData() {
		try {
			data.save(dataYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void saveConfig() {
		try {
			config.save(configYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setConfig(FileConfiguration config) {
		this.config = config;
	}

	public FileConfiguration getLang() {
		return lang;
	}

	public void setLang(FileConfiguration lang) {
		this.lang = lang;
	}

	public Checks getChecks() {
		return this.checks;
	}

	public PlayerManager getPlayerManager() {
		return pManager;
	}

	public boolean devMode() {
		return config.getBoolean("DevMode");
	}

	public boolean debugMode() {
		return config.getBoolean("DebugMode");
	}

	public CPlayer getCPlayer(OfflinePlayer off) {
		return pManager.getPlayer(off);
	}

	public Banwave getBanwave() {
		return this.banwave;
	}

	/**
	 * @return the compatabilities
	 */
	public Collection<AbstractHook> getCompatabilities() {
		return compatabilities;
	}
}
