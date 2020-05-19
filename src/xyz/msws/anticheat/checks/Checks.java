package xyz.msws.anticheat.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;

import com.google.common.collect.Sets;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.combat.AntiKB1;
import xyz.msws.anticheat.checks.combat.AutoArmor1;
import xyz.msws.anticheat.checks.combat.AutoClicker1;
import xyz.msws.anticheat.checks.combat.FastBow1;
import xyz.msws.anticheat.checks.combat.HighCPS1;
import xyz.msws.anticheat.checks.combat.HighCPS2;
import xyz.msws.anticheat.checks.combat.HighCPS3;
import xyz.msws.anticheat.checks.combat.KillAura2;
import xyz.msws.anticheat.checks.combat.KillAura5;
import xyz.msws.anticheat.checks.combat.KillAura6;
import xyz.msws.anticheat.checks.combat.KillAura7;
import xyz.msws.anticheat.checks.movement.AntiAFK1;
import xyz.msws.anticheat.checks.movement.AntiRotate1;
import xyz.msws.anticheat.checks.movement.AutoWalk1;
import xyz.msws.anticheat.checks.movement.BHop1;
import xyz.msws.anticheat.checks.movement.ClonedMovement1;
import xyz.msws.anticheat.checks.movement.FastClimb1;
import xyz.msws.anticheat.checks.movement.FastSneak1;
import xyz.msws.anticheat.checks.movement.Flight1;
import xyz.msws.anticheat.checks.movement.Flight2;
import xyz.msws.anticheat.checks.movement.Flight3;
import xyz.msws.anticheat.checks.movement.Flight4;
import xyz.msws.anticheat.checks.movement.Flight5;
import xyz.msws.anticheat.checks.movement.Flight6;
import xyz.msws.anticheat.checks.movement.Glide1;
import xyz.msws.anticheat.checks.movement.GlobalSprint1;
import xyz.msws.anticheat.checks.movement.InventoryMove1;
import xyz.msws.anticheat.checks.movement.Jesus1;
import xyz.msws.anticheat.checks.movement.Jesus2;
import xyz.msws.anticheat.checks.movement.NoSlowDown1;
import xyz.msws.anticheat.checks.movement.NoSlowDown2;
import xyz.msws.anticheat.checks.movement.NoSlowDown3;
import xyz.msws.anticheat.checks.movement.NoSlowDown4;
import xyz.msws.anticheat.checks.movement.NoSlowDown5;
import xyz.msws.anticheat.checks.movement.NoWeb1;
import xyz.msws.anticheat.checks.movement.Speed1;
import xyz.msws.anticheat.checks.movement.Speed2;
import xyz.msws.anticheat.checks.movement.Speed3;
import xyz.msws.anticheat.checks.movement.Spider1;
import xyz.msws.anticheat.checks.player.AntiFire1;
import xyz.msws.anticheat.checks.player.AutoSwitch1;
import xyz.msws.anticheat.checks.player.AutoTool1;
import xyz.msws.anticheat.checks.player.ChestStealer1;
import xyz.msws.anticheat.checks.player.FastEat1;
import xyz.msws.anticheat.checks.player.GhostHand2;
import xyz.msws.anticheat.checks.player.NoFall1;
import xyz.msws.anticheat.checks.player.SafeWalk1;
import xyz.msws.anticheat.checks.player.SelfHarm1;
import xyz.msws.anticheat.checks.player.Zoot1;
import xyz.msws.anticheat.checks.render.AutoSneak1;
import xyz.msws.anticheat.checks.render.InvalidMovement1;
import xyz.msws.anticheat.checks.render.NoSwing1;
import xyz.msws.anticheat.checks.render.SkinBlinker1;
import xyz.msws.anticheat.checks.render.Spinbot1;
import xyz.msws.anticheat.checks.tick.Blink1;
import xyz.msws.anticheat.checks.tick.Timer1;
import xyz.msws.anticheat.checks.world.AutoBuild1;
import xyz.msws.anticheat.checks.world.FastBreak1;
import xyz.msws.anticheat.checks.world.IllegalBlockBreak1;
import xyz.msws.anticheat.checks.world.IllegalBlockPlace1;
import xyz.msws.anticheat.checks.world.Scaffold1;
import xyz.msws.anticheat.checks.world.Scaffold2;
import xyz.msws.anticheat.checks.world.Scaffold3;
import xyz.msws.anticheat.utils.MSG;

@SuppressWarnings("deprecation")
public class Checks {
	private NOPE plugin;
	private List<Check> activeChecks;

	private Set<Check> checkList = new HashSet<>();

	public Checks(NOPE plugin) {
		this.plugin = plugin;
		activeChecks = new ArrayList<Check>();

		checkList.addAll(Sets.newHashSet(new Flight1(), new Flight2(), new Flight3(), new Flight4(), new Flight5(),
				new Flight6(), new Speed1(), new Speed2(), new Speed3(), new ClonedMovement1(), new Blink1(),
				new Timer1(), new NoFall1(), new Scaffold1(), new Scaffold2(), new Scaffold3(), new FastClimb1(),
				new Jesus1(), new FastBow1(), new FastSneak1(), new InvalidMovement1(), new Spinbot1(),
				new IllegalBlockBreak1(), new IllegalBlockPlace1(), new NoWeb1(), new AutoWalk1(), new AutoClicker1(),
				new HighCPS1(), new HighCPS2(), new HighCPS3(), new AntiAFK1(), new AutoSneak1(), new InventoryMove1(),
				new KillAura2(), new KillAura6(), new AntiRotate1(), new NoSlowDown1(), new NoSlowDown2(),
				new NoSlowDown3(), new NoSlowDown4(), new FastEat1(), new ChestStealer1(), new AntiFire1(),
				new SelfHarm1(), new AntiKB1(), new Zoot1(), new AutoArmor1(), new SafeWalk1(), new AutoTool1(),
				new AutoSwitch1(), new FastBreak1(), new Spider1(), new KillAura7(), new Glide1(), new BHop1(),
				new GhostHand2(), new GlobalSprint1(), new Jesus2(), new AutoBuild1()));

		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			checkList.addAll(Sets.newHashSet(new NoSlowDown5(), new KillAura5(), new SkinBlinker1(), new NoSwing1()));
		} else {
			MSG.warn("ProtocolLib is not enabled, certain checks will not work.");
		}
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
		if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled"))
			return Result.DISABLED_NAME;
		if (!plugin.getConfig()
				.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled"))
			return Result.DISABLED_CATEGORY;
		if (!plugin.getConfig().getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory()
				+ "." + check.getDebugName() + ".Enabled"))
			return Result.DISABLED_DEBUG;
		try {
			check.register(plugin);
		} catch (OperationNotSupportedException e) {
//			e.printStackTrace();
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
}
