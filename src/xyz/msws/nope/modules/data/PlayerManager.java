package xyz.msws.nope.modules.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;

public class PlayerManager extends AbstractModule {
	private Map<UUID, CPlayer> players = new HashMap<>();

	public PlayerManager(NOPE plugin) {
		super(plugin);
	}

	public CPlayer getPlayer(OfflinePlayer player) {
		return getPlayer(player.getUniqueId());
	}

	public CPlayer getPlayer(UUID player) {
		players.putIfAbsent(player, new CPlayer(player, plugin));
		return players.get(player);
	}

	public List<UUID> getLoadedPlayers() {
		return new ArrayList<UUID>(players.keySet());
	}

	public void removePlayer(UUID player) {
		if (players.containsKey(player))
			players.get(player).saveData();
		players.remove(player);
	}

	public void clearPlayers() {
		if (players == null)
			return;
		for (Entry<UUID, CPlayer> entry : players.entrySet()) {
			entry.getValue().saveData();
		}
		players.clear();
	}

	@Override
	public void enable() {
	}

	@Override
	public void disable() {
		clearPlayers();
	}
}
