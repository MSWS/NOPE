package xyz.msws.anticheat.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanHook implements BanHook {

	@Override
	public void ban(UUID player, String reason, long time) {
		if (reason == null)
			reason = "Hacking";
		String name = Bukkit.getOfflinePlayer(player).getName();
		PunishmentType type = (time == -1) ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Punishment.create(name, player.toString(), reason, "NOPE", type, System.currentTimeMillis() + time, null, true);
	}

}
