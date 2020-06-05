package xyz.msws.nope.modules.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
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
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import xyz.msws.nope.protocols.WrapperPlayServerEntityDestroy;
import xyz.msws.nope.protocols.WrapperPlayServerEntityEquipment;
import xyz.msws.nope.protocols.WrapperPlayServerEntityMetadata;
import xyz.msws.nope.protocols.WrapperPlayServerEntityTeleport;
import xyz.msws.nope.protocols.WrapperPlayServerNamedEntitySpawn;
import xyz.msws.nope.protocols.WrapperPlayServerPlayerInfo;
import xyz.msws.nope.protocols.WrapperPlayServerRelEntityMoveLook;

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

	public Location getLocation() {
		return loc;
	}

	public ItemStack setItem(ItemSlot slot, ItemStack item) {
		ItemStack result = contents.put(slot, item);
		WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
		equipment.setEntityID(id);
		equipment.setSlot(slot);
		equipment.setItem(item);
		equipment.broadcastPacket();
		return result;
	}

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

		WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn();
		packet.setEntityID(id);
		packet.setPlayerUUID(uuid);
		packet.setPosition(l.toVector());
		packet.sendPacket(player);
		this.loc = l;
	}

	public void moveOrTeleport(Location loc) {
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

	public double getHealth() {
		return health;
	}

	public boolean onGround() {
		return onGround;
	}

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

	public boolean getVisible() {
		return !this.invisible;
	}

	public boolean isSneaking() {
		return sneaking;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
		PlayerInfoData data = new PlayerInfoData(profile, ping, gamemode,
				WrappedChatComponent.fromText(profile.getName()));

		packet.setAction(PlayerInfoAction.UPDATE_LATENCY);
		packet.setData(Arrays.asList(data));
		packet.broadcastPacket();
	}

	public NativeGameMode getGamemode() {
		return gamemode;
	}

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

	public void remove() {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] { id });
		destroy.broadcastPacket();
	}

	public int getEntityID() {
		return id;
	}

}
