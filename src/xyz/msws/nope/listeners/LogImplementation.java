package xyz.msws.nope.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Provides context to logs
 * 
 * @author imodm TODO
 *
 */
public class LogImplementation extends AbstractModule implements Listener {

	public LogImplementation(NOPE plugin) {
		super(plugin);
	}

	@EventHandler
	public void onCommandPreProcess(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.addLogMessage(
				player.getName() + " sent chat message: " + event.getMessage() + " time:" + System.currentTimeMillis());
	}

	@EventHandler
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.addLogMessage(
				player.getName() + " ran command: " + event.getMessage() + " time:" + System.currentTimeMillis());

		for (String word : event.getMessage().split(" ")) {
			List<Player> players = Bukkit.matchPlayer(word);
			for (Player target : players) {
				if (target.equals(player))
					continue;
				CPlayer ct = plugin.getCPlayer(target);
				ct.addLogMessage(player.getName() + " ran command: " + event.getMessage() + " time:"
						+ System.currentTimeMillis());
			}
		}
	}

	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.addLogMessage("");
		cp.addLogMessage("World change from " + event.getFrom().getName() + " to " + player.getWorld().getName()
				+ " time:" + System.currentTimeMillis());
		cp.addLogMessage("");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.addLogMessage("");
		cp.addLogMessage("Joined server time:" + System.currentTimeMillis());
		cp.addLogMessage("");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.addLogMessage("");
		cp.addLogMessage("Quit server time:" + System.currentTimeMillis());
		cp.addLogMessage("");
	}

	@Override
	public void enable() {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@Override
	public void disable() {
	}

}
