package xyz.msws.anticheat.modules.actions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.actions.CommandAction;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Similar to {@link CommandAction} but the console executes this command
 * 
 * @author imodm
 *
 */
public class ConsoleCommandAction extends AbstractAction {

	private String command;

	public ConsoleCommandAction(NOPE plugin, String command) {
		super(plugin);
		this.command = command;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if (!player.isOnline())
			return;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				MSG.replaceCheckPlaceholder(command, plugin.getCPlayer(player), check));
	}

}
