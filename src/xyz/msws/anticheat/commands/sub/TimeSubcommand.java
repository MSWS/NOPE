package xyz.msws.anticheat.commands.sub;

import java.util.List;

import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.utils.MSG;

public class TimeSubcommand extends Subcommand {

	public TimeSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions() {
		return null;
	}

	@Override
	public String getName() {
		return "time";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.time")) {
			return CommandResult.NO_PERMISSION;
		}
		MSG.tell(sender,
				"&4&l[&c&lNOPE&4&l] &7Next banwave: &e" + MSG.getTime(plugin.getBanwave().timeToNextBanwave()));
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}

}
