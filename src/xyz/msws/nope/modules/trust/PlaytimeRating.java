package xyz.msws.nope.modules.trust;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public class PlaytimeRating implements TrustRating {

	@Override
	public double getTrust(UUID uuid) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if (!player.hasPlayedBefore())
			return 0;
		int seconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
		double hours = TimeUnit.SECONDS.toHours(seconds);
		return Math.min(hours / 5, 1);
	}

	@Override
	public float getWeight() {
		return .2f;
	}

}
