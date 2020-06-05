package xyz.msws.nope.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @deprecated Unused
 * @author imodm
 *
 */
public class DevModeToggleEvent extends Event {
	private final static HandlerList handlers = new HandlerList();

	private boolean to;

	public DevModeToggleEvent(boolean to) {
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
