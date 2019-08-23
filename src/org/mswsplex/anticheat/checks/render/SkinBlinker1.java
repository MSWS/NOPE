package org.mswsplex.anticheat.checks.render;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks how often a player sends a settings packet
 * 
 * @author imodm
 *
 */
public class SkinBlinker1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@Override
	public void register(NOPE plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (cp.timeSince("lastMove") > 500 || cp.timeSince("lastOnGround") > 500
							|| cp.timeSince("lastSettingsPacket") > 200)
						return;

					int packets = cp.getTempInteger("settingsPackets");
					cp.setTempData("settingsPackets", 0);

					if (packets <= 20)
						return;
					cp.flagHack(SkinBlinker1.this, (packets - 8) * 10, "Packets: &e" + packets + ">&a20");
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}

	@Override
	public String getCategory() {
		return "SkinBlinker";
	}

	@Override
	public String getDebugName() {
		return "SkinBlinker#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}
}
