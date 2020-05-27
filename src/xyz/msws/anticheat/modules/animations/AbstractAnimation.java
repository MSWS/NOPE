package xyz.msws.anticheat.modules.animations;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.ActionGroup;
import xyz.msws.anticheat.modules.checks.Check;

/**
 * 
 * @author imodm
 *
 */
public abstract class AbstractAnimation {

	protected NOPE plugin;
	protected Player player;
	protected long startTime;
	@Nullable
	protected ActionGroup action;
	protected Check check;

	public AbstractAnimation(NOPE plugin, Player player, Check check) {
		this.plugin = plugin;
		this.player = player;
		this.check = check;
	}

	public boolean start() {
		this.startTime = System.currentTimeMillis();
		return true;
	}

	public void setEndAction(ActionGroup group) {
		this.action = group;
	}

	public ActionGroup getEndAction() {
		return this.action;
	}

	public abstract void stop(boolean manual);

	public abstract boolean completed();

	public abstract String getName();
}
