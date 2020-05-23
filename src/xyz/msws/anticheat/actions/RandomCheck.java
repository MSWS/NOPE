package xyz.msws.anticheat.actions;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;

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
