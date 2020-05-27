package xyz.msws.anticheat.modules.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;

public abstract class CScoreboard {
	protected List<String> lines = new ArrayList<String>();
	protected Player player;

	protected String title;
	protected NOPE plugin;

	public CScoreboard(NOPE plugin, Player player) {
		this.player = player;
		this.plugin = plugin;

		for (int i = 0; i <= 15; i++)
			lines.add(i, "");
	}

	public abstract void onTick();

	public List<String> getLines() {
		return lines;
	}

	public String getLine(int line) {
		return (line < 0 || line > 15 || line >= lines.size()) ? null : lines.get(line);
	}

	public void setLine(int line, String value) {
		lines.set(line, value);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
