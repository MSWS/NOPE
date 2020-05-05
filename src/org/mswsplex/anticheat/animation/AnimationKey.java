package org.mswsplex.anticheat.animation;

import org.bukkit.entity.Player;
import org.mswsplex.anticheat.checks.Timing;

/**
 * 
 * @author imodm
 *
 * @deprecated See {@link Animation}
 */
public class AnimationKey {
	private Player player;
	private Timing timing;
	private long startTime;
	private String check, token;
	private boolean doCommands;

	public AnimationKey(Player player, Timing timing, String check, String token, boolean commands) {
		this.player = player;
		this.timing = timing;
		this.check = check;
		this.token = token;
		this.startTime = System.currentTimeMillis();
		this.doCommands = commands;
	}

	public Player getPlayer() {
		return player;
	}

	public Timing getTiming() {
		return timing;
	}

	public String getCheck() {
		return check;
	}

	public String getToken() {
		return token;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public boolean doCommands() {
		return this.doCommands;
	}
}
