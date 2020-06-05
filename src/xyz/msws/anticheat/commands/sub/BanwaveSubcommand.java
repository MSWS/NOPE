package xyz.msws.anticheat.commands.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.bans.Banwave;
import xyz.msws.anticheat.utils.MSG;

/**
 * 
 * @author imodm
 *
 */
public class BanwaveSubcommand extends Subcommand {

	public BanwaveSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "banwave";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.banwave")) {
			return CommandResult.NO_PERMISSION;
		}
		if (args.length == 1) {
			MSG.sendPluginMessage(null, "banwave");
			plugin.getModule(Banwave.class).runBanwave(true).run();
//			MSG.tell(sender, "&cSuccessfully initiated banwave.");
			MSG.tell(sender, MSG.getString("BanwaveExecuted", "Executed the banwave"));
			return CommandResult.SUCCESS;
		}

		if (args.length < 4) {
			return CommandResult.MISSING_ARGUMENT;
		}

		if (!sender.hasPermission("nope.command.banwave.addplayer")) {
			return CommandResult.NO_PERMISSION;
		}

		Player target = Bukkit.getPlayer(args[1]);
		long duration = Long.parseLong(args[2]);
		StringBuilder builder = new StringBuilder();
		for (int i = 3; i < args.length; i++)
			builder.append(args[i]).append(" ");
		String reason = builder.toString().trim();

		if (target == null) {
			return CommandResult.INVALID_ARGUMENT;
		}

		plugin.getModule(Banwave.class).addPlayer(target.getUniqueId(),
				plugin.getModule(Banwave.class).new BanwaveInfo(target.getUniqueId(), reason, duration));
		MSG.sendPluginMessage(null, "banwave:" + target.getName() + " Manual");
		MSG.tell(sender, MSG.getString("BanwaveAdded", "&7Successfully added &e%player% &7to the banwave.")
				.replace("%player%", target.getName()));
		return CommandResult.SUCCESS;

	}

	@Override
	public String getUsage() {
		return "[player] [duration] [reason]";
	}

}
