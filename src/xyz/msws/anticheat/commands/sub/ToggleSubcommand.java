package xyz.msws.anticheat.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.events.DevModeToggleEvent;
import xyz.msws.anticheat.events.player.PlayerToggleScoreboardEvent;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

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
		CPlayer cp;
		switch (args[1].toLowerCase()) {
			case "dev":
				if (!sender.hasPermission("nope.command.toggle.dev")) {
					return CommandResult.NO_PERMISSION;
				}
				plugin.getConfig().set("DevMode", !plugin.devMode());
				MSG.tell(sender, MSG.getString("Toggle", "you %status% %name%")
						.replace("%status%", enabledDisable(plugin.devMode())).replace("%name%", "Developer Mode"));
				plugin.saveConfig();
				Bukkit.getPluginManager().callEvent(new DevModeToggleEvent(plugin.devMode()));
				break;
			case "debug":
				if (!sender.hasPermission("nope.command.toggle.debug")) {
					return CommandResult.NO_PERMISSION;
				}
				plugin.getConfig().set("DebugMode", !plugin.debugMode());
				MSG.tell(sender, MSG.getString("Toggle", "you %status% %name%")
						.replace("%status%", enabledDisable(plugin.debugMode())).replace("%name%", "Debug Mode"));
				plugin.saveConfig();
				break;
			case "log":
			case "logs":
				if (!sender.hasPermission("nope.command.toggle.logs")) {
					return CommandResult.NO_PERMISSION;
				}
				String nextValue = "";
				String current = plugin.getConfig().getString("Log");
				if (current.equalsIgnoreCase("none")) {
					nextValue = "file";
				} else if (current.equalsIgnoreCase("file")) {
					nextValue = "hastebin";
				} else {
					nextValue = "NONE";
				}

				plugin.getConfig().set("Log", nextValue);
				MSG.tell(sender, MSG.getString("LogToggle", "You set logs to %status%").replace("%status%", nextValue));
				plugin.saveConfig();
				break;
			case "global":
				if (!sender.hasPermission("nope.command.toggle.global")) {
					return CommandResult.NO_PERMISSION;
				}
				plugin.getConfig().set("Global", !plugin.getConfig().getBoolean("Global"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.getConfig().getBoolean("Global")))
								.replace("%name%", "Global"));
				plugin.saveConfig();
				break;
			case "globalscoreboard":
				if (!sender.hasPermission("nope.command.toggle.globalscoreboard")) {
					return CommandResult.NO_PERMISSION;
				}
				plugin.getConfig().set("Scoreboard", !plugin.getConfig().getBoolean("Scoreboard"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.getConfig().getBoolean("Scoreboard")))
								.replace("%name%", "Global Scoreboard"));
				plugin.saveConfig();
				break;
			case "scoreboard":
				if (!sender.hasPermission("nope.command.toggle.scoreboard")) {
					return CommandResult.NO_PERMISSION;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "You must be a player to have a scoreboard.");
					return CommandResult.PLAYER_ONLY;
				}
				cp = plugin.getCPlayer(((Player) sender));
				cp.setSaveData("scoreboard",
						cp.hasSaveData("scoreboard") ? !cp.getSaveData("scoreboard", Boolean.class) : true);
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(cp.getSaveData("scoreboard", Boolean.class)))
								.replace("%name%", "your Scoreboard"));
				Bukkit.getPluginManager().callEvent(
						new PlayerToggleScoreboardEvent((Player) sender, cp.getSaveData("scoreboard", Boolean.class)));
				return CommandResult.SUCCESS;
			default:
				return CommandResult.INVALID_ARGUMENT;
		}
		return CommandResult.SUCCESS;
	}

	private String enabledDisable(boolean toggle) {
		return toggle ? "&aenabled" : "&cdisabled";
	}

	@Override
	public List<String[]> tabCompletions() {
		List<String[]> result = new ArrayList<>();
		result.add(new String[] { "dev", "debug", "log", "global", "globalscoreboard" });
		return result;
	}

	@Override
	public String getUsage() {
		return "[setting]";
	}

}
