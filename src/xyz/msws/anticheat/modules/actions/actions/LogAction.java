package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.actions.Webhook;
import xyz.msws.anticheat.modules.checks.Check;
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
	private Webhook hook;

	public LogAction(NOPE plugin, Type type, String message) {
		super(plugin);
		this.type = type;
		this.message = message;
	}

	public LogAction(NOPE plugin, Webhook hook, String message) {
		super(plugin);
		this.type = Type.WEBHOOK;
		this.hook = hook;
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
			case WEBHOOK:
				hook.sendMessage(msg, plugin.getCPlayer(player), check);
				break;
		}
	}

	public enum Type {
		CONSOLE, FILE, INGAME, WEBHOOK;
	}

}
