package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Logs the specified message to either CONSOLE, FILE, or INGAME. {@link Type}
 * 
 * @author imodm
 *
 */
public class LogAction extends AbstractAction {

	private Type type;
	private String message;

	public LogAction(NOPE plugin, Type type, String message) {
		super(plugin);
		this.type = type;
		this.message = message;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		String msg = MSG.color(MSG.replaceCheckPlaceholder(message, plugin.getCPlayer(player), check));
		switch (type) {
			case CONSOLE:
				MSG.log(msg);
				break;
			case FILE:
				plugin.getCPlayer(player).addLogMessage(msg);
				break;
			case INGAME:
				MSG.tell("nope.message.normal", msg);
				break;
		}
	}

	public enum Type {
		CONSOLE, FILE, INGAME;
	}

}
