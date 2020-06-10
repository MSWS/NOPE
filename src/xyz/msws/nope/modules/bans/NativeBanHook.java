package xyz.msws.nope.modules.bans;

import java.util.Date;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.utils.MSG;

public class NativeBanHook extends AbstractBanHook {

	public NativeBanHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		Bukkit.getBanList(Type.NAME).addBan(p.getName(), MSG.color(reason == null ? "Hacking" : reason),
				time == -1 ? null : new Date(System.currentTimeMillis() + time), "NOPE");
		if (p.isOnline())
			p.getPlayer().kickPlayer(MSG.color(reason));
	}

	@Override
	public int bans(UUID player) {
		return (int) Bukkit.getBanList(Type.NAME).getBanEntries().parallelStream()
				.filter(b -> b.getTarget().equals(player.toString())).count();
	}

}
