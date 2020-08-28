package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.Checks;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.PlayerManager;
import xyz.msws.nope.utils.MSG;

public class ClearSubcommand extends Subcommand {

	public ClearSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		List<String[]> result = new ArrayList<>();

		List<String> checks = new ArrayList<>();
		Set<String> playerNames = Bukkit.getOnlinePlayers().stream().map(n -> n.getName()).collect(Collectors.toSet());

		for (Check check : plugin.getModule(Checks.class).getAllChecks()) {
			if (checks.contains(check.getCategory()))
				continue;
			checks.add(check.getCategory());
		}

		checks.add("all");
		checks.addAll(playerNames);
		result.add(checks.toArray(new String[checks.size()]));
		checks.removeAll(playerNames);
		result.add(checks.toArray(new String[checks.size()]));
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission("nope.command.clear")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length < 3) {
			return CommandResult.MISSING_ARGUMENT;
		}
		String target = "", hack = "";
		CPlayer cp;

		if (args[1].equalsIgnoreCase("all")) {
			target = "everyone's";

			for (UUID uuid : plugin.getModule(PlayerManager.class).getLoadedPlayers()) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
				cp = plugin.getCPlayer(p);
				if (args[2].equalsIgnoreCase("all")) {
					cp.clearVls();
					MSG.sendPluginMessage(null, "clearvl:" + p.getName());
					hack = "all hacks";
				} else {
					boolean found = false;
					for (Check h : plugin.getModule(Checks.class).getAllChecks()) {
						if (args[2].equalsIgnoreCase(h.getCategory())) {
							cp.setSaveData("vls." + h.getCategory(), 0);
							MSG.sendPluginMessage(null, "setvl:" + p.getName() + " " + h + " 0");
							hack = h.getCategory();
							found = true;
							break;
						}
					}
					if (!found) {
						MSG.tell(sender, MSG.getString("Command.Clear.UnknownHack", "Unknown hack"));
						return CommandResult.INVALID_ARGUMENT;
					}
				}
			}
		} else {
			cp = plugin.getCPlayer(Bukkit.getOfflinePlayer(args[1]));
			target = cp.getPlayer().getName() + "'" + (cp.getPlayer().getName().toLowerCase().endsWith("s") ? "" : "s");

			if (args[2].equalsIgnoreCase("all")) {
				cp.clearVls();
				MSG.sendPluginMessage(null, "clearvl:" + cp.getPlayer().getName());
				hack = "all hacks";
			} else {
				boolean found = false;
				for (Check h : plugin.getModule(Checks.class).getAllChecks()) {
					if (args[2].equalsIgnoreCase(h.getCategory())) {
						cp.setSaveData("vls." + h.getCategory(), 0);
						MSG.sendPluginMessage(null, "setvl:" + cp.getPlayer().getName() + " " + h + " 0");
						hack = h.getCategory();
						found = true;
						break;
					}
				}
				if (!found) {
					MSG.tell(sender, MSG.getString("Command.Clear.UnknownHack", "Unknown hack"));
					return CommandResult.INVALID_ARGUMENT;
				}
			}
		}

		MSG.tell(sender, "&7You cleared &e" + target + "&7 VLs for &c" + hack);
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "[target] [hack]";
	}

	@Override
	public String getPermission() {
		return "nope.command.clear";
	}

	@Override
	public String getDescription() {
		return "Clear player VLs";
	}

}
