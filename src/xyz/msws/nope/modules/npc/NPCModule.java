package xyz.msws.nope.modules.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;

/**
 * Responsible for managing and assigning NPCs, ideally you shouldn't be calling
 * {@link NPC#remove()}.
 * 
 * @author imodm
 *
 */
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

	/**
	 * Returns the NPC the player has been assigned to or null.
	 * 
	 * @param player The UUID to get
	 * @return The NPC, null if none.
	 */
	@Nullable
	public NPC getNPC(UUID player) {
		return npcs.get(player);
	}

	/**
	 * Returns whether or not the player has been assigned an NPC with getOrSpawn.
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasNPC(UUID player) {
		return npcs.containsKey(player);
	}

	/**
	 * This returns true ONLY if the NPC is an NPC <b>assigned</b>, players can be
	 * sent NPCs without being assigned them (like with PlayerESP)
	 * 
	 * @param entityid
	 * @return
	 */
	public boolean isRegisteredNPC(int entityid) {
		return npcs.values().stream().anyMatch(n -> n.getEntityID() == entityid);
	}

	/**
	 * Primary method for spawning a NPC for a player.
	 * 
	 * @param player
	 * @return
	 */
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

	/**
	 * Removes and destroys the NPC.
	 * 
	 * @param player
	 */
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
