package xyz.msws.anticheat.bans;

import java.util.UUID;

import net.lapismc.lapisbans.api.LapisBansAPIStorage;
import net.lapismc.lapisbans.api.punishments.core.PunishmentType;

public class LapisHook implements BanHook {

	@Override
	public void ban(UUID player, String reason, long time) {
		PunishmentType type = PunishmentType.Ban;
		LapisBansAPIStorage.getAPI().createPunishment(type, player, UUID.randomUUID(), reason, false, time != -1,
				System.currentTimeMillis(), System.currentTimeMillis() + time);
	}

}
