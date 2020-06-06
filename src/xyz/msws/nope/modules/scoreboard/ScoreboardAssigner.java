package xyz.msws.nope.modules.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.global.OptionChangeEvent;
import xyz.msws.nope.events.player.PlayerOptionChangeEvent;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Responsible for assigning scoreboards to players, listents to
 * {@link OptionChangeEvent}, {@link PlayerOptionChangeEvent}, and
 * {@link PlayerJoinEvent}
 * 
 * @author imodm
 *
 */
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
	public void onDev(OptionChangeEvent event) {
		if (!(event.getOption().getKey().equals("Scoreboard") || event.getOption().getKey().equals("DevMode")))
			return;
		for (Player p : Bukkit.getOnlinePlayers())
			giveScoreboard(p);
	}

	@EventHandler
	public void onToggle(PlayerOptionChangeEvent event) {
		if (!event.getOption().getKey().equals("scoreboard"))
			return;
		giveScoreboard(event.getPlayer());
	}

	private void giveScoreboard(Player player) {
		sb.removeScoreboard(player);
		CPlayer cp = plugin.getCPlayer(player);

		if (!cp.getOption("scoreboard").asBoolean() || !plugin.getOption("gscoreboard").asBoolean()) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			return;
		}

		if (plugin.getOption("dev").asBoolean()) {
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
