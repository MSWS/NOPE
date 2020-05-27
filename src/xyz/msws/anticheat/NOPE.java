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

import xyz.msws.anticheat.commands.AntiCheatCommand;
import xyz.msws.anticheat.listeners.GUIListener;
import xyz.msws.anticheat.listeners.LogImplementation;
import xyz.msws.anticheat.listeners.LoginAndQuit;
import xyz.msws.anticheat.listeners.MessageListener;
import xyz.msws.anticheat.listeners.UpdateCheckerListener;
import xyz.msws.anticheat.modules.AbstractModule;
import xyz.msws.anticheat.modules.actions.ActionManager;
import xyz.msws.anticheat.modules.animations.AnimationManager;
import xyz.msws.anticheat.modules.bans.AdvancedBanHook;
import xyz.msws.anticheat.modules.bans.BanHook;
import xyz.msws.anticheat.modules.bans.BanManagementHook;
import xyz.msws.anticheat.modules.bans.LiteBansHook;
import xyz.msws.anticheat.modules.bans.MaxBansHook;
import xyz.msws.anticheat.modules.bans.NativeBanHook;
import xyz.msws.anticheat.modules.checks.Banwave;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.Checks;
import xyz.msws.anticheat.modules.checks.Global;
import xyz.msws.anticheat.modules.checks.TPSChecker;
import xyz.msws.anticheat.modules.compatability.AbstractHook;
import xyz.msws.anticheat.modules.compatability.CrazyEnchantsHook;
import xyz.msws.anticheat.modules.compatability.McMMOHook;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.modules.data.PlayerManager;
import xyz.msws.anticheat.modules.data.Stats;
import xyz.msws.anticheat.modules.scoreboard.ScoreboardAssigner;
import xyz.msws.anticheat.modules.scoreboard.ScoreboardModule;
import xyz.msws.anticheat.utils.MSG;
import xyz.msws.anticheat.utils.Metrics;
import xyz.msws.anticheat.utils.Metrics.CustomChart;

public class NOPE extends JavaPlugin {
	private FileConfiguration config, data, lang;
	private File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml");

	private String serverName = "Unknown Server";

	private PluginInfo pluginInfo;

	private String newVersion = null;

	private Collection<AbstractHook> compatabilities = new ArrayList<>();

	private HashSet<AbstractModule> modules = new HashSet<>();

	private PlayerManager pManager;

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
		modules.add(new ActionManager(this, configYml));
		modules.add(new PlayerManager(this));
		modules.add(new TPSChecker(this));
		modules.add(new Banwave(this));
		modules.add(new Checks(this));
		modules.add(new Stats(this));
		modules.add(new Global(this));
		modules.add(hookBans());
		modules.add(new ScoreboardModule(this));
		modules.add(new ScoreboardAssigner(this));
		modules.add(new AnimationManager(this));

		new AntiCheatCommand(this);
		new LogImplementation(this);
		new LoginAndQuit(this);
		new GUIListener(this);

		enableModules();

		uploadCustomCharts();
		runUpdateCheck();

		compatabilities = loadCompatabilities();
		for (AbstractHook comp : compatabilities)
			MSG.log("&7Registered compatability for " + MSG.FORMAT_INFO + comp.getName() + "&7.");

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));

		MSG.log("&aPlease report any bugs at the github. (https://github.com/MSWS/AntiCheat)");
	}

	private void enableModules() {
		for (AbstractModule mod : modules)
			mod.enable();
	}

	public <T extends AbstractModule> T getModule(Class<T> cast) {
		for (AbstractModule module : modules) {
			if (cast.isAssignableFrom(module.getClass()))
				return cast.cast(module);
		}
		return null;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public ScoreboardModule getScoreboardModule() {
		return getModule(ScoreboardModule.class);
	}

	/**
	 * @deprecated
	 * @return
	 */
	public ActionManager getActionManager() {
		return getModule(ActionManager.class);
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
			case "1.5":
				return "Your config version is slightly old however nothing has changed.";
			default:
				return "Your config version is unknown, it is strongly recommended you reset your config.";
		}
	}

	private BanHook hookBans() {
		if (Bukkit.getPluginManager().isPluginEnabled("AdvancedBan")) {
			MSG.log("Successfully hooked into AdvancedBans.");
			return new AdvancedBanHook(this);
		} else if (Bukkit.getPluginManager().isPluginEnabled("MaxBans")) {
			MSG.log("Successfully hooked into MaxBans.");
			return new MaxBansHook(this);
		} else if (Bukkit.getPluginManager().isPluginEnabled("BanManagement")) {
			MSG.log("Successfully hooked into BanManagement.");
			return new BanManagementHook(this);
		} else if (Bukkit.getPluginManager().isPluginEnabled("LiteBans")) {
			MSG.log("Successfully hooked into LiteBans.");
			return new LiteBansHook(this);
		} else {
			MSG.log("Unable to find a ban management plugin, using native support.");
			return new NativeBanHook(this);
		}
	}

	private void uploadCustomCharts() {
		Metrics metrics = new Metrics(this, 7422);
		CustomChart chart = new Metrics.SingleLineChart("bans", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getModule(Stats.class).getAllBans();
			}
		});
		metrics.addCustomChart(chart);

		chart = new Metrics.AdvancedBarChart("checkweights", new Callable<Map<String, int[]>>() {
			@Override
			public Map<String, int[]> call() throws Exception {
				Map<String, int[]> result = new HashMap<>();
				Checks checks = getModule(Checks.class);
				Stats stats = getModule(Stats.class);
				for (Check check : checks.getAllChecks()) {
					int[] arr = new int[2];
					arr[0] = stats.getTotalVl(check);
					arr[1] = stats.getTotalTriggers(check);
					result.put(check.getDebugName(), arr);
				}
				return result;
			}
		});
		metrics.addCustomChart(chart);
		chart = new Metrics.SimplePie("defaultactions", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return NOPE.this.getConfig().getString("Actions.Default", "Unset");
			}
		});
		metrics.addCustomChart(chart);
		chart = new Metrics.SimplePie("configversion", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return NOPE.this.getConfig().getString("ConfigVersion", "Unknown");
			}
		});
		metrics.addCustomChart(chart);
		chart = new Metrics.SimplePie("bungeeoverride", new Callable<String>() {
			@Override
			public String call() throws Exception {
				if (NOPE.this.getConfig().getString("BungeeNameOverride", "").equals(""))
					return "Unset";
				return NOPE.this.getConfig().getString("BungeeNameOverride", "Unset");
			}
		});
		metrics.addCustomChart(chart);
		chart = new Metrics.SimplePie("updatechecker", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return NOPE.this.getConfig().getString("UpdateChecker.Enabled", "Unset");
			}
		});

		metrics.addCustomChart(chart);
		chart = new Metrics.SimplePie("customactions", new Callable<String>() {
			@Override
			public String call() throws Exception {
				if (!NOPE.this.getConfig().isConfigurationSection("Commands"))
					return "None";
				return NOPE.this.getConfig().getConfigurationSection("Commands").getKeys(false).size() + "";
			}
		});

		metrics.addCustomChart(chart);
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

	/**
	 * @deprecated
	 * @return
	 */
	public BanHook getBanManager() {
		return getModule(BanHook.class);
	}

	/**
	 * @deprecated
	 * @return
	 */
	public TPSChecker getTPSChecker() {
		return getModule(TPSChecker.class);
	}

	public String getNewVersion() {
		return newVersion;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public Stats getStats() {
		return getModule(Stats.class);
	}

	public float getTPS() {
		return getTPSChecker().getTPS();
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
		getModule(Stats.class).saveData();

		PlayerManager pm = getModule(PlayerManager.class);

		for (OfflinePlayer p : pm.getLoadedPlayers())
			pm.removePlayer(p); // Clear all loaded player data and save to files

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
		return getModule(Checks.class);
	}

	/**
	 * @deprecated
	 * @return
	 */
	public PlayerManager getPlayerManager() {
		return getModule(PlayerManager.class);
	}

	public boolean devMode() {
		return config.getBoolean("DevMode");
	}

	public boolean debugMode() {
		return config.getBoolean("DebugMode");
	}

	public CPlayer getCPlayer(OfflinePlayer off) {
		if (pManager == null)
			pManager = getModule(PlayerManager.class);
		return pManager.getPlayer(off);
	}

	public Banwave getBanwave() {
		return getModule(Banwave.class);
	}

	/**
	 * @return the compatabilities
	 */
	public Collection<AbstractHook> getCompatabilities() {
		return compatabilities;
	}
}
