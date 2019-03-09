package org.mswsplex.anticheat.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * 
 * Compares a player's previous fire ticks and flags if they've dropped too
 * suddenly this WILL flag commands such as /heal
 * 
 * @author imodm
 * 
 */
public class AntiFire1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private final int RATE = 20;

	@Override
	public void register(AntiCheat plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
						continue;
					CPlayer cp = plugin.getCPlayer(player);

					int oldFireTicks = cp.getTempInteger("oldFireTicks");

					cp.setTempData("oldFireTicks", player.getFireTicks());

					if (oldFireTicks - player.getFireTicks() <= RATE + 10)
						continue;

					if (cp.timeSince("lastLiquid") < 500)
						continue;

					cp.flagHack(AntiFire1.this,
							(int) Math.round((oldFireTicks - player.getFireTicks() - (RATE + 10)) / 20));
				}
			}
		}.runTaskTimer(plugin, 0, RATE);
	}

	@Override
	public String getCategory() {
		return "AntiFire";
	}

	@Override
	public String getDebugName() {
		return "AntiFire#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	@Override
	public boolean onlyLegacy() {
		return false;
	}
}
