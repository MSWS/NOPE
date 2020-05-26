package xyz.msws.anticheat.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.events.DevModeToggleEvent;
import xyz.msws.anticheat.utils.MSG;

public class ScoreboardAssigner implements Listener {

	private NOPE plugin;
	private ScoreboardModule sb;

	public ScoreboardAssigner(NOPE plugin) {
		this.plugin = plugin;
		if (!plugin.getConfig().getBoolean("Scoreboard"))
			return;
		sb = plugin.getScoreboardModule();

		for (Player player : Bukkit.getOnlinePlayers()) {
			giveScoreboard(player);
		}

		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		giveScoreboard(player);
	}

	@EventHandler
	public void onDev(DevModeToggleEvent event) {
		for (Player p : Bukkit.getOnlinePlayers())
			giveScoreboard(p);
	}

	private void giveScoreboard(Player player) {
		sb.removeScoreboard(player);
		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.hasSaveData("scoreboard") || !cp.getSaveData("scoreboard", Boolean.class))
			return;

		if (plugin.devMode()) {
			if (!player.hasPermission("nope.scoreboard.dev"))
				return;
			sb.setScoreboard(player, new DevScoreboard(plugin, player));
		} else {
			if (!player.hasPermission("nope.scoreboard"))
				return;
			sb.setScoreboard(player, new VLScoreboard(plugin, player));
		}
	}
}
