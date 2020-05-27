package xyz.msws.anticheat.modules.bans;

import java.util.UUID;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public abstract class BanHook extends AbstractModule {
	public BanHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	public void ban(UUID player) {
		ban(player, null);
	}

	public void ban(UUID player, String reason) {
		ban(player, reason, -1);
	}

	public abstract void ban(UUID player, String reason, long time);

}
