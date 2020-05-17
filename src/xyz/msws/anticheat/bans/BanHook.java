package xyz.msws.anticheat.bans;

import java.util.UUID;

public interface BanHook {
	public default void ban(UUID player) {
		ban(player, null);
	}

	public default void ban(UUID player, String reason) {
		ban(player, reason, -1);
	}

	public void ban(UUID player, String reason, long time);

}
