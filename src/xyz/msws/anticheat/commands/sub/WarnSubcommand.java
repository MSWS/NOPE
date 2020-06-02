package xyz.msws.anticheat.commands.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class WarnSubcommand extends Subcommand {

	public WarnSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "warn";
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission("nope.command.warn")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length < 4) {
			MSG.sendHelp(sender, 0, "default");
			return CommandResult.MISSING_ARGUMENT;
		}
		OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
		CPlayer cp = plugin.getCPlayer(t);

		String hackName = "", stringVl = "", current = "";
		for (int i = 2; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("h:")) {
				current = "hack";
				hackName += arg.substring(2);
				continue;
			} else if (arg.startsWith("v:")) {
				current = "vl";
				stringVl += arg.substring(2);
				continue;
			}

			if (current.equals("hack")) {
				hackName += " " + arg;
			} else if (current.equals("vl")) {
				stringVl += " " + arg;
			}
		}

		final String hackNameFinal = hackName;

		cp.flagHack(new Check() {
			@Override
			public boolean lagBack() {
				return true;
			}

			@Override
			public CheckType getType() {
				return CheckType.MISC;
			}

			@Override
			public String getDebugName() {
				return "ManuallyIssued";
			}

			@Override
			public String getCategory() {
				return hackNameFinal;
			}

			@Override
			public void register(NOPE plugin) {
			}

		}, Integer.parseInt(stringVl));

		MSG.tell(sender, "Warned " + t.getName() + " for " + hackName + " (vl: " + stringVl + ")");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "[player] h:[hack] v:[vl]";
	}

}
