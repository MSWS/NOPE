package xyz.msws.anticheat.commands.sub;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class StatsSubcommand extends Subcommand {

	public StatsSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public List<String[]> tabCompletions() {
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
		player.openInventory(plugin.getStats().getInventory());
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
		cp.setInventory("stats");
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "";
	}
}
