package xyz.msws.anticheat.modules.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

import xyz.msws.anticheat.NOPE;

public class LiteBansHook extends BanHook {

	// LiteBans has a questionable [b]api[/b]

	public LiteBansHook(NOPE plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + Bukkit.getOfflinePlayer(player).getName()
				+ " --sender=NOPE " + (reason == null ? "Hacking" : reason));
	}
}
