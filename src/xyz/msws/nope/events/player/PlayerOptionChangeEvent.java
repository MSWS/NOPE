package xyz.msws.nope.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import xyz.msws.nope.modules.data.PlayerOption;

/**
 * @author imodm
 *
 */
public class PlayerOptionChangeEvent extends PlayerEvent {

	private final static HandlerList handlers = new HandlerList();

	private PlayerOption option;

	public PlayerOptionChangeEvent(Player who, PlayerOption option) {
		super(who);
		this.option = option;
	}

	public PlayerOption getOption() {
		return option;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
