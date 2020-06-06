package xyz.msws.nope.events.global;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.nope.modules.data.Option;

/**
 * Called when a Global NOPE Option is changed
 * 
 * @author imodm
 *
 */
public class OptionChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Option option;

	public OptionChangeEvent(Option option) {
		this.option = option;
	}

	public Option getOption() {
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
