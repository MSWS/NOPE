package xyz.msws.anticheat.actions;

import org.apache.commons.lang.StringUtils;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.actions.BanAction;
import xyz.msws.anticheat.actions.actions.BanwaveAction;
import xyz.msws.anticheat.actions.actions.CancelAction;
import xyz.msws.anticheat.actions.actions.CommandAction;
import xyz.msws.anticheat.actions.actions.CustomAction;
import xyz.msws.anticheat.actions.actions.DelayAction;
import xyz.msws.anticheat.actions.actions.IsDevCheck;
import xyz.msws.anticheat.actions.actions.KickAction;
import xyz.msws.anticheat.actions.actions.LogAction;
import xyz.msws.anticheat.actions.actions.MessageAction;
import xyz.msws.anticheat.actions.actions.MessagePlayerAction;
import xyz.msws.anticheat.actions.actions.NotDevCheck;
import xyz.msws.anticheat.actions.actions.PingCheck;
import xyz.msws.anticheat.actions.actions.RandomCheck;
import xyz.msws.anticheat.actions.actions.TPSCheck;
import xyz.msws.anticheat.actions.actions.VLActionCheck;
import xyz.msws.anticheat.actions.actions.LogAction.Type;
import xyz.msws.anticheat.utils.MSG;

/**
 * Creates actions from an action string
 * 
 * The format for an action is lable:[information] each action can have separate
 * info
 * 
 * {@link AbstractAction}
 * 
 * @author imodm
 *
 */
public class ActionFactory {

	private NOPE plugin;
	private ActionManager manager;

	public ActionFactory(NOPE plugin, ActionManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}

	public AbstractAction createAction(String data) {
		String rawData = data;
		if (data.contains("|")) {
			MSG.error("Invalid action format: " + data);
			return null;
		}

		if (manager.hasAction(data)) {
			// If the player has defined a custom command
			return new CustomAction(plugin, data);
		}

		data = data.toLowerCase();
		if (data.startsWith("cancel"))
			return new CancelAction(plugin);
		if (data.startsWith("kick:"))
			return new KickAction(plugin, rawData.substring("kick:".length()));
		if (data.startsWith("pmsg:"))
			return new MessagePlayerAction(plugin, rawData.substring("pmsg:".length()));
		if (data.startsWith("msg:")) {
			String target = rawData.substring("msg:".length(), rawData.indexOf(":", "msg:".length()));
			return new MessageAction(plugin, target, rawData.substring("msg:".length() + target.length() + 1));
		}
		if (data.startsWith("cmd:"))
			return new CommandAction(plugin, rawData.substring("cmd:".length()));
		if (data.startsWith("ccmd:"))
			return new ConsoleCommandAction(plugin, rawData.substring("ccmd:".length()));
		if (data.startsWith("vl"))
			return new VLActionCheck(plugin, rawData);
		if (data.startsWith("ping"))
			return new PingCheck(plugin, rawData);
		if (data.startsWith("tps"))
			return new TPSCheck(plugin, rawData);
		if (data.startsWith("delay:")) {
			String time = data.substring("delay:".length());
			if (!StringUtils.isNumeric(time)) {
				MSG.error("Invalid delay duration: " + time);
				return null;
			}
			return new DelayAction(plugin, Long.parseLong(time));
		}
		if (data.startsWith("log:")) {
			try {
				String logType = rawData.substring("log:".length(), rawData.indexOf(":", "log:".length()));
				String message = rawData.substring("log:".length() + logType.length() + 1);
				if (manager.hasWebHook(logType))
					return new LogAction(plugin, manager.getWebHook(logType), message);
				return new LogAction(plugin, Type.valueOf(logType), message);
			} catch (IllegalArgumentException e) {
				MSG.error("Invalid log format: " + rawData.substring("log:".length()));
				return null;
			}
		}
		if (data.startsWith("ban:") || data.startsWith("banwave:")) { // Handle ban and banwave both
			long time;
			int len = data.startsWith("ban:") ? "ban:".length() : "banwave:".length();
			String reason;
			String timeString = rawData.substring(len, rawData.indexOf(":", len));
			if (!StringUtils.isNumeric(timeString)) {
				MSG.error("Invalid ban duration: " + timeString);
				return null;
			}
			time = Long.parseLong(timeString);
			reason = rawData.substring(len + timeString.length() + 1);
			return data.startsWith("banwave") ? new BanwaveAction(plugin, time, reason)
					: new BanAction(plugin, time, reason);
		}
		if (data.startsWith("rnd:")) {
			try {
				return new RandomCheck(plugin, Double.parseDouble(rawData.substring("rnd:".length())));
			} catch (NumberFormatException e) {
				MSG.error("Invalid rnd number: " + rawData.substring("rnd:".length()));
				return null;
			}
		}
		if (data.startsWith("notdev"))
			return new NotDevCheck(plugin);
		if (data.startsWith("isdev"))
			return new IsDevCheck(plugin);

		MSG.error("Invalid action format: " + rawData);
		return null;
	}
}
