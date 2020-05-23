package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

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
