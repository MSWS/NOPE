package xyz.msws.nope.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.data.PlayerManager;

/**
 * Grabs the server name automatically from bungeecord.
 * 
 * @author imodm
 *
 */
public class LoginAndQuit extends AbstractModule implements Listener {

	public LoginAndQuit(NOPE plugin) {
		super(plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		plugin.getModule(PlayerManager.class).removePlayer(event.getPlayer().getUniqueId());
	}

	@Override
	public void enable() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {

	}
}
