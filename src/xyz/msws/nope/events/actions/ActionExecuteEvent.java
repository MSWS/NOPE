package xyz.msws.nope.events.actions;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.checks.Check;

public class ActionExecuteEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private AbstractAction action;
	private OfflinePlayer player;
	private Check check;

	private boolean cancel = false;

	public ActionExecuteEvent(AbstractAction action, OfflinePlayer player, Check check) {
		this.action = action;
		this.player = player;
	}

	public AbstractAction getAction() {
		return action;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public Check getCheck() {
		return check;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
