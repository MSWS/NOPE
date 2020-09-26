package xyz.msws.nope.modules.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

public class Global extends AbstractModule implements Listener {

	public Global(NOPE plugin) {
		super(plugin);

		for (Player player : Bukkit.getOnlinePlayers()) {
			CPlayer cp = plugin.getCPlayer(player);
			cp.setTempData(Stat.JOIN_TIME, System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onRiptid(PlayerRiptideEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData(Stat.RIPTIDE, System.currentTimeMillis());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		long time = System.currentTimeMillis();
		boolean onGround = player.isOnGround(), weirdBlock = cp.isInWeirdBlock(), climbing = cp.isInClimbingBlock();

		cp.setTempData(Stat.MOVE, time);

		Location from = event.getFrom(), to = event.getTo();

		if (plugin.getOption("debug").asBoolean()) {
			MSG.tell(player, " ");
			MSG.tell(player, "&9From &e" + String.format("%.3f, %.3f, %.3f", from.getX(), from.getY(), from.getZ()));
			MSG.tell(player, "&9To &6" + String.format("%.3f, %.3f, %.3f", to.getX(), to.getY(), to.getZ()));
			MSG.tell(player, "&9Diff &b" + String.format("%.3f, %.3f, %.3f", to.getX() - from.getX(),
					to.getY() - from.getY(), to.getZ() - from.getZ()));
			MSG.tell(player,
					String.format("&9In: &e%s &7(Solid: %s&7, Liquid: %s&7)",
							MSG.camelCase(to.getBlock().getType().toString()),
							MSG.TorF(to.getBlock().getType().isSolid()), MSG.TorF(to.getBlock().isLiquid())));
			MSG.tell(player, String.format("&9OnGround&7: %s", MSG.TorF(player.isOnGround())));
		}

		if (cp.isBlockNearby(Material.WATER) || cp.isBlockNearby(Material.LAVA))
			cp.setTempData(Stat.IN_LIQUID, time);

		if (from.getY() != to.getY())
			cp.setTempData(Stat.VERTICAL_CHANGE, time);

		if (onGround) {
			cp.setTempData(Stat.ON_GROUND, time);
			if (!weirdBlock && player.getLocation().subtract(0, .1, 0).getBlock().getType().isSolid()) {
				cp.setLastSafeLocation(player.getLocation());
			}
		} else {
			cp.setTempData(Stat.IN_AIR, time);
		}

		if (cp.isBlockAbove()) {
			if (player.getLocation().clone().add(0, -.5, 0).getBlock().getType().toString().contains("ICE") || player
					.getLocation().clone().subtract(0, .05, 0).getBlock().getType().toString().contains("TRAP")) {
				cp.setTempData(Stat.ICE_TRAPDOOR, time);

			}
		}

		if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().toString().contains("ICE"))
			cp.setTempData(Stat.ON_ICE, time);

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (player.getLocation().clone().add(x, -.1, z).getBlock().getType().isSolid()) {
					cp.setTempData(Stat.FLIGHT_GROUNDED, time);
					break;
				}
				if (player.getLocation().clone().add(x, -1.5, z).getBlock().getType().isSolid()) {
					cp.setTempData(Stat.FLIGHT_GROUNDED, time);
					break;
				}
			}
		}

		if (player.getNearbyEntities(2, 2, 2).stream().anyMatch(e -> e.getType() == EntityType.SHULKER))
			cp.setTempData(Stat.SHULKER, time);

		if (cp.isBlockNearby(Material.LILY_PAD))
			cp.setTempData(Stat.LILY_PAD, time);

		if (cp.isBlockNearby(Material.COBWEB) || cp.isBlockNearby(Material.COBWEB, 1.0)
				|| cp.isBlockNearby(Material.COBWEB, 2.0))
			cp.setTempData(Stat.COBWEB, time);

		if (climbing)
			cp.setTempData(Stat.CLIMBING, time);

		if (weirdBlock)
			cp.setTempData(Stat.IN_WEIRD_BLOCK, time);

		if (player.isInsideVehicle())
			cp.setTempData(Stat.IN_VEHICLE, time);

		if (player.isFlying() || player.isGliding())
			cp.setTempData(Stat.FLYING, time);

		if (player.isSprinting())
			cp.setTempData(Stat.SPRINTING, time);

		if (player.isRiptiding())
			cp.setTempData(Stat.RIPTIDE, time);

		ItemStack boots = player.getInventory().getBoots();
		if (boots != null && boots.getType() != Material.AIR && boots.containsEnchantment(Enchantment.SOUL_SPEED)) {
			if (cp.isBlockNearby(Material.SOUL_SAND, -1))
				cp.setTempData(Stat.SOUL_SPEED, time);
			if (cp.isBlockNearby(Material.SOUL_SOIL, 0, -0.5f))
				cp.setTempData(Stat.SOUL_SPEED, time);
		}

		if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
			cp.setTempData(Stat.HORIZONTAL_BLOCKCHANGE, time);

		Location vertLine = player.getLocation().clone();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0)
			vertLine.subtract(0, 1, 0);

		Block lowestBlock = vertLine.getBlock();

		if (lowestBlock.getType() == Material.SLIME_BLOCK || lowestBlock.getType() == Material.HONEY_BLOCK)
			cp.setTempData(Stat.ON_SLIMEBLOCK, time);
	}

	@EventHandler
	public void onPotion(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		CPlayer cp = plugin.getCPlayer((Player) event.getEntity());
		cp.setTempData(Stat.MOVEMENT_POTION, System.currentTimeMillis());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData(Stat.BLOCK_PLACE, System.currentTimeMillis());
	}

	@EventHandler(ignoreCancelled = true)
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData(Stat.TOGGLE_FLIGHT, System.currentTimeMillis());

		if (player.isFlying()) {
			cp.setTempData(Stat.DISABLE_FLIGHT, System.currentTimeMillis());
		} else {
			cp.setTempData(Stat.ENABLE_FLIGHT, System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onToggleGlide(EntityToggleGlideEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = ((Player) event.getEntity());
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData(Stat.TOGGLE_GLIDE, System.currentTimeMillis());

		if (player.isGliding()) {
			cp.setTempData(Stat.DISABLE_GLIDE, System.currentTimeMillis());
		} else {
			cp.setTempData(Stat.ENABLE_GLIDE, System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onVehicleLeave(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player))
			return;
		Player player = ((Player) event.getExited());
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData(Stat.LEAVE_VEHICLE, System.currentTimeMillis());

	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData(Stat.TELEPORT, System.currentTimeMillis());
	}

	@EventHandler
	public void onPush(BlockPistonExtendEvent event) {
		List<Block> affected = new ArrayList<>(event.getBlocks()); // Clone to make it mutable
		affected.add(event.getBlock());

		for (Block b : affected) {
			for (Player p : b.getLocation().getWorld().getNearbyEntities(b.getLocation(), 2, 2, 2).parallelStream()
					.filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList())) {
				CPlayer cp = plugin.getCPlayer(p);
				cp.setTempData(Stat.REDSTONE, System.currentTimeMillis());
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof Player))
			return;
		Player player = (Player) ent;
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData(Stat.DAMAGE_TAKEN, System.currentTimeMillis());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData(Stat.RESPAWN, System.currentTimeMillis());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setLastSafeLocation(player.getLocation());
		cp.setTempData(Stat.JOIN_TIME, System.currentTimeMillis());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData(Stat.INVENTORY_CLICK, System.currentTimeMillis());

	}

	public enum Stat {
		/**
		 * Last time damage was taken
		 */
		DAMAGE_TAKEN,
		/**
		 * Time since the player joined the server
		 */
		JOIN_TIME,
		/**
		 * Last time the player left their vehicle
		 */
		LEAVE_VEHICLE,
		/**
		 * Last time the player teleported
		 */
		TELEPORT,
		/**
		 * Last time the player toggled gliding
		 */
		TOGGLE_GLIDE,
		/**
		 * Last time the player disabled gliding
		 */
		DISABLE_GLIDE,
		/**
		 * Last time the player enabled gliding
		 */
		ENABLE_GLIDE,
		/**
		 * Last time the player placed a block
		 */
		BLOCK_PLACE,
		/**
		 * Last time the player was affected by redstone/pistons
		 */
		REDSTONE,
		/**
		 * Last time the player was on a slimeblock
		 */
		ON_SLIMEBLOCK,
		/**
		 * Last time the player's block was changed horizontally
		 */
		HORIZONTAL_BLOCKCHANGE,
		/**
		 * Last time the player was sprinting
		 */
		SPRINTING,
		/**
		 * Last time the player was flying or gliding
		 */
		FLYING,
		/**
		 * Last time the player was in a vehicle
		 */
		IN_VEHICLE,
		/**
		 * Last time the player had any block nearby
		 */
		FLIGHT_GROUNDED,
		/**
		 * Last time the player was on ice
		 */
		ON_ICE,
		/**
		 * Last time the player had ice/trapdoor combo
		 */
		ICE_TRAPDOOR,
		/**
		 * Last time the player's Y coordinate changed
		 */
		VERTICAL_CHANGE,
		/**
		 * Last time the player was surrounded by air
		 */
		IN_AIR,
		/**
		 * Last time the player was in liquid
		 */
		IN_LIQUID,
		/**
		 * Last time the player was on the ground ({@link Player#isOnGround()}
		 */
		ON_GROUND,
		/**
		 * Last time the player moved
		 */
		MOVE,
		/**
		 * Last time the player failed a check
		 */
		FLAGGED,
		/**
		 * Last time the player was in a climbing block
		 */
		CLIMBING,
		/**
		 * Last time the player was in a weird block {@link CPlayer#isInWeirdBlock()}
		 */
		IN_WEIRD_BLOCK,
		/**
		 * Last time the player toggled flying
		 */
		TOGGLE_FLIGHT,
		/**
		 * Last time the player disabled flight
		 */
		DISABLE_FLIGHT,
		/**
		 * Last time the player enabled flight
		 */
		ENABLE_FLIGHT,
		/**
		 * Last time the player clicked in an inventory
		 */
		INVENTORY_CLICK,
		/**
		 * Last time the player opened their inventory
		 */
		OPEN_INVENTORY,
		/**
		 * Last time the player respawned
		 */
		RESPAWN,
		/**
		 * Last time the player was near a lily pad
		 */
		LILY_PAD,
		/**
		 * Last time the player was in cobweb
		 */
		COBWEB,
		/**
		 * Last time the player activated riptide
		 */
		RIPTIDE,
		/**
		 * Last time the player was on top of a shulker
		 */
		SHULKER,
		/**
		 * Last time the player was sped up by the SOUL_SPEED enchantment
		 */
		SOUL_SPEED,
		/**
		 * Last time the player had a movement related potion
		 */
		MOVEMENT_POTION;
	}

	@Override
	public void enable() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {

	}
}
