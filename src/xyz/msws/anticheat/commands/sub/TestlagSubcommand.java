package xyz.msws.anticheat.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.checks.TPSManager;
import xyz.msws.anticheat.utils.MSG;

public class TestlagSubcommand extends Subcommand {

	public TestlagSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "testlag";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			MSG.tell(sender, "The current lag is set to " + plugin.getModule(TPSManager.class).getDelay() + "ms.");
			return CommandResult.SUCCESS;
		}
		if (!sender.hasPermission("nope.command.lag"))
			return CommandResult.NO_PERMISSION;

		if (!StringUtils.isNumeric(args[1]))
			return CommandResult.INVALID_ARGUMENT;

		long delay = Long.parseLong(args[1]);
		plugin.getModule(TPSManager.class).setDelay(delay);
		MSG.tell(sender, "Successfully set the delay to " + delay);
		return CommandResult.SUCCESS;
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return new ArrayList<>();
	}

	@Override
	public String getUsage() {
		return "<delay>";
	}

}
