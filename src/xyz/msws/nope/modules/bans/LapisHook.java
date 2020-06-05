package xyz.msws.nope.modules.bans;

import java.util.UUID;

import net.lapismc.lapisbans.api.LapisBansAPIStorage;
import net.lapismc.lapisbans.api.punishments.core.PunishmentType;
import xyz.msws.nope.NOPE;

public class LapisHook extends BanHook {

	public LapisHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		PunishmentType type = PunishmentType.Ban;
		LapisBansAPIStorage.getAPI().createPunishment(type, player, UUID.randomUUID(), reason, false, time != -1,
				System.currentTimeMillis(), System.currentTimeMillis() + time);
	}

}
