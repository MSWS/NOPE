package xyz.msws.anticheat.commands.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class BanwaveSubcommand extends Subcommand {

	public BanwaveSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions() {
		return null;
	}

	@Override
	public String getName() {
		return "banwave";
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.banwave")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length > 1) {
			if (!sender.hasPermission("nope.command.banwave.addplayer")) {
				return CommandResult.NO_PERMISSION;
			}
			OfflinePlayer off = Bukkit.getOfflinePlayer(args[1]);
			CPlayer cp = plugin.getCPlayer(off);
			cp.setSaveData("isBanwaved", "Manual [" + sender.getName() + "]");
			MSG.sendPluginMessage(null, "banwave:" + off.getName() + " Manual");
			MSG.tell(sender, "Added " + off.getName() + " to the banwave.");
			return CommandResult.SUCCESS;
		}
		MSG.sendPluginMessage(null, "banwave");
		plugin.getBanwave().runBanwave(true).run();
		MSG.tell(sender, "&cSuccessfully initiated banwave.");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}

}
