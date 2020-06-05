package xyz.msws.nope.modules.animations;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.AnimationStartEvent;
import xyz.msws.nope.modules.actions.ActionGroup;
import xyz.msws.nope.modules.checks.Check;

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

	public boolean queue() {
		AnimationStartEvent ase = new AnimationStartEvent(player, this);
		Bukkit.getPluginManager().callEvent(ase);
		if (ase.isCancelled())
			return false;
		this.startTime = System.currentTimeMillis();
		start();
		return true;
	}

	public abstract void start();

	public void setEndAction(ActionGroup group) {
		this.action = group;
	}

	public ActionGroup getEndAction() {
		return this.action;
	}

	public abstract void stop();

	public abstract boolean completed();

	public abstract String getName();

}
