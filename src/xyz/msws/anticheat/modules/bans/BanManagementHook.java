package xyz.msws.anticheat.modules.bans;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.confuser.banmanager.BmAPI;
import me.confuser.banmanager.data.PlayerBanData;
import me.confuser.banmanager.data.PlayerData;
import xyz.msws.anticheat.NOPE;

public class BanManagementHook extends BanHook {

	public BanManagementHook(NOPE plugin) {
		super(plugin);
	}

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
