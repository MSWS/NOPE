package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.Banwave.BanwaveInfo;

/**
 * TODO A minor rework is required to support tokens and proper logging related
 * to Actions
 * 
 * @author imodm
 *
 */
public class BanwaveAction extends AbstractAction {

	private long time;
	private String reason;

	public BanwaveAction(NOPE plugin, long time, String reason) {
		super(plugin);
		this.time = time;
		this.reason = reason;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		BanwaveInfo info = plugin.getBanwave().new BanwaveInfo(player.getUniqueId(), reason, time);
		plugin.getBanwave().addPlayer(player.getUniqueId(), info);
	}

}
