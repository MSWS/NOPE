package xyz.msws.nope.events.player;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.nope.modules.reports.Report;

/**
 * Called when a player flags a check
 * 
 * @author imodm
 *
 */
public class PlayerReportEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private Report report;

	private boolean cancel = false;

	public PlayerReportEvent(Report report) {
		this.report = report;
	}

	public Report getReport() {
		return report;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
