package xyz.msws.anticheat.modules.animations;

import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;

/**
 * 
 * @author imodm
 *
 */
public abstract class AbstractAnimation {

	protected NOPE plugin;
	protected Player player;
	protected long startTime;

	public AbstractAnimation(NOPE plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	public boolean start() {
		this.startTime = System.currentTimeMillis();
		return true;
	}

	public abstract void stop(boolean manual);

	public abstract boolean completed();

	public abstract String getName();
}
