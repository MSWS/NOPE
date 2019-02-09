package org.mswsplex.anticheat.checks;

import java.util.ArrayList;
import java.util.List;

import org.mswsplex.anticheat.checks.client.FastBow1;
import org.mswsplex.anticheat.checks.client.GhostHand1;
import org.mswsplex.anticheat.checks.client.InventoryMove1;
import org.mswsplex.anticheat.checks.client.NoFall1;
import org.mswsplex.anticheat.checks.client.NoGround1;
import org.mswsplex.anticheat.checks.combat.AutoClicker1;
import org.mswsplex.anticheat.checks.combat.Criticals1;
import org.mswsplex.anticheat.checks.combat.HighCPS1;
import org.mswsplex.anticheat.checks.combat.HighCPS2;
import org.mswsplex.anticheat.checks.combat.HighCPS3;
import org.mswsplex.anticheat.checks.movement.AntiAFK1;
import org.mswsplex.anticheat.checks.movement.AutoWalk1;
import org.mswsplex.anticheat.checks.movement.ClonedMovement1;
import org.mswsplex.anticheat.checks.movement.FastClimb1;
import org.mswsplex.anticheat.checks.movement.FastSneak1;
import org.mswsplex.anticheat.checks.movement.Flight1;
import org.mswsplex.anticheat.checks.movement.Flight2;
import org.mswsplex.anticheat.checks.movement.Flight3;
import org.mswsplex.anticheat.checks.movement.Jesus1;
import org.mswsplex.anticheat.checks.movement.Movement1;
import org.mswsplex.anticheat.checks.movement.NoWeb1;
import org.mswsplex.anticheat.checks.movement.Speed1;
import org.mswsplex.anticheat.checks.movement.Speed2;
import org.mswsplex.anticheat.checks.movement.Speed3;
import org.mswsplex.anticheat.checks.movement.Step1;
import org.mswsplex.anticheat.checks.render.AutoSneak1;
import org.mswsplex.anticheat.checks.render.InvalidMovement1;
import org.mswsplex.anticheat.checks.render.Spinbot1;
import org.mswsplex.anticheat.checks.tick.Timer1;
import org.mswsplex.anticheat.checks.world.IllegalBlockBreak1;
import org.mswsplex.anticheat.checks.world.IllegalBlockPlace1;
import org.mswsplex.anticheat.checks.world.Scaffold1;
import org.mswsplex.anticheat.checks.world.Scaffold2;
import org.mswsplex.anticheat.checks.world.Scaffold3;
import org.mswsplex.anticheat.checks.world.Scaffold4;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Checks {
	private AntiCheat plugin;
	private List<Check> activeChecks;

	public Checks(AntiCheat plugin) {
		this.plugin = plugin;
		activeChecks = new ArrayList<Check>();
	}

	public void registerChecks() {
		Check[] checks = { new Flight1(), new Flight2(), new Flight3(), new NoGround1(), new Speed1(), new Speed2(),
				new Speed3(), new Movement1(), new ClonedMovement1(), new Timer1(), new Step1(), new Criticals1(),
				new NoFall1(), new Scaffold1(), new Scaffold2(), new Scaffold3(), new Scaffold4(), new FastClimb1(),
				new Jesus1(), new FastBow1(), new FastSneak1(), new InvalidMovement1(), new Spinbot1(),
				new IllegalBlockBreak1(), new IllegalBlockPlace1(), new GhostHand1(), new NoWeb1(), new AutoWalk1(),
				new AutoClicker1(), new HighCPS1(), new HighCPS2(), new HighCPS3(), new AntiAFK1(), new AutoSneak1(),
				new InventoryMove1() };

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
