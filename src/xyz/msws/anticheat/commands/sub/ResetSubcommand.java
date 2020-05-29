package xyz.msws.anticheat.commands.sub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.utils.MSG;

public class ResetSubcommand extends Subcommand {

	public ResetSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions() {
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
