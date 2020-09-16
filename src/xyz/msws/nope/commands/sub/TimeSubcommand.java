package xyz.msws.nope.commands.sub;

import java.util.List;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.utils.MSG;

public class TimeSubcommand extends Subcommand {

	public TimeSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public String getName() {
		return "time";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.time"))
			return CommandResult.NO_PERMISSION;

		String time = MSG.getTime(plugin.getModule(Banwave.class).timeToNextBanwave());
		MSG.tell(sender,
				MSG.getString("Command.Time", "The next banwave will happen in &e%time%&7.").replace("%time%", time));
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "View time until next banwave";
	}
}
