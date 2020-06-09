package xyz.msws.nope.modules.trust;

import java.util.UUID;

/**
 * TrustRating represents a measurement of a specific aspect of a player,
 * whether that be gametime, mob kills, player kills, etc.
 * 
 * @author imodm
 *
 */
public interface TrustRating {
	/**
	 * Should return a number between 0-1.
	 */
	public double getTrust(UUID uuid);

	/**
	 * Should return a number between 0-1.
	 * 
	 * @return
	 */
	public float getWeight();
}
