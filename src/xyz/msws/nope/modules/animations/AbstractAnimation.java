package xyz.msws.nope.modules.animations;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.AnimationStartEvent;
import xyz.msws.nope.modules.actions.ActionGroup;
import xyz.msws.nope.modules.checks.Check;

/**
 * Represents an animation for a specific player.
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

	/**
	 * Prepare the animation and call {@link AnimationStartEvent} to check if its
	 * cancelled.
	 * 
	 * @return
	 */
	public boolean queue() {
		AnimationStartEvent ase = new AnimationStartEvent(player, this);
		Bukkit.getPluginManager().callEvent(ase);
		if (ase.isCancelled())
			return false;
		this.startTime = System.currentTimeMillis();
		start();
		return true;
	}

	/**
	 * Start the animation
	 */
	public abstract void start();

	/**
	 * Set the end action, this action must be executed by the animation itself.
	 * 
	 * @param group
	 */
	public void setEndAction(ActionGroup group) {
		this.action = group;
	}

	public ActionGroup getEndAction() {
		return this.action;
	}

	/**
	 * Stop the animation This should cancel any runnables and execute the
	 * {@link AbstractAnimation#action}
	 */
	public abstract void stop();

	/**
	 * Check whether the animation is completed
	 * 
	 * @return
	 */
	public abstract boolean completed();

	/**
	 * Get the name of the animation
	 * 
	 * @return
	 */
	public abstract String getName();

}
