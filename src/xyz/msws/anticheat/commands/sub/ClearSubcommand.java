package xyz.msws.anticheat.commands.sub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.modules.data.PlayerManager;
import xyz.msws.anticheat.utils.MSG;

public class ClearSubcommand extends Subcommand {

	public ClearSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public List<String[]> tabCompletions() {
		List<String[]> result = new ArrayList<>();
		result.add(new String[] { "all" });
		result.add(new String[] { "all", "Flight" });
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission("nope.command.clear")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length < 3) {
			MSG.sendHelp(sender, 0, "default");
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
					for (Check h : plugin.getChecks().getAllChecks()) {
						if (args[2].equalsIgnoreCase(h.getCategory())) {
							cp.setSaveData("vls." + h.getCategory(), 0);
							MSG.sendPluginMessage(null, "setvl:" + p.getName() + " " + h + " 0");
							hack = h.getCategory();
							found = true;
							break;
						}
					}
					if (!found) {
						MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
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
				for (Check h : plugin.getChecks().getAllChecks()) {
					if (args[2].equalsIgnoreCase(h.getCategory())) {
						cp.setSaveData("vls." + h.getCategory(), 0);
						MSG.sendPluginMessage(null, "setvl:" + cp.getPlayer().getName() + " " + h + " 0");
						hack = h.getCategory();
						found = true;
						break;
					}
				}
				if (!found) {
					MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
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

}
