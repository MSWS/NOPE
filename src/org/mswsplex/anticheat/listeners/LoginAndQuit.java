package org.mswsplex.anticheat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mswsplex.anticheat.msws.AntiCheat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class LoginAndQuit implements Listener {

	private AntiCheat plugin;

	public LoginAndQuit(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("NOPE");
		out.writeUTF("GetServer");
		event.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		plugin.getCPlayer(event.getPlayer()).saveData();
	}
}
