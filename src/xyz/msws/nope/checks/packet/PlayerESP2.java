package xyz.msws.nope.checks.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.nope.protocols.WrapperPlayServerRelEntityMove;
import xyz.msws.nope.protocols.WrapperPlayServerRelEntityMoveLook;

/**
 * Hides entities that are too far away and that don't have line of sight
 * 
 * @author imodm
 *
 */
public class PlayerESP2 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PACKET;
	}

	private PacketAdapter blocker;
	private Map<UUID, Map<Integer, Boolean>> visible = new HashMap<>();

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
		if (ent instanceof ArmorStand) {
			if (!((ArmorStand) ent).isVisible()) {
				set(player, ent, false);
				return;
			}
		}
		if (canSee(player.getEyeLocation(), ent.getLocation())) {
			set(player, ent, true);
			return;
		}
		if (ent instanceof LivingEntity) {
			if (((LivingEntity) ent).hasPotionEffect(PotionEffectType.INVISIBILITY))
				return;
			if (ent instanceof Player)
				if (((Player) ent).getGameMode() == GameMode.SPECTATOR)
					return;

			if (canSee(player.getEyeLocation(), ((LivingEntity) ent).getEyeLocation())) {
				set(player, ent, true);
				return;
			}
		}
		set(player, ent, false);
	}

	private void set(Player key, Entity ent, boolean cansee) {
		if (visible.getOrDefault(key.getUniqueId(), new HashMap<>()).getOrDefault(ent.getEntityId(), true) == cansee)
			return;

		Map<Integer, Boolean> vals = visible.getOrDefault(key.getUniqueId(), new HashMap<>());
		vals.put(ent.getEntityId(), cansee);
		visible.put(key.getUniqueId(), vals);

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
		return getCategory() + "#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
