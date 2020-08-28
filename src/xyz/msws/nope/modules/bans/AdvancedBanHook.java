package xyz.msws.nope.modules.bans;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import xyz.msws.nope.NOPE;

public class AdvancedBanHook extends BanHook {

	public AdvancedBanHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		if (reason == null)
			reason = "Hacking";
		final String r = reason;
		String name = Bukkit.getOfflinePlayer(player).getName();
		PunishmentType type = (time == -1) ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			Punishment.create(name, player.toString(), r, "NOPE", type, System.currentTimeMillis() + time, null, true);
		});
	}

	@Override
	public int bans(UUID player) {
		return PunishmentManager.get().getPunishments(player.toString(), PunishmentType.BAN, false).size();
	}

}
