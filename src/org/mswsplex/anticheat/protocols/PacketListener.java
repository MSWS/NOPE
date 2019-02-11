package org.mswsplex.anticheat.protocols;

import org.bukkit.entity.Player;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

import io.netty.channel.Channel;

public class PacketListener {

	private AntiCheat plugin;

	public PacketListener(AntiCheat plugin) {
		this.plugin = plugin;
		new TinyProtocol(plugin) {
			@Override
			public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
				if (sender == null)
					return super.onPacketInAsync(sender, channel, packet);
				CPlayer cp = PacketListener.this.plugin.getCPlayer(sender);
				String name = packet.toString().split("\\.")[packet.toString().split("\\.").length - 1];
				if (name.contains("@"))
					name = name.substring(0, name.indexOf("@"));

				switch (name) {
				case "PacketPlayInUseEntity":
					cp.setTempData("lastEntityHitDirection", sender.getLocation());
					break;
				case "PacketPlayInSettings":
					cp.setTempData("settingsPackets", cp.getTempInteger("settingsPackets") + 1);
					break;
				case "PacketPlayInArmAnimation":
					cp.setTempData("lastSwing", (double) System.currentTimeMillis());
					break;
				default:
					MSG.log(name);
				case "PacketPlayInFlying":
				case "PacketPlayInFlying$PacketPlayInPosition":
				case "PacketPlayInKeepAlive":
				case "PacketPlayInFlying$PacketPlayInPositionLook":
				case "PacketPlayInFlying$PacketPlayInLook":
					return super.onPacketInAsync(sender, channel, packet);
				}
				return super.onPacketInAsync(sender, channel, packet);
			}

			@Override
			public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
				if (receiver == null)
					return super.onPacketInAsync(receiver, channel, packet);
				//CPlayer cp = PacketListener.this.plugin.getCPlayer(receiver);
				String name = packet.toString().split("\\.")[packet.toString().split("\\.").length - 1];
				if (name.contains("@"))
					name = name.substring(0, name.indexOf("@"));

				switch (name) {
				case "":
					break;
				default:
					MSG.log("out: " + name);
				case "PacketPlayOutScoreboardObjective":
				case "PacketPlayOutKeepAlive":
				case "PacketPlayOutExperience":
				case "PacketPlayOutUpdateHealth":
				case "PacketPlayOutBlockAction":
				case "PacketPlayOutUpdateTime":
				case "PacketPlayOutEntity$PacketPlayOutRelEntityMove":
				case "PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook":
				case "PacketPlayOutEntityVelocity":
				case "PacketPlayOutEntityHeadRotation":
				case "PacketPlayOutEntity$PacketPlayOutEntityLook":
					return super.onPacketOutAsync(receiver, channel, packet);
				}

				MSG.log("out: " + name);

				return super.onPacketOutAsync(receiver, channel, packet);
			}
		};
	}

}
