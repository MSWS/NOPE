package xyz.msws.nope.checks.packet;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.nope.utils.MSG;

public class KillAuraBlocker1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.PACKET;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.LOWEST,
				PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event.getPacket());
				List<WrappedWatchableObject> data = packet.getMetadata();
				boolean real = packet.getEntity(event) != null;

				if (!real)
					return;
				if (packet.getEntity(event).getType() != EntityType.PLAYER)
					return;

				for (WrappedWatchableObject obj : data) {
					MSG.announce((real ? "&a" : "&c") + obj.getIndex() + ": " + obj.getRawValue() + " ("
							+ obj.getRawValue().getClass().getName() + ")");
				}
			}
		};
		manager.addPacketListener(adapter);
	}

	@Override
	public String getCategory() {
		return "KillAuraBlocker";
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
