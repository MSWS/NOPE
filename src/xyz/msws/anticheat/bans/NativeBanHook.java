package xyz.msws.anticheat.bans;

import java.util.Date;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class NativeBanHook implements BanHook {

	@Override
	public void ban(UUID player, String reason, long time) {
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		Bukkit.getBanList(Type.NAME).addBan(p.getName(), reason == null ? "Hacking" : reason,
				time == -1 ? null : new Date(System.currentTimeMillis() + time), "NOPE");
		if (p.isOnline())
			p.getPlayer().kickPlayer(reason);
	}

}
