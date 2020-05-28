package xyz.msws.anticheat.modules.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.events.DevModeToggleEvent;
import xyz.msws.anticheat.events.player.PlayerToggleScoreboardEvent;
import xyz.msws.anticheat.modules.AbstractModule;
import xyz.msws.anticheat.modules.data.CPlayer;

public class ScoreboardAssigner extends AbstractModule implements Listener {

	private ScoreboardModule sb;

	public ScoreboardAssigner(NOPE plugin) {
		super(plugin);
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

	@EventHandler
	public void onToggle(PlayerToggleScoreboardEvent event) {
		giveScoreboard(event.getPlayer());
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

	@Override
	public void enable() {
		if (!plugin.getConfig().getBoolean("Scoreboard"))
			return;
		sb = plugin.getModule(ScoreboardModule.class);

		for (Player player : Bukkit.getOnlinePlayers()) {
			giveScoreboard(player);
		}

		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@Override
	public void disable() {

	}
}
