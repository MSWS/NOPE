package xyz.msws.anticheat.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;

public abstract class Subcommand {

	protected NOPE plugin;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public List<String[]> tabCompletions() {
		return null;
	}

	public abstract String getName();

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
