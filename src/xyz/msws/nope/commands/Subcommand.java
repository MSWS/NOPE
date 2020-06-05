package xyz.msws.nope.commands;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

public abstract class Subcommand {

	protected NOPE plugin;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	public abstract List<String[]> tabCompletions(CommandSender sender);

	public abstract String getName();

	public abstract String getUsage();

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
