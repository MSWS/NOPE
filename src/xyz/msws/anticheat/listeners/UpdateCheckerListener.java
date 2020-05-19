package xyz.msws.anticheat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.utils.MSG;
import xyz.msws.anticheat.utils.Utils.Age;

public class UpdateCheckerListener implements Listener {

	private NOPE plugin;

	public UpdateCheckerListener(NOPE plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("nope.message.update"))
			return;
		if (plugin.getPluginInfo().outdated() != Age.OUTDATED_VERSION)
			return;

		MSG.tell(player, "&4[NOPE] &7A new update for &cNOPE &7is now available! (&e"
				+ plugin.getDescription().getVersion() + " &7-> &a" + plugin.getNewVersion() + "&7)");

		BaseComponent message = new TextComponent(MSG.color("&7You can download the latest version "));
		TextComponent here = new TextComponent("here");
		here.setColor(ChatColor.AQUA);
		here.setItalic(true);
		here.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/64671/"));
		here.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Visit NOPE on Spigot").create()));
		message.addExtra(here);
		message.addExtra(MSG.color("&7."));

		player.spigot().sendMessage(message);

	}
}
