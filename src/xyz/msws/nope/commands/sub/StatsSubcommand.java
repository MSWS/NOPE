package xyz.msws.nope.commands.sub;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

@Deprecated
public class StatsSubcommand extends Subcommand {

	public StatsSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "stats";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			MSG.tell(sender, "You must be a player.");
			return CommandResult.PLAYER_ONLY;
		}
		if (!sender.hasPermission("nope.command.stats")) {
			return CommandResult.NO_PERMISSION;
		}

		Player player = (Player) sender;
		CPlayer cp = plugin.getCPlayer(player);
		player.openInventory(plugin.getModule(Stats.class).getInventory());
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
		cp.setInventory("stats");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "View check statistics and toggle in a GUI";
	}
}
