package xyz.msws.nope.events.animation;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.nope.modules.animations.AbstractAnimation;

/**
 * Called when an animation ends, whether because the player left early or if it
 * ended naturally.
 * 
 * @author imodm
 *
 */
public class AnimationEndEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private AbstractAnimation animation;

	private Player player;

	public AnimationEndEvent(Player player, AbstractAnimation animation) {
		this.player = player;
		this.animation = animation;
	}

	public Player getPlayer() {
		return player;
	}

	public AbstractAnimation getAnimation() {
		return animation;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
