package xyz.msws.nope.modules.actions.actions;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractConditionalAction;
import xyz.msws.nope.modules.actions.Compare;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.utils.Utils;

/**
 * Another {@link AbstractConditionalAction} that is used for checking the
 * player's ping.
 * 
 * <b>PingSpoof</b> is a thing. Do not use this to allow bypasses.
 * 
 * @author imodm
 *
 */
public class PingCheck extends AbstractConditionalAction {
	private int value;
	private Compare comparer;

	public PingCheck(NOPE plugin, String data) {
		super(plugin);
		String symb = "";
		int firstNumber = "ping".length();
		for (; firstNumber < data.length(); firstNumber++) {
			String c = data.charAt(firstNumber) + "";
			if (StringUtils.isNumeric(c))
				break;
			symb += c;
		}
		comparer = Compare.fromString(symb);
		this.value = Integer.parseInt(data.substring(firstNumber));
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		if (!player.isOnline())
			return false;
		return comparer.check(Utils.getPing(player.getPlayer()), value);
	}

}
