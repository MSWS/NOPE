package xyz.msws.nope.modules.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import xyz.msws.nope.NOPE;

public class AdvancedBanHook extends AbstractBanHook {

	public AdvancedBanHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		if (reason == null)
			reason = "Hacking";
		String name = Bukkit.getOfflinePlayer(player).getName();
		PunishmentType type = (time == -1) ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Punishment.create(name, player.toString(), reason, "NOPE", type, System.currentTimeMillis() + time, null, true);
	}

}
