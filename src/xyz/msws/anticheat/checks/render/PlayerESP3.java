package xyz.msws.anticheat.checks.render;

import javax.naming.OperationNotSupportedException;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.RayTraceResult;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.anticheat.protocols.WrapperPlayServerRelEntityMove;
import xyz.msws.anticheat.protocols.WrapperPlayServerRelEntityMoveLook;

/**
 * Hides entities that are too far away and that don't have line of sight
 * 
 * @author imodm
 *
 */
public class PlayerESP3 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private PacketAdapter blocker;

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
				runCheck(player, ent);
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
				runCheck(player, ent);
			}
		};
		manager.addPacketListener(blocker);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo().getBlock().equals(event.getFrom().getBlock()))
			return;
		Player player = event.getPlayer();
		for (Entity ent : player.getNearbyEntities(10, 10, 10)) {
			if (player.equals(ent))
				continue;
			runCheck(player, ent);
		}
	}

	private void runCheck(Player player, Entity ent) {
		if (canSee(player.getEyeLocation(), ent.getLocation())) {
			set(player, ent, true);
			return;
		}
		if (ent instanceof LivingEntity) {
			if (canSee(player.getEyeLocation(), ((LivingEntity) ent).getEyeLocation())) {
				set(player, ent, true);
				return;
			}
		}
		set(player, ent, false);
	}

	private void set(Player key, Entity ent, boolean cansee) {
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(ent.getEntityId());

		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(meta.getMetadata());
		WrappedDataWatcherObject object = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
		byte v = (byte) (cansee ? 0x0 : 0x20);
		dataWatcher.setObject(object, v);
		meta.setMetadata(dataWatcher.getWatchableObjects());
		meta.sendPacket(key);
	}

	private boolean canSee(Location source, Location target) {
		if (target.distanceSquared(source) < 256)
			return true;
		RayTraceResult result = source.getWorld().rayTraceBlocks(source, target.toVector().subtract(source.toVector()),
				100, FluidCollisionMode.NEVER, true);
		if (result == null || result.getHitBlock() == null)
			return false;

		Block hit = result.getHitBlock();

		double rayDist = result.getHitPosition().toLocation(source.getWorld()).distanceSquared(source);
		double rawDist = source.distanceSquared(target);

		if (hit.getType().toString().contains("GLASS") || !hit.getType().isSolid()) {
			return true;
		}

		return (rayDist >= rawDist);
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
