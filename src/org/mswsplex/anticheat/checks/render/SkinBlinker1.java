package org.mswsplex.anticheat.checks.render;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

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
	public void register(AntiCheat plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					int packets = cp.getTempInteger("settingsPackets");
					if (packets <= 20)
						return;
					MSG.tell(player, "&epackets: " + packets);
					cp.setTempData("settingsPackets", 0);
					cp.flagHack(SkinBlinker1.this, (packets - 8) * 10);
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
}
