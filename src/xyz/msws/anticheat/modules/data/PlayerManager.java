package xyz.msws.anticheat.modules.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class PlayerManager extends AbstractModule {
	private NOPE plugin;

	private Map<OfflinePlayer, CPlayer> players = new HashMap<>();

	public PlayerManager(NOPE plugin) {
		super(plugin);
		this.plugin = plugin;

	}

	public CPlayer getPlayer(OfflinePlayer player) {
		if (!players.containsKey(player))
			players.put(player, new CPlayer(player, plugin));
		return players.get(player);
	}

	public List<OfflinePlayer> getLoadedPlayers() {
		return new ArrayList<OfflinePlayer>(players.keySet());
	}

	public void removePlayer(OfflinePlayer player) {
		if (players.containsKey(player))
			players.get(player).saveData();
		players.remove(player);
	}

	public void clearPlayers() {
		for (OfflinePlayer player : players.keySet())
			removePlayer(player);
	}

	@Override
	public void enable() {
	}

	@Override
	public void disable() {
		clearPlayers();
	}
}
