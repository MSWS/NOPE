package xyz.msws.nope.commands.sub;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.utils.MSG;

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
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
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
			plugin.getModule(Banwave.class).runBanwave().run();
			MSG.tell(sender, MSG.getString("Command.Banwave.Executed", "Executed the banwave"));
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

		Check decoy = new Check() {

			@Override
			public void register(NOPE plugin) throws OperationNotSupportedException {
			}

			@Override
			public boolean lagBack() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CheckType getType() {
				return CheckType.MISC;
			}

			@Override
			public String getDebugName() {
				return "ManuallyIssued#2";
			}

			@Override
			public String getCategory() {
				return "Manual";
			}
		};

		plugin.getModule(Banwave.class).addPlayer(target.getUniqueId(),
				plugin.getModule(Banwave.class).new BanwaveInfo(target.getUniqueId(), decoy, duration, reason));
		MSG.sendPluginMessage(null, "banwave:" + target.getName() + " " + duration + " " + reason);
		MSG.tell(sender, MSG.getString("Command.Banwave.Added", "&7Successfully added &e%player% &7to the banwave.")
				.replace("%player%", target.getName()));
		return CommandResult.SUCCESS;

	}

	@Override
	public String getUsage() {
		return "[player] [duration] [reason]";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Manage the banwave";
	}

}
