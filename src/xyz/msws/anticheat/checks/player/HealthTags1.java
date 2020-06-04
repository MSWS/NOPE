package xyz.msws.anticheat.checks.player;

import java.util.List;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.protocols.WrapperPlayServerEntityMetadata;

/**
 * This check simply prevents any health-related data being sent
 * 
 * <s>For some reason this also affects the player being damaged</s>
 * 
 * @author imodm
 *
 */
public class HealthTags1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL,
				PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event.getPacket());
//				WrappedDataWatcher dataWatcher = new WrappedDataWatcher(packet.getMetadata());
//				WrappedDataWatcherObject health = new WrappedDataWatcherObject(8,
//						WrappedDataWatcher.Registry.get(Float.class));
				Entity damaged = packet.getEntity(event.getPlayer().getWorld());

				if (damaged == null)
					return;
				if (!(damaged instanceof LivingEntity))
					return;
				if (damaged.equals(event.getPlayer())) {
					return;
				}

				if (((LivingEntity) damaged).getHealth() <= 0)
					return;

				List<WrappedWatchableObject> data = packet.getMetadata();
				data = data.stream().filter(obj -> obj.getIndex() != 8).collect(Collectors.toList());
				packet.setMetadata(data);
			}
		};
		manager.addPacketListener(adapter);
	}

	@Override
	public String getCategory() {
		return "HealthTag";
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
