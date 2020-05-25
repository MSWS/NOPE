package xyz.msws.anticheat.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.protocols.WrapperPlayServerRemoveEntityEffect;

public class ZootProtocol {
	@SuppressWarnings("unused")
	private NOPE plugin;

	private Map<UUID, Long> lastStatus = new HashMap<>();

	public ZootProtocol(NOPE plugin) throws OperationNotSupportedException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL,
				PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				Player player = event.getPlayer();
				WrapperPlayServerRemoveEntityEffect packet = new WrapperPlayServerRemoveEntityEffect(event.getPacket());
				if (player.getEntityId() != packet.getEntityID())
					return;
				lastStatus.put(player.getUniqueId(), System.currentTimeMillis());
			}
		};
		manager.addPacketListener(adapter);
	}

	public long getLastStatus(UUID uuid) {
		return lastStatus.getOrDefault(uuid, 0L);
	}

}
