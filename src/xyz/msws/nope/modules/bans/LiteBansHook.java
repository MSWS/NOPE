package xyz.msws.nope.modules.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

import xyz.msws.nope.NOPE;

public class LiteBansHook extends AbstractBanHook {

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
