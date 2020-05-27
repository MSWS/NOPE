package xyz.msws.anticheat.checks.render;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.protocols.WrapperPlayClientSettings;

public class SkinBlinker1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@SuppressWarnings("unused")
	private NOPE plugin;

	private Map<UUID, Integer> skinValue = new HashMap<>();
	private Map<UUID, Long> skinPacket = new HashMap<>();
	private Map<UUID, Integer> packetAmo = new HashMap<UUID, Integer>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				WrapperPlayClientSettings wrapped = new WrapperPlayClientSettings(packet);

				int lastSkin = skinValue.getOrDefault(player.getUniqueId(), 0);
				if (lastSkin == wrapped.getDisplayedSkinParts())
					return;
//				cp.setTempData("lastSkinValue", wrapped.getDisplayedSkinParts());
				skinValue.put(player.getUniqueId(), wrapped.getDisplayedSkinParts());
//				cp.setTempData("lastSettingsPacket", (double) System.currentTimeMillis());
				skinPacket.put(player.getUniqueId(), System.currentTimeMillis());
//				cp.setTempData("settingsPackets", cp.getTempInteger("settingsPackets") + 1);
				packetAmo.put(player.getUniqueId(), packetAmo.getOrDefault(player.getUniqueId(), 0) + 1);
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (cp.timeSince(Stat.MOVE) > 500 || cp.timeSince(Stat.ON_GROUND) > 500
							|| System.currentTimeMillis() - skinPacket.getOrDefault(player.getUniqueId(), 0L) > 200)
						return;

					int packets = packetAmo.getOrDefault(player.getUniqueId(), 0);
//					cp.setTempData("settingsPackets", 0);
					packetAmo.put(player.getUniqueId(), 0);

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
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
