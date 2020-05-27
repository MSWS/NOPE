package xyz.msws.anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Checks if a player sends a chat message while moving
 * 
 * @author imodm
 *
 */
public class AutoWalk1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, Long> lastMove = new HashMap<>();
	private Map<UUID, Long> lastChat = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle())
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() < from.getY())
			return;

		lastMove.put(player.getUniqueId(), System.currentTimeMillis());

		if (System.currentTimeMillis() - lastChat.getOrDefault(player.getUniqueId(), 0L) > 50)
			return;
		cp.flagHack(this, 20);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (System.currentTimeMillis() - lastMove.getOrDefault(player.getUniqueId(), System.currentTimeMillis()) < 50)
			lastChat.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@Override
	public String getCategory() {
		return "AutoWalk";
	}

	@Override
	public String getDebugName() {
		return "AutoWalk#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
