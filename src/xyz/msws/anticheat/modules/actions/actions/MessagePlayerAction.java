package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Accessibility option that is effectively just {@link MessageAction} but
 * targets only the player.
 * 
 * @author imodm
 *
 */
public class MessagePlayerAction extends AbstractAction {

	private String message;

	public MessagePlayerAction(NOPE plugin, String message) {
		super(plugin);
		this.message = message;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if (!player.isOnline())
			return;
		MSG.tell(player.getPlayer(), MSG.replaceCheckPlaceholder(message, plugin.getCPlayer(player), check));
	}

}
