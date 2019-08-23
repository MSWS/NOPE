package org.mswsplex.anticheat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mswsplex.anticheat.msws.NOPE;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class LoginAndQuit implements Listener {

	private NOPE plugin;

	public LoginAndQuit(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("NOPE");
		out.writeUTF("GetServer");
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (plugin.getAnimation().isInAnimation(event.getPlayer())) {
			plugin.getAnimation().stopAnimation(plugin.getAnimation().getKey(event.getPlayer()));
		}
		plugin.getPlayerManager().removePlayer(event.getPlayer());
	}
}
