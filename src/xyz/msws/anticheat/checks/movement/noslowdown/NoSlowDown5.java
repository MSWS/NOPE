package xyz.msws.anticheat.checks.movement.noslowdown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

public class NoSlowDown5 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, Long> start = new HashMap<>();

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				start.remove(player.getUniqueId());
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item == null)
			return;
		if (!isFood(item.getType()))
			return;

		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (player.getFoodLevel() >= 20 && item.getType() != Material.GOLDEN_APPLE
				&& item.getType() != Material.ENCHANTED_GOLDEN_APPLE)
			return;

		start.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
//		cp.removeTempData("foodStartTime");
		start.remove(player.getUniqueId());
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
//		cp.removeTempData("foodStartTime");
		start.remove(player.getUniqueId());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Location to = event.getTo(), from = event.getFrom();

		if (!start.containsKey(player.getUniqueId()))
			return;

		if (player.isFlying() || player.isGliding())
			return;

		if (cp.timeSince(Stat.FLYING) < 300)
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		if (dist < .47)
			return;

		cp.flagHack(this, (int) Math.round((dist - .43) * 400.0), "Dist: &e" + dist + "&7 >= &a.47");
	}

	private boolean isFood(Material mat) {
		if (mat == null || mat == Material.AIR)
			return false;
		for (String res : new String[] { "BEEF", "CHICKEN", "RABBIT", "POTATO", "COOKED", "SOUP", "FISH", "APPLE" }) {
			if (mat.toString().contains(res))
				return true;
		}
		switch (mat) {
			case ROTTEN_FLESH:
			case BEETROOT:
			case PUMPKIN_PIE:
			case PUFFERFISH:
			case CHORUS_FRUIT:
				return true;
			default:
				return false;
		}

	}

	@Override
	public String getCategory() {
		return "NoSlowDown";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#5";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

}
