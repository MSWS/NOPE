package xyz.msws.anticheat.checks.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.npc.NPC;

public class PlayerESP1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private Map<UUID, List<NPC>> npcs = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		for (Player p : Bukkit.getOnlinePlayers()) {
			spawnNPCs(p);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Iterator<Entry<UUID, List<NPC>>> it = npcs.entrySet().iterator();
				while (it.hasNext()) {
					Entry<UUID, List<NPC>> entry = it.next();
					if (!Bukkit.getOfflinePlayer(entry.getKey()).isOnline()) {
						entry.getValue().forEach(n -> n.remove());
						it.remove();
						continue;
					}

					Player player = Bukkit.getPlayer(entry.getKey());

					ThreadLocalRandom rnd = ThreadLocalRandom.current();
					for (NPC n : entry.getValue()) {
						Location l = player.getLocation();
						l = l.add(rnd.nextDouble(-100, 100), rnd.nextDouble(-10, 20), rnd.nextDouble(-100, 100));

						n.moveOrTeleport(l);
					}
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0, 20);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {
		for (Player p : Bukkit.getOnlinePlayers())
			clearNPCs(p);
		npcs.clear();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		spawnNPCs(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		npcs.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamemode(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getNewGameMode() != GameMode.SPECTATOR) {
			spawnNPCs(player);
			return;
		}
		clearNPCs(player);
	}

	private void clearNPCs(Player player) {
		npcs.getOrDefault(player.getUniqueId(), new ArrayList<>()).forEach(npc -> npc.remove());
		npcs.remove(player.getUniqueId());
	}

	private void spawnNPCs(Player player) {
		if (player.getGameMode() == GameMode.SPECTATOR)
			return;
		List<NPC> ns = npcs.getOrDefault(player.getUniqueId(), new ArrayList<>());
		for (NPC npc : ns)
			npc.remove();
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		for (int i = 0; i < 100; i++) {
			Location l = player.getLocation();
			l = l.add(rnd.nextDouble(-100, 100), rnd.nextDouble(-10, 20), rnd.nextDouble(-100, 100));
			NPC n = new NPC(l);
			n.spawn(player);
			n.setVisible(false);
			ns.add(n);
		}
		npcs.put(player.getUniqueId(), ns);
	}

	@Override
	public String getCategory() {
		return "PlayerESP";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
