package xyz.msws.nope.modules.trust;

import java.util.UUID;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.bans.AbstractBanHook;

public class HistoryRating implements TrustRating {

	private NOPE plugin;

	public HistoryRating(NOPE plugin) {
		this.plugin = plugin;
	}

	@Override
	public double getTrust(UUID uuid) {
		return 1 - Math.min(plugin.getModule(AbstractBanHook.class).bans(uuid) / 5, 1);
	}

	@Override
	public float getWeight() {
		return .1f;
	}

}
