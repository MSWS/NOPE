package xyz.msws.anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.data.CPlayer;

public class PlayerFlagEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private CPlayer player;
	private Check check;

	private boolean cancel = false;

	public PlayerFlagEvent(CPlayer player, Check check) {
		this.player = player;
		this.check = check;
	}

	public CPlayer getCPlayer() {
		return player;
	}

	public Player getPlayer() {
		return player.getPlayer().getPlayer();
	}

	public Check getCheck() {
		return check;
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
