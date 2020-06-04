package xyz.msws.anticheat.modules.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class NPCModule extends AbstractModule implements Listener {

	private Map<UUID, NPC> npcs = new HashMap<>();

	public NPCModule(NOPE plugin) {
		super(plugin);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void enable() {

	}

	public Map<UUID, NPC> getNPCs() {
		return npcs;
	}

	public NPC getNPC(UUID player) {
		return npcs.get(player);
	}

	public boolean hasNPC(UUID player) {
		return npcs.containsKey(player);
	}

	public NPC getOrSpawn(Player player) {
		if (player == null || !player.isOnline())
			throw new IllegalArgumentException("Invalid player");

		if (hasNPC(player.getUniqueId()))
			return getNPC(player.getUniqueId());

		NPC npc = new NPC(player.getLocation());
		npc.spawn(player);
		npcs.put(player.getUniqueId(), npc);
		return npc;
	}

	public void removeNPC(Player player) {
		if (!npcs.containsKey(player.getUniqueId()))
			return;
		NPC npc = getNPC(player.getUniqueId());
		npc.remove();
		npcs.remove(player.getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!hasNPC(player.getUniqueId()))
			return;
		getNPC(player.getUniqueId()).remove();
		npcs.remove(player.getUniqueId());
	}

	@Override
	public void disable() {
		npcs.values().forEach(n -> n.remove());
		npcs.clear();
	}

}
