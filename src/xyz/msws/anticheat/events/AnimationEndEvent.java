package xyz.msws.anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.anticheat.modules.animations.AbstractAnimation;

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
