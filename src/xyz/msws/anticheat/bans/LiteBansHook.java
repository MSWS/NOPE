package xyz.msws.anticheat.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

public class LiteBansHook implements BanHook {

	// LiteBans has a questionable [b]api[/b]

	@Override
	public void ban(UUID player, String reason, long time) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + Bukkit.getOfflinePlayer(player).getName()
				+ " --sender=NOPE " + (reason == null ? "Hacking" : reason));
	}
}
