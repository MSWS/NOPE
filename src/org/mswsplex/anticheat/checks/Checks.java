package org.mswsplex.anticheat.checks;

import java.util.ArrayList;
import java.util.List;

import org.mswsplex.anticheat.checks.client.NoGround;
import org.mswsplex.anticheat.checks.movement.GeneralMovement1;
import org.mswsplex.anticheat.checks.movement.Teleport1;
import org.mswsplex.anticheat.checks.movement.Timer1;
import org.mswsplex.anticheat.checks.movement.Flight1;
import org.mswsplex.anticheat.checks.movement.Speed1;
import org.mswsplex.anticheat.checks.movement.GeneralMovement2;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Checks {
	private AntiCheat plugin;
	private List<Check> activeChecks;

	public Checks(AntiCheat plugin) {
		this.plugin = plugin;
		activeChecks = new ArrayList<Check>();
	}

	public void registerChecks() {
		Check[] checks = { new GeneralMovement1(), new Flight1(), new NoGround(), new Speed1(), new GeneralMovement2(),
				new Teleport1(), new Timer1() };

		for (Check check : checks) {
			activeChecks.add(check);
			check.register(plugin);
		}
	}

	public List<Check> getActiveChecks() {
		return activeChecks;
	}

	public void registerCheck(Check check) {
		if (activeChecks.contains(check))
			throw new IllegalArgumentException("Check already registered");
		check.register(plugin);
		activeChecks.add(check);
	}
}
