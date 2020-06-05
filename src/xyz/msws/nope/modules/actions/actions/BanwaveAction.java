package xyz.msws.nope.modules.actions.actions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.bans.Banwave.BanwaveInfo;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

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
		if (plugin.getModule(Banwave.class).isInBanwave(player.getUniqueId()))
			return;
		CPlayer cp = plugin.getCPlayer(player);

		BanwaveInfo info = plugin.getModule(Banwave.class).new BanwaveInfo(player.getUniqueId(), reason, time);
		plugin.getModule(Banwave.class).addPlayer(player.getUniqueId(), info);
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String r = MSG.replaceCheckPlaceholder(reason, cp, check);

			String token = cp.saveLog(check);
			r = r.replace("%token%", token);

			plugin.getModule(Banwave.class).addPlayer(player.getUniqueId(), info);
		});

	}

}
