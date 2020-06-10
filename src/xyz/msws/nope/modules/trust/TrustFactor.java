package xyz.msws.nope.modules.trust;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

/**
 * The TrustFactor is an abstract representation of how <i>trustworth</i> a
 * player is. For now this is simply a little interesting feature that is
 * relatively unreliable. The inspiration behidn this is of course from Valve's
 * Trust Factor system.
 * 
 * @author imodm
 *
 */
public class TrustFactor extends AbstractModule {

	private Map<UUID, Double> ratings = new HashMap<>();
	private Set<TrustRating> factors = new HashSet<>();

	public TrustFactor(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		factors.add(new PlaytimeRating());
		factors.add(new ChatRating(plugin));
		factors.add(new VLRating(plugin));
		factors.add(new InteractionRating(plugin));
		factors.add(new HistoryRating(plugin));
		factors.add(new ReportRating(plugin));
	}

	/**
	 * Registers a new {@link TrustRating} for the TrustFactor to take into account.
	 * 
	 * @param rating
	 */
	public void addTrustRating(TrustRating rating) {
		factors.add(rating);
	}

	/**
	 * Returns a cached result of the player's report.
	 * 
	 * @param player
	 * @return
	 */
	public double getRating(UUID player) {
		return ratings.getOrDefault(player, recalculate(player));
	}

	/**
	 * Recalculates the weighted trust factor of a player.
	 * 
	 * @param player
	 * @return
	 */
	public double recalculate(UUID player) {
		double result = 0;
		double weights = 0;
		for (TrustRating rating : factors) {
			double trust = rating.getTrust(player);
			if (trust < 0 || trust > 1)
				MSG.warn(rating.getClass().getName() + " returned " + trust);
			result += rating.getTrust(player) * rating.getWeight();
			weights += rating.getWeight();
		}
		result /= weights;
		ratings.put(player, result);
		return result;
	}

	public String format(UUID player) {
		StringBuilder builder = new StringBuilder();
		for (TrustRating rating : factors) {
			double trust = rating.getTrust(player);
			builder.append(rating.getClass().getName()).append(": ").append(trust).append(" (")
					.append(trust * rating.getWeight()).append(")\n");
		}
		return builder.toString();
	}

	@Override
	public void disable() {
		ratings.clear();
	}

}
