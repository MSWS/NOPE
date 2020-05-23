package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

public class MessageAction extends AbstractAction {

	private String target, message;

	public MessageAction(NOPE plugin, String target, String message) {
		super(plugin);
		this.message = message;
		this.target = target;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		String replace = MSG.replaceCheckPlaceholder(message, plugin.getCPlayer(player), check);
		if (target.contains(".")) {
			MSG.tell(target, replace);
			return;
		}
		if (target.equalsIgnoreCase("all")) {
			MSG.announce(replace);
			return;
		}
		MSG.warn("Unknown message target: " + target);
	}

}
