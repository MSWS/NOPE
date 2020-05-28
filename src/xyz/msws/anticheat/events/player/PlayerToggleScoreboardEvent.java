package xyz.msws.anticheat.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerToggleScoreboardEvent extends PlayerEvent {

	private final static HandlerList handlers = new HandlerList();

	private boolean to;

	public PlayerToggleScoreboardEvent(Player who, boolean to) {
		super(who);
		this.to = to;
	}

	public boolean getTo() {
		return to;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
