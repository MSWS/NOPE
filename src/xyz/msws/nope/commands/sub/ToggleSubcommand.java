package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.events.global.OptionChangeEvent;
import xyz.msws.nope.events.player.PlayerOptionChangeEvent;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Option;
import xyz.msws.nope.modules.data.PlayerOption;
import xyz.msws.nope.utils.MSG;

public class ToggleSubcommand extends Subcommand {

	public ToggleSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "toggle";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length < 2)
			return CommandResult.MISSING_ARGUMENT;

		String id = args[1];

		if (sender instanceof Player) {
			CPlayer cp = plugin.getCPlayer((Player) sender);
			Option option = cp.getOption(id);

			if (option != null) {
				if (!sender.hasPermission("nope.command.toggle." + id))
					return CommandResult.NO_PERMISSION;
				Object value = args.length > 2 ? args[2] : option.toggle();
				if (value == null)
					return CommandResult.MISSING_ARGUMENT;

				PlayerOptionChangeEvent poce = new PlayerOptionChangeEvent((Player) sender, (PlayerOption) option);
				Bukkit.getPluginManager().callEvent(poce);

				MSG.tell(sender,
						MSG.getString("Command.Toggle.PlayerOption",
								"&4NOPE > &7Successfully your &a%option%&7 to &e%value%&7.").replace("%option%", id)
								.replace("%value%", value.toString()));
				return CommandResult.SUCCESS;
			}
		}

		Option option = plugin.getOption(id);

		if (option == null)
			return CommandResult.INVALID_ARGUMENT;

		if (!sender.hasPermission("nope.command.toggle." + id))
			return CommandResult.NO_PERMISSION;

		Object value = args.length > 2 ? option.set(args[2]) : option.toggle();
		if (value == null)
			return CommandResult.MISSING_ARGUMENT;

		OptionChangeEvent event = new OptionChangeEvent(option);
		Bukkit.getPluginManager().callEvent(event);

		MSG.tell(sender,
				MSG.getString("Command.Toggle.GlobalOption",
						"&4NOPE > &7Successfully the &a%option%&7 option to &e%value%&7.").replace("%option%", id)
						.replace("%value%", value.toString()));
		plugin.saveConfig();
		return CommandResult.SUCCESS;
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		List<String[]> result = new ArrayList<>();
		List<String> first = new ArrayList<>();
		first.addAll(plugin.getOptionMappings().keySet());

		if (sender instanceof Player) {
			CPlayer cp = plugin.getCPlayer((Player) sender);
			first.addAll(cp.getOptionMappings().keySet());
		}
		result.add(first.toArray(new String[0]));
		return result;
	}

	@Override
	public String getUsage() {
		return "[setting] <value>";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Toggle settings";
	}
}
