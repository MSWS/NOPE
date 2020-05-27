package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Makes the player run a command
 * 
 * @author imodm
 *
 */
public class CommandAction extends AbstractAction {

	private String command;

	public CommandAction(NOPE plugin, String command) {
		super(plugin);
		this.command = command;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if (!player.isOnline())
			return;
		Bukkit.dispatchCommand(player.getPlayer(),
				MSG.replaceCheckPlaceholder(command, plugin.getCPlayer(player), check));
	}

}
