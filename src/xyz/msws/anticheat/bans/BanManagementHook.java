package xyz.msws.anticheat.bans;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.confuser.banmanager.BmAPI;
import me.confuser.banmanager.data.PlayerBanData;
import me.confuser.banmanager.data.PlayerData;

public class BanManagementHook implements BanHook {
	
	@Override
	public void ban(UUID player, String reason, long time) {
		PlayerBanData banData = new PlayerBanData(new PlayerData(player, Bukkit.getOfflinePlayer(player).getName()),
				new PlayerData(UUID.randomUUID(), "NOPE"), reason);
		try {
			BmAPI.ban(banData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
