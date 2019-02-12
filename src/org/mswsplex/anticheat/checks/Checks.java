package org.mswsplex.anticheat.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mswsplex.anticheat.checks.combat.AntiKB1;
import org.mswsplex.anticheat.checks.combat.AutoArmor1;
import org.mswsplex.anticheat.checks.combat.AutoClicker1;
import org.mswsplex.anticheat.checks.combat.Criticals1;
import org.mswsplex.anticheat.checks.combat.FastBow1;
import org.mswsplex.anticheat.checks.combat.HighCPS1;
import org.mswsplex.anticheat.checks.combat.HighCPS2;
import org.mswsplex.anticheat.checks.combat.HighCPS3;
import org.mswsplex.anticheat.checks.combat.KillAura1;
import org.mswsplex.anticheat.checks.combat.KillAura2;
import org.mswsplex.anticheat.checks.combat.KillAura3;
import org.mswsplex.anticheat.checks.combat.KillAura4;
import org.mswsplex.anticheat.checks.combat.KillAura5;
import org.mswsplex.anticheat.checks.combat.Reach1;
import org.mswsplex.anticheat.checks.exploit.ServerCrasher1;
import org.mswsplex.anticheat.checks.exploit.ServerCrasher2;
import org.mswsplex.anticheat.checks.movement.AntiAFK1;
import org.mswsplex.anticheat.checks.movement.AntiRotate1;
import org.mswsplex.anticheat.checks.movement.AutoWalk1;
import org.mswsplex.anticheat.checks.movement.ClonedMovement1;
import org.mswsplex.anticheat.checks.movement.FastClimb1;
import org.mswsplex.anticheat.checks.movement.FastSneak1;
import org.mswsplex.anticheat.checks.movement.Flight1;
import org.mswsplex.anticheat.checks.movement.Flight2;
import org.mswsplex.anticheat.checks.movement.Flight3;
import org.mswsplex.anticheat.checks.movement.Flight4;
import org.mswsplex.anticheat.checks.movement.InventoryMove1;
import org.mswsplex.anticheat.checks.movement.Jesus1;
import org.mswsplex.anticheat.checks.movement.Movement1;
import org.mswsplex.anticheat.checks.movement.NoSlowDown1;
import org.mswsplex.anticheat.checks.movement.NoWeb1;
import org.mswsplex.anticheat.checks.movement.Speed1;
import org.mswsplex.anticheat.checks.movement.Speed2;
import org.mswsplex.anticheat.checks.movement.Speed3;
import org.mswsplex.anticheat.checks.movement.Step1;
import org.mswsplex.anticheat.checks.player.AntiFire1;
import org.mswsplex.anticheat.checks.player.ChestStealer1;
import org.mswsplex.anticheat.checks.player.FastEat1;
import org.mswsplex.anticheat.checks.player.GhostHand1;
import org.mswsplex.anticheat.checks.player.NoFall1;
import org.mswsplex.anticheat.checks.player.NoGround1;
import org.mswsplex.anticheat.checks.player.SelfHarm1;
import org.mswsplex.anticheat.checks.player.Zoot1;
import org.mswsplex.anticheat.checks.render.AutoSneak1;
import org.mswsplex.anticheat.checks.render.InvalidMovement1;
import org.mswsplex.anticheat.checks.render.NoSwing1;
import org.mswsplex.anticheat.checks.render.SkinBlinker1;
import org.mswsplex.anticheat.checks.render.Spinbot1;
import org.mswsplex.anticheat.checks.tick.MultiUse1;
import org.mswsplex.anticheat.checks.tick.Regen1;
import org.mswsplex.anticheat.checks.tick.Regen2;
import org.mswsplex.anticheat.checks.tick.Timer1;
import org.mswsplex.anticheat.checks.tick.Timer2;
import org.mswsplex.anticheat.checks.world.IllegalBlockBreak1;
import org.mswsplex.anticheat.checks.world.IllegalBlockPlace1;
import org.mswsplex.anticheat.checks.world.Scaffold1;
import org.mswsplex.anticheat.checks.world.Scaffold2;
import org.mswsplex.anticheat.checks.world.Scaffold3;
import org.mswsplex.anticheat.msws.AntiCheat;

@SuppressWarnings("deprecation")
public class Checks {
	private AntiCheat plugin;
	private List<Check> activeChecks;

	public Checks(AntiCheat plugin) {
		this.plugin = plugin;
		activeChecks = new ArrayList<Check>();
	}

	public void registerChecks() {
		Check[] checks = { new Flight1(), new Flight2(), new Flight3(), new Flight4(), new NoGround1(), new Speed1(),
				new Speed2(), new Speed3(), new Movement1(), new ClonedMovement1(), new Timer1(), new Timer2(),
				new Step1(), new Criticals1(), new NoFall1(), new Scaffold1(), new Scaffold2(), new Scaffold3(),
				new FastClimb1(), new Jesus1(), new FastBow1(), new FastSneak1(), new InvalidMovement1(),
				new Spinbot1(), new IllegalBlockBreak1(), new IllegalBlockPlace1(), new GhostHand1(), new NoWeb1(),
				new AutoWalk1(), new AutoClicker1(), new HighCPS1(), new HighCPS2(), new HighCPS3(), new AntiAFK1(),
				new AutoSneak1(), new InventoryMove1(), new Reach1(), new KillAura1(), new KillAura2(), new KillAura3(),
				new KillAura4(), new KillAura5(), new AntiRotate1(), new NoSlowDown1(), new FastEat1(), new Regen1(),
				new Regen2(), new SkinBlinker1(), new NoSwing1(), new ServerCrasher1(), new ServerCrasher2(),
				new ChestStealer1(), new AntiFire1(), new MultiUse1(), new SelfHarm1(), new AntiKB1(), new Zoot1(),
				new AutoArmor1() };

		for (Check check : checks) {
			activeChecks.add(check);
			check.register(plugin);
		}
	}

	public List<Check> getActiveChecks() {
		return activeChecks;
	}

	public List<CheckType> getCheckTypes() {
		return Arrays.asList(CheckType.values());
	}

	public List<Check> getChecksWithType(CheckType type) {
		return getActiveChecks().stream().filter((check) -> check.getType() == type).collect(Collectors.toList());
	}

	public void registerCheck(Check check) {
		if (activeChecks.contains(check))
			throw new IllegalArgumentException("Check already registered");
		check.register(plugin);
		activeChecks.add(check);
	}
}
