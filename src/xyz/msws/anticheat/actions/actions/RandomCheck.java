package xyz.msws.anticheat.actions.actions;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.AbstractConditionalAction;
import xyz.msws.anticheat.checks.Check;

/**
 * {@link AbstractConditionalAction} Used for random actions. Not entirely sure
 * why you'd want to use this but I imagine it's useful one way or another.
 * 
 * @author imodm
 *
 */
public class RandomCheck extends AbstractConditionalAction {

	private double percent;

	public RandomCheck(NOPE plugin, double perc) {
		super(plugin);
		this.percent = perc;
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		return ThreadLocalRandom.current().nextDouble() > percent;
	}

}
