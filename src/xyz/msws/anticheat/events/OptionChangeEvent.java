package xyz.msws.anticheat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.anticheat.modules.data.Option;

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
