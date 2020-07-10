package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.events.player.PlayerFlagEvent;
import xyz.msws.nope.modules.checks.TPSManager;
import xyz.msws.nope.utils.MSG;
import xyz.msws.nope.utils.Utils;

public class TestlagSubcommand extends Subcommand implements Listener {

	public TestlagSubcommand(NOPE plugin) {
		super(plugin);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public String getName() {
		return "testlag";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			MSG.tell(sender, MSG.getString("Command.TestLag.Get", "the delay is &e%delay%&7.").replace("%delay%",
					plugin.getModule(TPSManager.class).getDelay() + ""));
			return CommandResult.SUCCESS;
		}
		if (!sender.hasPermission("nope.command.lag"))
			return CommandResult.NO_PERMISSION;

		if (!StringUtils.isNumeric(args[1]))
			return CommandResult.INVALID_ARGUMENT;

		long delay = Long.parseLong(args[1]);
		plugin.getModule(TPSManager.class).setDelay(delay);
//		MSG.tell(sender, "Successfully set the delay to " + delay);
		MSG.tell(sender, MSG.getString("Command.TestLag.Set", "Successfully set the delay to &e%delay%&7.")
				.replace("%delay%", delay + ""));
		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void onTrigger(PlayerFlagEvent event) {
		if (plugin.getModule(TPSManager.class).getDelay() != 0)
			MSG.tell(event.getPlayer(), "Ping", Utils.getPing(event.getPlayer()) + "");
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
