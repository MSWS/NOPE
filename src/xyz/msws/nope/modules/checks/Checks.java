package xyz.msws.nope.modules.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;

import com.google.common.collect.Sets;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.checks.combat.AntiKB1;
import xyz.msws.nope.checks.combat.AutoArmor1;
import xyz.msws.nope.checks.combat.AutoClicker1;
import xyz.msws.nope.checks.combat.FastBow1;
import xyz.msws.nope.checks.combat.HighCPS1;
import xyz.msws.nope.checks.combat.HighCPS2;
import xyz.msws.nope.checks.combat.HighCPS3;
import xyz.msws.nope.checks.combat.killaura.KillAura1;
import xyz.msws.nope.checks.combat.killaura.KillAura2;
import xyz.msws.nope.checks.combat.killaura.KillAura3;
import xyz.msws.nope.checks.combat.killaura.KillAura4;
import xyz.msws.nope.checks.combat.killaura.KillAura5;
import xyz.msws.nope.checks.movement.AntiAFK1;
import xyz.msws.nope.checks.movement.AntiRotate1;
import xyz.msws.nope.checks.movement.AutoWalk1;
import xyz.msws.nope.checks.movement.BHop1;
import xyz.msws.nope.checks.movement.ClonedMovement1;
import xyz.msws.nope.checks.movement.ElytraFlight1;
import xyz.msws.nope.checks.movement.FastClimb1;
import xyz.msws.nope.checks.movement.FastSneak1;
import xyz.msws.nope.checks.movement.Glide1;
import xyz.msws.nope.checks.movement.InventoryMove1;
import xyz.msws.nope.checks.movement.NoWeb1;
import xyz.msws.nope.checks.movement.Spider1;
import xyz.msws.nope.checks.movement.flight.Flight1;
import xyz.msws.nope.checks.movement.flight.Flight2;
import xyz.msws.nope.checks.movement.flight.Flight3;
import xyz.msws.nope.checks.movement.flight.Flight4;
import xyz.msws.nope.checks.movement.flight.Flight5;
import xyz.msws.nope.checks.movement.flight.Flight6;
import xyz.msws.nope.checks.movement.jesus.Jesus1;
import xyz.msws.nope.checks.movement.jesus.Jesus2;
import xyz.msws.nope.checks.movement.noslowdown.NoSlowDown1;
import xyz.msws.nope.checks.movement.noslowdown.NoSlowDown2;
import xyz.msws.nope.checks.movement.noslowdown.NoSlowDown3;
import xyz.msws.nope.checks.movement.noslowdown.NoSlowDown4;
import xyz.msws.nope.checks.movement.noslowdown.NoSlowDown5;
import xyz.msws.nope.checks.movement.speed.Speed1;
import xyz.msws.nope.checks.movement.speed.Speed2;
import xyz.msws.nope.checks.movement.speed.Speed3;
import xyz.msws.nope.checks.movement.speed.Speed4;
import xyz.msws.nope.checks.packet.AutoSneak1;
import xyz.msws.nope.checks.packet.Blink1;
import xyz.msws.nope.checks.packet.HealthTags1;
import xyz.msws.nope.checks.packet.InvalidMovement1;
import xyz.msws.nope.checks.packet.NoSwing1;
import xyz.msws.nope.checks.packet.PlayerESP1;
import xyz.msws.nope.checks.packet.PlayerESP2;
import xyz.msws.nope.checks.packet.SkinBlinker1;
import xyz.msws.nope.checks.packet.Spinbot1;
import xyz.msws.nope.checks.packet.Timer1;
import xyz.msws.nope.checks.player.AntiFire1;
import xyz.msws.nope.checks.player.AutoSwitch1;
import xyz.msws.nope.checks.player.AutoTool1;
import xyz.msws.nope.checks.player.FastEat1;
import xyz.msws.nope.checks.player.GhostHand2;
import xyz.msws.nope.checks.player.NoFall1;
import xyz.msws.nope.checks.player.NoFall2;
import xyz.msws.nope.checks.player.SafeWalk1;
import xyz.msws.nope.checks.player.Zoot1;
import xyz.msws.nope.checks.world.AutoBuild1;
import xyz.msws.nope.checks.world.FastBreak1;
import xyz.msws.nope.checks.world.IllegalBlockBreak1;
import xyz.msws.nope.checks.world.IllegalBlockPlace1;
import xyz.msws.nope.checks.world.Scaffold1;
import xyz.msws.nope.checks.world.Scaffold2;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

/**
 * Responsible for registering and keeping track of checks.
 * 
 * @author imodm
 *
 */
public class Checks extends AbstractModule {
	private List<Check> activeChecks;

	private Set<Check> checkList = new HashSet<>();

	public Checks(NOPE plugin) {
		super(plugin);
	}

	public void registerChecks() {
		for (Check check : checkList) {
			Result result = registerCheck(check);
			if (result != Result.SUCCESS)
				MSG.log("&cRegistration for " + check.getDebugName() + " is disabled (&e" + result.toString() + "&c)");
		}
	}

	public Check getCheckByDebug(String debugName) {
		for (Check check : activeChecks) {
			if (check.getDebugName().equals(debugName))
				return check;
		}
		return null;
	}

	public Set<Check> getAllChecks() {
		return checkList;
	}

	public boolean isCheckEnabled(Check check) {
		return activeChecks.contains(check);
	}

	public List<CheckType> getCheckTypes() {
		return Arrays.asList(CheckType.values());
	}

	public List<Check> getChecksWithType(CheckType type) {
		return getAllChecks().stream().filter((check) -> check.getType() == type).collect(Collectors.toList());
	}

	public List<Check> getChecksByCategory(String category) {
		return getAllChecks().stream().filter((check) -> check.getCategory().equals(category))
				.collect(Collectors.toList());
	}

	public Result registerCheck(Check check) {
		if (activeChecks.contains(check))
			return Result.ALREADY_REGISTERED;
		String key = "Checks." + MSG.camelCase(check.getType().toString()) + ".Enabled";
		if (!plugin.getConfig().isSet(key))
			plugin.getConfig().set(key, true);
		if (!plugin.getConfig().getBoolean(key))
			return Result.DISABLED_NAME;

		key = "Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled";
		if (!plugin.getConfig().isSet(key))
			plugin.getConfig().set(key, true);
		if (!plugin.getConfig().getBoolean(key))
			return Result.DISABLED_CATEGORY;

		key = "Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "." + check.getDebugName()
				+ ".Enabled";
		if (!plugin.getConfig().isSet(key))
			plugin.getConfig().set(key, true);
		if (!plugin.getConfig().getBoolean(key))
			return Result.DISABLED_DEBUG;
		try {
			check.register(plugin);
		} catch (OperationNotSupportedException e) {
			return Result.MISSING_DEPENDENCY;
		}
		activeChecks.add(check);
		return Result.SUCCESS;
	}

	enum Result {
		ALREADY_REGISTERED, DISABLED_NAME, DISABLED_CATEGORY, DISABLED_DEBUG, NOT_SUPPORTED, MISSING_DEPENDENCY,
		WRONG_VERSION, DEPRECATED, SUCCESS;

		@Override
		public String toString() {
			return MSG.camelCase(super.toString());
		}
	}

	@Override
	public void enable() {
		activeChecks = new ArrayList<Check>();

		checkList.addAll(Sets.newHashSet(new Flight1(), new Flight2(), new Flight3(), new Flight4(), new Flight5(),
				new Flight6(), new Speed1(), new Speed2(), new Speed3(), new ClonedMovement1(), new Blink1(),
				new Scaffold1(), new Scaffold2(), new FastClimb1(), new FastBow1(), new FastSneak1(),
				new InvalidMovement1(), new Spinbot1(), new IllegalBlockBreak1(), new IllegalBlockPlace1(),
				new NoWeb1(), new AutoWalk1(), new AutoClicker1(), new HighCPS1(), new HighCPS2(), new HighCPS3(),
				new AntiAFK1(), new AutoSneak1(), new InventoryMove1(), new AntiRotate1(), new NoSlowDown1(),
				new NoSlowDown2(), new NoSlowDown3(), new NoSlowDown4(), new FastEat1(), new AntiFire1(), new AntiKB1(),
				new Zoot1(), new SafeWalk1(), new AutoTool1(), new AutoSwitch1(), new FastBreak1(), new Spider1(),
				new Glide1(), new BHop1(), new GhostHand2(), new Speed4(), new AutoBuild1(), new Speed4(), new Jesus1(),
				new Jesus2(), new ElytraFlight1(), new KillAura1(), new KillAura2(), new NoFall1(),
				new NoFall2()));

		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			checkList.addAll(Sets.newHashSet(new NoSlowDown5(), new SkinBlinker1(), new NoSwing1(), new Timer1(),
					new AutoArmor1(), new KillAura3(), new KillAura4(), new KillAura5(), new HealthTags1(),
					new PlayerESP1(), new PlayerESP2()));
		} else {
			MSG.warn("ProtocolLib is not enabled, certain checks will not work.");
		}

		registerChecks();
	}

	@Override
	public void disable() {
		for (Check check : activeChecks)
			check.disable();
		activeChecks.clear();
	}
}
