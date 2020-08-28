package xyz.msws.nope.commands.sub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.utils.MSG;

public class ResetSubcommand extends Subcommand {

	public ResetSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		List<String[]> result = new ArrayList<>();
		result.add(new String[] { "config", "lang", "all" });
		return result;
	}

	@Override
	public String getName() {
		return "reset";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.reset")) {
			return CommandResult.NO_PERMISSION;
		}

		if (args.length < 2)
			return CommandResult.MISSING_ARGUMENT;

		switch (args[1].toLowerCase()) {
			case "config":
				plugin.saveResource("config.yml", true);
				plugin.setConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")));
				break;
			case "lang":
				plugin.saveResource("lang.yml", true);
				plugin.setLang(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
				break;
			case "all":
				plugin.saveResource("config.yml", true);
				plugin.saveResource("lang.yml", true);
				plugin.setConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")));
				plugin.setLang(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
				break;
		}

		plugin.reload();
		MSG.tell(sender, "&7Successfully reset &e" + args[1] + "&7 files.");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "[config/lang/all]";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Reset all NOPE files";
	}
}
