package xyz.msws.nope.modules.bans;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.confuser.banmanager.BmAPI;
import me.confuser.banmanager.data.PlayerBanData;
import me.confuser.banmanager.data.PlayerBanRecord;
import me.confuser.banmanager.data.PlayerData;
import xyz.msws.nope.NOPE;

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

	@Override
	public int bans(UUID player) {
		PlayerData data = new PlayerData(player, Bukkit.getOfflinePlayer(player).getName());
		int amo = 0;
		try {
			Iterator<PlayerBanRecord> it = BmAPI.getBanRecords(data);
			while (BmAPI.getBanRecords(data).hasNext()) {
				amo++;
				it.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return amo;
	}

}
