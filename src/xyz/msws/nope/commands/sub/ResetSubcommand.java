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
	public List<String[]> tabCompletions(CommandSender sender) {
		return new ArrayList<>();
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.reset")) {
			return CommandResult.NO_PERMISSION;
		}
		plugin.saveResource("config.yml", true);
		plugin.saveResource("lang.yml", true);
		plugin.setConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")));
		plugin.setLang(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
		MSG.tell(sender, "Succesfully reset.");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}

}
