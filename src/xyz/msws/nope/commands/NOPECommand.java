package xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.sub.BanwaveSubcommand;
import xyz.msws.nope.commands.sub.ChecksSubcommand;
import xyz.msws.nope.commands.sub.ClearSubcommand;
import xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.LookupSubcommand;
import xyz.msws.nope.commands.sub.OnlineSubcommand;
import xyz.msws.nope.commands.sub.ReloadSubcommand;
import xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
import xyz.msws.nope.commands.sub.ResetSubcommand;
import xyz.msws.nope.commands.sub.StatsSubcommand;
import xyz.msws.nope.commands.sub.TestAnimationSubcommand;
import xyz.msws.nope.commands.sub.TestlagSubcommand;
import xyz.msws.nope.commands.sub.TimeSubcommand;
import xyz.msws.nope.commands.sub.ToggleSubcommand;
import xyz.msws.nope.commands.sub.TrustSubcommand;
import xyz.msws.nope.commands.sub.VLSubcommand;
import xyz.msws.nope.commands.sub.WarnSubcommand;
import xyz.msws.nope.utils.MSG;
import xyz.msws.nope.utils.Utils.Age;

public class NOPECommand extends AbstractCommand {

	public NOPECommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return super.onCommand(sender, command, label, args);
	}

	@Override
	public void sendHelp(CommandSender sender) {
		for (AbstractSubcommand cmd : subcommands) {
			if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission()))
				MSG.tell(sender, "&c/nope " + cmd.getName() + " &e" + cmd.getUsage() + "&8- &7" + cmd.getDescription());
		}
		String bottom = "&l&4[&c&lNOPE&4&l] &e" + plugin.getDescription().getVersion() + " &7created by &bMSWS";
		if (plugin.getPluginInfo() != null)
			bottom += " &7(Online Version is "
					+ (plugin.getPluginInfo().outdated() == Age.OUTDATED_VERSION ? "&a"
							: (plugin.getPluginInfo().outdated() == Age.DEVELOPER_VERSION ? "&c" : "&b"))
					+ plugin.getPluginInfo().getVersion() + "&7)";
		MSG.tell(sender, bottom);
	}

	@Override
	public void enable() {
		super.enable();
		subcommands.add(new TestlagSubcommand(plugin));
		subcommands.add(new ClearSubcommand(plugin));
		subcommands.add(new VLSubcommand(plugin));
		subcommands.add(new ReloadSubcommand(plugin));
		subcommands.add(new ResetSubcommand(plugin));
		subcommands.add(new TimeSubcommand(plugin));
		subcommands.add(new BanwaveSubcommand(plugin));
		subcommands.add(new RemovebanwaveSubcommand(plugin));
		subcommands.add(new StatsSubcommand(plugin));
		subcommands.add(new EnablechecksSubcommand(plugin));
		subcommands.add(new OnlineSubcommand(plugin));
		subcommands.add(new TestAnimationSubcommand(plugin));
		subcommands.add(new WarnSubcommand(plugin));
		subcommands.add(new ToggleSubcommand(plugin));
		subcommands.add(new ChecksSubcommand(plugin));
		subcommands.add(new LookupSubcommand(plugin));
		subcommands.add(new TrustSubcommand(plugin));
	}

	@Override
	public String getName() {
		return "nope";
	}

}
