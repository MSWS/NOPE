package org.mswsplex.anticheat.msws;

import java.io.File;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.anticheat.checks.Checks;
import org.mswsplex.anticheat.checks.Global;
import org.mswsplex.anticheat.checks.TPSChecker;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.data.PlayerManager;
import org.mswsplex.anticheat.utils.MSG;

public class AntiCheat extends JavaPlugin {
	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	private PlayerManager pManager;
	private TPSChecker tpsChecker;

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

		new Checks(this).registerChecks();

		new Global(this);

		MSG.log("&aSuccessfully Enabled!");
	}

	public TPSChecker getTPSChecker() {
		return tpsChecker;
	}

	public float getTPS() {
		return tpsChecker.getTPS();
	}

	public void onDisable() {
		for (OfflinePlayer p : pManager.getLoadedPlayers())
			pManager.removePlayer(p);
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

	public PlayerManager getPlayerManager() {
		return pManager;
	}

	public CPlayer getCPlayer(Player player) {
		return pManager.getPlayer(player);
	}
}
