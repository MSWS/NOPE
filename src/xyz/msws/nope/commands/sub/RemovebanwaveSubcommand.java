package xyz.msws.nope.commands.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.AbstractSubcommand;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.utils.MSG;

public class RemovebanwaveSubcommand extends AbstractSubcommand {

	public RemovebanwaveSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "removebanwave";
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.removebanwave")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length < 2) {
			return CommandResult.PLAYER_REQUIRED;
		}
		OfflinePlayer off = Bukkit.getOfflinePlayer(args[1]);

		Banwave bw = plugin.getModule(Banwave.class);
		if (!bw.isInBanwave(off.getUniqueId())) {
			MSG.tell(sender, off.getName() + " is not banwaved.");
			return CommandResult.ERROR;
		}
		MSG.sendPluginMessage(null, "removebanwave:" + off.getName());
//		cp.removeSaveData("isBanwaved");
		bw.removePlayer(off.getUniqueId());
		MSG.tell(sender, "Removed " + off.getName() + " from the banwave.");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "[player]";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Remove a player from the banwave";
	}
}
