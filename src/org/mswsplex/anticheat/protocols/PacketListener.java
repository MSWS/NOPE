package org.mswsplex.anticheat.protocols;

import org.bukkit.entity.Player;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.MSG;

import io.netty.channel.Channel;

/**
 * 
 * @author imodm
 *
 * @deprecated
 */
public class PacketListener {

	private NOPE plugin;

	public PacketListener(NOPE plugin) {
		this.plugin = plugin;
		try {
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
							cp.setTempData("lastSettingsPacket", (double) System.currentTimeMillis());
							break;
						case "PacketPlayInArmAnimation":
							cp.setTempData("lastSwing", (double) System.currentTimeMillis());
							break;
						default:
							// MSG.log(name);
						case "PacketPlayInFlying":
						case "PacketPlayInFlying$PacketPlayInPosition":
						case "PacketPlayInKeepAlive":
						case "PacketPlayInFlying$PacketPlayInPositionLook":
						case "PacketPlayInFlying$PacketPlayInLook":
							return super.onPacketInAsync(sender, channel, packet);
					}
					return super.onPacketInAsync(sender, channel, packet);
				}
			};
		} catch (NoClassDefFoundError e) {
			MSG.log("&4[ERROR] There was an error registering the PacketListner, you may be using a custom fork or reloaded the server.");
			e.printStackTrace();
		}
	}

}
