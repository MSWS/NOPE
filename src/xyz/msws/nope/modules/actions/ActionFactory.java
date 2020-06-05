package xyz.msws.nope.modules.actions;

import org.apache.commons.lang.StringUtils;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.actions.AddVLAction;
import xyz.msws.nope.modules.actions.actions.AnimationAction;
import xyz.msws.nope.modules.actions.actions.BanAction;
import xyz.msws.nope.modules.actions.actions.BanwaveAction;
import xyz.msws.nope.modules.actions.actions.CancelAction;
import xyz.msws.nope.modules.actions.actions.CommandAction;
import xyz.msws.nope.modules.actions.actions.CustomAction;
import xyz.msws.nope.modules.actions.actions.DelayAction;
import xyz.msws.nope.modules.actions.actions.IsDevCheck;
import xyz.msws.nope.modules.actions.actions.KickAction;
import xyz.msws.nope.modules.actions.actions.LogAction;
import xyz.msws.nope.modules.actions.actions.LogAction.Type;
import xyz.msws.nope.modules.actions.actions.MessageAction;
import xyz.msws.nope.modules.actions.actions.MessagePlayerAction;
import xyz.msws.nope.modules.actions.actions.NotDevCheck;
import xyz.msws.nope.modules.actions.actions.PingCheck;
import xyz.msws.nope.modules.actions.actions.RandomCheck;
import xyz.msws.nope.modules.actions.actions.SetVLAction;
import xyz.msws.nope.modules.actions.actions.TPSCheck;
import xyz.msws.nope.modules.actions.actions.VLActionCheck;
import xyz.msws.nope.modules.animations.AnimationManager.AnimationType;
import xyz.msws.nope.utils.MSG;

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
		if (data.startsWith("animation:")) {
			try {
				String animType = rawData.substring("animation:".length(), rawData.indexOf(":", "animation:".length()));
				String customGroup = rawData.substring("animation:".length() + animType.length() + 1);
				return new AnimationAction(plugin, AnimationType.valueOf(animType), customGroup);
			} catch (IllegalArgumentException e) {
				MSG.error("Invalid animation format: " + rawData.substring("animation:".length()));
				return null;
			}
		}
		if (data.startsWith("rnd:")) {
			try {
				return new RandomCheck(plugin, Double.parseDouble(rawData.substring("rnd:".length())));
			} catch (NumberFormatException e) {
				MSG.error("Invalid rnd number: " + rawData.substring("rnd:".length()));
				return null;
			}
		}
		if (data.startsWith("setvl:") || data.startsWith("addvl:")) {
			String num = rawData.substring("setvl:".length());
			if (!StringUtils.isNumeric(num.startsWith("-") ? num.substring(1) : num)) {
				MSG.error("Invalid VL: " + num);
				return null;
			}
			return data.startsWith("addvl:") ? new AddVLAction(plugin, Integer.parseInt(num))
					: new SetVLAction(plugin, Integer.parseInt(num));
		}
		if (data.startsWith("notdev"))
			return new NotDevCheck(plugin);
		if (data.startsWith("isdev"))
			return new IsDevCheck(plugin);

		MSG.error("Invalid action format: " + rawData);
		return null;
	}
}
