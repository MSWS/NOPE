package xyz.msws.nope.modules.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import xyz.msws.nope.protocols.WrapperPlayServerEntityDestroy;
import xyz.msws.nope.protocols.WrapperPlayServerEntityEquipment;
import xyz.msws.nope.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.nope.protocols.WrapperPlayServerEntityTeleport;
import xyz.msws.nope.protocols.WrapperPlayServerNamedEntitySpawn;
import xyz.msws.nope.protocols.WrapperPlayServerPlayerInfo;
import xyz.msws.nope.protocols.WrapperPlayServerRelEntityMoveLook;
import xyz.msws.nope.utils.Utils;

/**
 * Easy to use object that handles the packets internally, NPCs should ideally
 * only be sent to one player, otherwise their locations may be confusing.
 * 
 * @author imodm
 *
 */
public class NPC {
	private Location loc;
	private int id;
	private boolean onGround;
	private double health;
	private int ping;
	private NativeGameMode gamemode;
	private WrappedGameProfile profile;
	private EnumMap<ItemSlot, ItemStack> contents;
	private boolean invisible;
	private boolean sneaking;

	/**
	 * This <b>does not</b> send the spawn packet, it merely initializes the fields
	 * 
	 * @param loc
	 */
	public NPC(Location loc) {
		this.loc = loc;
		this.health = 20;
		this.onGround = false;
		this.ping = 0;
		this.gamemode = NativeGameMode.SURVIVAL;
		this.contents = new EnumMap<>(ItemSlot.class);

		this.id = ThreadLocalRandom.current().nextInt(65536);

		this.invisible = false;
		this.sneaking = false;
	}

	/**
	 * @return Server-side location of the NPC
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Set and broadcast the EntityEquipment packet accordingly
	 * 
	 * @param slot The ItemSlot to set
	 * @param item The Item to set the slot to
	 * @return
	 */
	public ItemStack setItem(ItemSlot slot, ItemStack item) {
		ItemStack result = contents.put(slot, item);
		WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
		equipment.setEntityID(id);

		try {
			equipment.setSlot(slot);
			equipment.setItem(item);
		} catch (FieldAccessException expected) {
			// ProtocolLib isn't updated
		}
		equipment.broadcastPacket();
		return result;
	}

	private static Object STANDING = null;

	/**
	 * Sends and spawns PlayerInfo and NamedEntitySpawn. This is limited in that it
	 * will try to spawn an NPC of a player already on the server.
	 * 
	 * The primary use of this is for KillAura.
	 * 
	 * @param player
	 */
	@SuppressWarnings("deprecation") // Client sided
	public void spawn(Player player) {
		List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
		online.remove(player);

		Player fake = online.isEmpty() ? player : online.get(ThreadLocalRandom.current().nextInt(online.size()));

		this.onGround = fake.isOnGround();
		this.health = fake.getHealth();

		UUID uuid = fake.getUniqueId();
		WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
		info.setAction(PlayerInfoAction.ADD_PLAYER);
		profile = new WrappedGameProfile(uuid, fake.getName());
		WrappedChatComponent comp = WrappedChatComponent.fromText(fake.getName());
		PlayerInfoData data = new PlayerInfoData(profile, ping, gamemode, comp);
		info.setData(Arrays.asList(data));

		info.sendPacket(player);

		Location l = player.getLocation().add(0, 5, 0);
		this.loc = l;

		WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn();
		packet.setEntityID(id);
		packet.setPlayerUUID(uuid);
		packet.setPosition(l.toVector());
		packet.sendPacket(player);

		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(id);
		List<WrappedWatchableObject> metadata = meta.getMetadata();

		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata);

		Map<Integer, Object> values = new HashMap<>();

		values.put(0, (byte) 0);

		if (STANDING == null) {
			try {
				Class<?> pose = Class.forName("net.minecraft.server." + Utils.nms + ".EntityPose");
				STANDING = pose.getEnumConstants()[0];
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		values.put(6, STANDING);
		values.put(4, false);
		values.put(15, 10);
		values.put(14, 0.0f);
		values.put(12, 0);
		values.put(8, (float) health);
		values.put(9, 0);
		values.put(11, 0);
		values.put(10, false);
		values.put(1, 300);
		values.put(3, false);
		values.put(7, 0);
		values.put(5, false);
		values.put(16, (byte) 1);
		values.put(17, (byte) 1);

		for (Entry<Integer, Object> entry : values.entrySet()) {
			WrappedDataWatcherObject object = new WrappedDataWatcherObject(entry.getKey(),
					WrappedDataWatcher.Registry.get(entry.getValue().getClass()));
			dataWatcher.setObject(object, entry.getValue());
		}
		meta.setMetadata(dataWatcher.getWatchableObjects());
//		meta.broadcastPacket();
	}

	/**
	 * Moves or teleports the NPC according to the guidelines.
	 * 
	 * {@link https://wiki.vg/Protocol}
	 * 
	 * @param loc
	 */
	public void moveOrTeleport(Location loc) {
		if (!this.loc.getWorld().equals(loc.getWorld()))
			throw new IllegalStateException("Attempted to teleport NPC cross-world.");

		if (loc.distanceSquared(this.loc) > 64) {
			// Teleport
			WrapperPlayServerEntityTeleport tp = new WrapperPlayServerEntityTeleport();
			tp.setEntityID(id);
			tp.setX(loc.getX());
			tp.setY(loc.getY());
			tp.setZ(loc.getZ());
			tp.setPitch(loc.getPitch());
			tp.setYaw(loc.getYaw());
			tp.setOnGround(onGround);

			tp.broadcastPacket();
		} else {
			// Move
			WrapperPlayServerRelEntityMoveLook move = new WrapperPlayServerRelEntityMoveLook();
			move.setEntityID(id);

			double offX = loc.getX() - this.loc.getX();
			double offY = loc.getY() - this.loc.getY();
			double offZ = loc.getZ() - this.loc.getZ();
			move.setDx(offX);
			move.setDy(offY);
			move.setDz(offZ);
			move.setPitch(loc.getPitch());
			move.setYaw(loc.getYaw());
			move.setOnGround(onGround);

			move.broadcastPacket();
		}
		this.loc = loc;
	}

	/**
	 * @return The NPC's health
	 */
	public double getHealth() {
		return health;
	}

	/**
	 * This doesn't necessarily reflect whether or not the <b>location</b> is on the
	 * ground, simply whether or not the NPC is marked as onGround
	 * 
	 * @return Whether or not the NPC is on the ground
	 */
	public boolean onGround() {
		return onGround;
	}

	/**
	 * Sets the flag for this entity to be on the ground. This doesn't send any
	 * packets, when this entity moves again ({@link #moveOrTeleport(Location)})
	 * this is used.
	 * 
	 * @param grounded
	 */
	public void setOnGround(boolean grounded) {
		this.onGround = grounded;
	}

	/**
	 * https://www.spigotmc.org/threads/spawn-entity-using-protocollib-wrappers.370803/
	 * 
	 * @param health
	 */
	public void setHealth(double health) {
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(id);
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(meta.getMetadata());
		WrappedDataWatcherObject object = new WrappedDataWatcherObject(8, WrappedDataWatcher.Registry.get(Float.class));
		dataWatcher.setObject(object, (float) health);

		meta.setMetadata(dataWatcher.getWatchableObjects());

		meta.broadcastPacket();
	}

	/**
	 * Sets whether or not the NPC is visible.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.invisible = !visible;
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(id);
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(meta.getMetadata());
		WrappedDataWatcherObject object = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
		byte value = (byte) ((visible ? 0x0 : 0x20) | (sneaking ? 0x0 : 0x02));
		dataWatcher.setObject(object, value);
		meta.setMetadata(dataWatcher.getWatchableObjects());
		meta.broadcastPacket();
	}

	/**
	 * Sets whether or not the NPC is sneaking.
	 * 
	 * @param sneaking
	 */
	public void setSneaking(boolean sneaking) {
		this.sneaking = sneaking;
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(id);
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(meta.getMetadata());
		WrappedDataWatcherObject object = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
		byte value = (byte) ((!invisible ? 0x0 : 0x20) | (sneaking ? 0x0 : 0x02));
		dataWatcher.setObject(object, value);
		meta.setMetadata(dataWatcher.getWatchableObjects());
		meta.broadcastPacket();
	}

	/**
	 * @return If the NPC is visible.
	 */
	public boolean getVisible() {
		return !this.invisible;
	}

	/**
	 * @return If the NPC is sneaking.
	 */
	public boolean isSneaking() {
		return sneaking;
	}

	/**
	 * @return The NPC's ping.
	 */
	public int getPing() {
		return ping;
	}

	/**
	 * Broadcasts and sets the NPC's ping
	 * 
	 * @param ping
	 */
	public void setPing(int ping) {
		WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
		PlayerInfoData data = new PlayerInfoData(profile, ping, gamemode,
				WrappedChatComponent.fromText(profile.getName()));

		packet.setAction(PlayerInfoAction.UPDATE_LATENCY);
		packet.setData(Arrays.asList(data));
		packet.broadcastPacket();
	}

	/**
	 * This is ProtocolWrapper's version of GameMode
	 * 
	 * @return
	 */
	public NativeGameMode getGamemode() {
		return gamemode;
	}

	/**
	 * Simple conversion method from {@link NativeGameMode} to {@link GameMode}
	 * 
	 * @return
	 */
	public GameMode getBukkitGamemode() {
		switch (gamemode) {
			case ADVENTURE:
				return GameMode.ADVENTURE;
			case CREATIVE:
				return GameMode.CREATIVE;
			case NOT_SET:
			case SPECTATOR:
				return GameMode.SPECTATOR;
			case SURVIVAL:
				return GameMode.SURVIVAL;
			default:
				break;
		}

		return GameMode.SPECTATOR;
	}

	/**
	 * Broadcasts an EntityDestroy packet
	 */
	public void remove() {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] { id });
		destroy.broadcastPacket();
	}

	/**
	 * @return The entity ID
	 */
	public int getEntityID() {
		return id;
	}

}
