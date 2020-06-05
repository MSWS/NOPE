package xyz.msws.anticheat.checks.render;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.anticheat.protocols.WrapperPlayServerRelEntityMove;
import xyz.msws.anticheat.protocols.WrapperPlayServerRelEntityMoveLook;

/**
 * @deprecated Causes some issues with player position syncing
 * @author imodm
 *
 */
public class PlayerESP2 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private PacketAdapter blocker;
	private Map<UUID, Map<Entity, Location>> lastKnown = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		blocker = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				Player player = event.getPlayer();
				WrapperPlayServerRelEntityMove packet = new WrapperPlayServerRelEntityMove(event.getPacket());
				Entity ent = packet.getEntity(event);
				if (ent == null)
					return;
				if (canSee(player.getEyeLocation(), ent.getLocation())
						|| canSee(player.getEyeLocation(), ent.getLocation().add(0, 2, 0))) {
					set(player.getUniqueId(), ent, ent.getLocation());
					return;
				}

				event.setCancelled(true);
			}
		};
		manager.addPacketListener(blocker);
		blocker = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				Player player = event.getPlayer();
				WrapperPlayServerRelEntityMoveLook packet = new WrapperPlayServerRelEntityMoveLook(event.getPacket());
				Entity ent = packet.getEntity(event);
				if (ent == null)
					return;
				if (canSee(player.getEyeLocation(), ent.getLocation())
						|| canSee(player.getEyeLocation(), ent.getLocation().add(0, 2, 0))) {
					set(player.getUniqueId(), ent, ent.getLocation());
					return;
				}
				event.setCancelled(true);
			}
		};

		blocker = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				Player player = event.getPlayer();
				WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event.getPacket());
				Entity ent = packet.getEntity(event);
				if (ent == null)
					return;
				if (canSee(player.getEyeLocation(), ent.getLocation())
						|| canSee(player.getEyeLocation(), ent.getLocation().add(0, 2, 0))) {
					return;
				}
				event.setCancelled(true);
			}
		};

		manager.addPacketListener(blocker);
	}

	private void set(UUID key, Entity ent, Location value) {
		Map<Entity, Location> map = lastKnown.getOrDefault(key, new HashMap<>());
		map.put(ent, value);
		lastKnown.put(key, map);
	}

	private boolean canSee(Location source, Location target) {
		RayTraceResult result = source.getWorld().rayTraceBlocks(source, target.toVector().subtract(source.toVector()),
				100, FluidCollisionMode.NEVER, true);
		if (result == null || result.getHitBlock() == null)
			return false;

		Block hit = result.getHitBlock();
		if (!hit.getType().isOccluding())
			return true;

		return (result.getHitPosition().toLocation(source.getWorld()).distanceSquared(source) >= source
				.distanceSquared(target));
	}

	@Override
	public void disable() {
	}

	@Override
	public String getCategory() {
		return "PlayerESP";
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
