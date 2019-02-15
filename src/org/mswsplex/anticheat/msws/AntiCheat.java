package org.mswsplex.anticheat.msws;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.anticheat.checks.Banwave;
import org.mswsplex.anticheat.checks.Checks;
import org.mswsplex.anticheat.checks.Global;
import org.mswsplex.anticheat.checks.TPSChecker;
import org.mswsplex.anticheat.commands.AntiCheatCommand;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.data.PlayerManager;
import org.mswsplex.anticheat.data.Stats;
import org.mswsplex.anticheat.listeners.GUIListener;
import org.mswsplex.anticheat.listeners.LogImplementation;
import org.mswsplex.anticheat.listeners.LoginAndQuit;
import org.mswsplex.anticheat.listeners.MessageListener;
import org.mswsplex.anticheat.protocols.PacketListener;
import org.mswsplex.anticheat.scoreboard.SBoard;
import org.mswsplex.anticheat.utils.MSG;

public class AntiCheat extends JavaPlugin {
	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	private PlayerManager pManager;
	private TPSChecker tpsChecker;
	private Checks checks;
	private Banwave banwave;
	private Stats stats;

	public String serverName = "Unknown Server";

	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);

		MSG.plugin = this;
		pManager = new PlayerManager(this);
		tpsChecker = new TPSChecker(this);

		banwave = new Banwave(this);

		checks = new Checks(this);
		checks.registerChecks();

		serverName = Bukkit.getServerName();
		stats = new Stats(this);

		new Global(this);
		new AntiCheatCommand(this);

		new LogImplementation(this);
		new LoginAndQuit(this);
		new GUIListener(this);

		new PacketListener(this);

		new SBoard(this);

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));

		MSG.log("&aSuccessfully enabled. &7Please note that NOPE is &cstill in beta&7. Please report bugs at the github. (https://github.com/MSWS/AntiCheat)");
	}

	public TPSChecker getTPSChecker() {
		return tpsChecker;
	}

	public Stats getStats() {
		return stats;
	}

	public float getTPS() {
		return tpsChecker.getTPS();
	}

	public void onDisable() {
		stats.saveData();
		for (OfflinePlayer p : pManager.getLoadedPlayers())
			pManager.removePlayer(p);

		for (Player p : Bukkit.getOnlinePlayers())
			p.removeMetadata("lastEntityHit", this);

		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntitiesByClass(ArmorStand.class)) {
				if (ent.hasMetadata("killAuraMark") || ent.hasMetadata("antiKillAuraMark")
						|| ent.hasMetadata("lowerKillAuraMark"))
					ent.remove();
			}
		}
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

	public Checks getChecks() {
		return this.checks;
	}

	public PlayerManager getPlayerManager() {
		return pManager;
	}

	public boolean devMode() {
		return config.getBoolean("DevMode");
	}

	public CPlayer getCPlayer(OfflinePlayer off) {
		return pManager.getPlayer(off);
	}

	public Banwave getBanwave() {
		return this.banwave;
	}
}
