package org.mswsplex.anticheat.checks.tick;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.protocols.TinyProtocol;

import io.netty.channel.Channel;

/**
 * Checks if the player has sent blockplacepackets too quickly
 * 
 * @author imodm
 *
 */
public class MultiUse1 implements Check {
	private AntiCheat plugin;

	private final int MAX_PACKETS = 4, TIMESPAN = 5;

	@Override
	public CheckType getType() {
		return CheckType.TICK;
	}

	@Override
	public void register(AntiCheat plugin) {

		this.plugin = plugin;

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player target : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(target);
					cp.setTempData("blockPlacePackets", 0);
				}
			}
		}.runTaskTimer(plugin, 0, TIMESPAN);

		new TinyProtocol(plugin) {
			@Override
			public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
				if (sender == null)
					return super.onPacketInAsync(sender, channel, packet);
				CPlayer cp = MultiUse1.this.plugin.getCPlayer(sender);
				String name = packet.toString().split("\\.")[packet.toString().split("\\.").length - 1];
				if (name.contains("@"))
					name = name.substring(0, name.indexOf("@"));

				if (!name.equals("PacketPlayInBlockPlace"))
					return super.onPacketInAsync(sender, channel, packet);

				int customPackets = cp.getTempInteger("blockPlacePackets");

				cp.setTempData("blockPlacePackets", customPackets + 1);

				if (customPackets > MAX_PACKETS) {
					Bukkit.getScheduler().runTask(this.plugin, () -> {
						cp.flagHack(MultiUse1.this, (customPackets - MAX_PACKETS) * 5 + 10);
					});
					return null;
				}
				return super.onPacketInAsync(sender, channel, packet);
			}
		};

	}

	@Override
	public String getCategory() {
		return "MultiUse";
	}

	@Override
	public String getDebugName() {
		return "MultiUse#1";
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
