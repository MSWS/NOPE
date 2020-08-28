package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.AbstractCommand;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.utils.MSG;
import xyz.msws.nope.utils.Utils.Age;

/**
 * 
 * @author imodm
 *
 */
public class HelpSubcommand extends Subcommand {

	private AbstractCommand main;

	private int size;

	public HelpSubcommand(NOPE plugin, AbstractCommand main, int size) {
		super(plugin);
		this.main = main;
		this.size = size;
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "<page>";
	}

	@Override
	public String getDescription() {
		return "Get help about a command";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		int p = 0;
		if (args.length >= 2) {
			try {
				p = Integer.parseInt(args[1]) - 1;
			} catch (NumberFormatException e) {
				return CommandResult.INVALID_ARGUMENT;
			}
		}
		sendHelp(sender, p);
		return CommandResult.SUCCESS;
	}

	public void sendHelp(CommandSender sender, int page) {
		List<String> lines = new ArrayList<>();
		for (int i = 0; i < 2; i++)
			MSG.tell(sender, " ");

		for (Subcommand cmd : main.getSubCommands()) {
			if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission()))
				lines.add(String.format(" &c/%s %s &e%s &8- &7%s", main.getName(), cmd.getName(), cmd.getUsage(),
						cmd.getDescription()));
		}
		MSG.tell(sender, String.format("&7Listing Help for &4/%s &7(Page &e%d &7of &a%d &7(&8/%s help &e[page]&7)",
				main.getName(), page + 1, (int) Math.ceil(lines.size() / size) + 1, main.getName()));
		MSG.tell(sender, " ");

		for (int i = page * size; i < Math.min(lines.size(), (page + 1) * size); i++) {
			MSG.tell(sender, lines.get(i));
		}

		String bottom = "&l&4[&c&lNOPE&4&l] &e" + plugin.getDescription().getVersion() + " &7created by &bMSWS";
		if (plugin.getPluginInfo() != null)
			bottom += " &7(Online Version is "
					+ (plugin.getPluginInfo().outdated() == Age.OUTDATED_VERSION ? "&a"
							: (plugin.getPluginInfo().outdated() == Age.DEVELOPER_VERSION ? "&c" : "&b"))
					+ plugin.getPluginInfo().getVersion() + "&7)";
		MSG.tell(sender, bottom);
	}

}
