package xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * Represents a subcommand of a main command. It is expected each subcommand
 * handles its logic internally. This can include having sub commands of sub
 * commands.
 * 
 * @author imodm
 *
 */
public abstract class Subcommand {

	protected NOPE plugin;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	public abstract List<String[]> tabCompletions(CommandSender sender, String[] args);

	public abstract String getName();

	public abstract String getUsage();

	public abstract String getDescription();

	public List<String> getAliases() {
		return new ArrayList<>();
	}

	public String getPermission() {
		return null;
	}

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
