package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.data.CPlayer;

public class CancelAction extends AbstractAction {

	public CancelAction(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if(!player.isOnline())
			return;
		CPlayer cp = plugin.getCPlayer(player);
		player.getPlayer().teleport(cp.getLastSafeLocation());
	}

}
