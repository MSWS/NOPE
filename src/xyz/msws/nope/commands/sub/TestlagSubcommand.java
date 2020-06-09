package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.AbstractSubcommand;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.modules.checks.TPSManager;
import xyz.msws.nope.utils.MSG;

public class TestlagSubcommand extends AbstractSubcommand {

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

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Test server-sided lag";
	}
}
