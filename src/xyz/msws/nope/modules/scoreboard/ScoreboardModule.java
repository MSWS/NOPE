package xyz.msws.nope.modules.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

/**
 * This is the main scoreboard utility that handles scoreboards across the
 * servers
 */
public class ScoreboardModule extends AbstractModule {

	private ScoreboardManager sman;

	private Map<UUID, CScoreboard> assigned = new HashMap<>();

	private BukkitRunnable runner;

	public ScoreboardModule(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		this.sman = Bukkit.getScoreboardManager();

		assigned = new HashMap<UUID, CScoreboard>();

		runner = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!assigned.containsKey(p.getUniqueId()))
						continue;

					assigned.get(p.getUniqueId()).onTick();
					for (int i = 1; i <= 15; i++)
						setLine(p, i, assigned.get(p.getUniqueId()).getLine(i));
					setTitle(p, assigned.get(p.getUniqueId()).getTitle());
				}
			}
		};
		runner.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public void disable() {
		for (Player p : Bukkit.getOnlinePlayers())
			removeScoreboard(p);
		assigned = new HashMap<>();

		if (runner != null && !runner.isCancelled())
			runner.cancel();
	}

	private void setTitle(Player player, String value) {
		Scoreboard board = player.getScoreboard();
		Objective obj;
		if (board == null || board.getObjective("nope") == null)
			return;
		obj = board.getObjective("nope");
		if (obj == null)
			return;
		obj.setDisplayName(MSG.color(value));
	}

	private void setLine(Player player, int line, String value) {
		Scoreboard board = player.getScoreboard();
		Objective obj;
		if (board == null) {
			board = player.getScoreboard() == null ? sman.getMainScoreboard() : player.getScoreboard();
			player.setScoreboard(board);
		}

		obj = board.getObjective("nope");

		if (obj == null) {
			obj = board.registerNewObjective("nope", "dummy", "nope");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		ChatColor[] vals = ChatColor.values();

		if (value == null) {
			if (!board.getScores(vals[line] + "" + ChatColor.RESET).isEmpty())
				board.resetScores(vals[line] + "" + ChatColor.RESET);
			return;
		}

		Validate.isTrue(value.length() <= 124, "Value cannot exceed length of 124", value);

		String prefix = ChatColor.translateAlternateColorCodes('&', value.substring(0, Math.min(62, value.length())));
		String suffix = ChatColor.translateAlternateColorCodes('&',
				value.substring(Math.min(value.length(), 62), Math.max(value.length(), Math.min(value.length(), 62))));

		Team team = board.getTeam(vals[line] + "" + ChatColor.RESET);
		if (team == null)
			team = board.registerNewTeam(vals[line] + "" + ChatColor.RESET);

		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.addEntry(vals[line] + "" + ChatColor.RESET);
		obj.getScore(vals[line] + "" + ChatColor.RESET).setScore(line);
	}

	public void setScoreboard(Player p, CScoreboard board) {
		assigned.put(p.getUniqueId(), board);

		Scoreboard b = p.getScoreboard();
		Objective obj;
		if (board == null) {
			b = p.getScoreboard() == null ? sman.getMainScoreboard() : p.getScoreboard();
			p.setScoreboard(b);
		}

		obj = b.getObjective("nope");

		if (obj == null) {
			obj = b.registerNewObjective("nope", "dummy", "nope");
		}

		if (obj.getDisplaySlot() != DisplaySlot.SIDEBAR)
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void removeScoreboard(Player p) {
		assigned.remove(p.getUniqueId());

		Scoreboard board = p.getScoreboard();
		if (board == null || board.getObjective("nope") == null)
			return;
		board.clearSlot(DisplaySlot.SIDEBAR);

		p.setScoreboard(sman.getMainScoreboard());
	}

}