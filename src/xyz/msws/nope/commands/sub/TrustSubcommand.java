package xyz.msws.nope.commands.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.AbstractSubcommand;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.modules.trust.TrustFactor;
import xyz.msws.nope.utils.MSG;

public class TrustSubcommand extends AbstractSubcommand {

	private TrustFactor trust;

	public TrustSubcommand(NOPE plugin) {
		super(plugin);
		trust = plugin.getModule(TrustFactor.class);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "trust";
	}

	@Override
	public String getUsage() {
		return "<player>";
	}

	@Override
	public String getDescription() {
		return "View a player's trust factor";
	}

	@Override
	public String getPermission() {
		return "nope.command.trust";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		Player target = null;
		if (args.length == 1) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				return CommandResult.PLAYER_REQUIRED;
			}
		} else if (sender.hasPermission("nope.command.trust.others")) {
			target = Bukkit.getPlayer(args[1]);
		} else {
			return CommandResult.NO_PERMISSION;
		}
		if (target == null)
			return CommandResult.INVALID_ARGUMENT;

		MSG.tell(sender, trust.format(target.getUniqueId()));

		MSG.tell(sender, MSG.PLAYER + target.getName() + "&7 has a trust factor of " + MSG.NUMBER
				+ trust.recalculate(target.getUniqueId()));

		return CommandResult.SUCCESS;
	}

}
