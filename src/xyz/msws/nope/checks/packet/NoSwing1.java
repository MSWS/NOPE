package xyz.msws.nope.checks.packet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Sets;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks if a player hasn't sent a swing packet before interaction event
 * 
 * @author imodm
 *
 */
public class NoSwing1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.PACKET;
	}

	private Map<UUID, Long> swung = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;

		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL,
				PacketType.Play.Client.ARM_ANIMATION) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				swung.put(player.getUniqueId(), System.currentTimeMillis());
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private EnumSet<Material> odd = Sets.newEnumSet(Sets.newHashSet(Material.COMMAND_BLOCK, Material.STRUCTURE_BLOCK,
			Material.LILY_PAD, Material.LADDER, Material.VINE), Material.class);
	private EnumSet<Material> oddBlocks = Sets.newEnumSet(
			Sets.newHashSet(Material.LADDER, Material.VINE, Material.REDSTONE_WIRE, Material.LILY_PAD), Material.class);

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.getGameMode() == GameMode.ADVENTURE)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.isBlockInHand())
			return;

		if (event.getAction() == Action.PHYSICAL)
			return;

		if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
			ItemStack item = event.getItem();
			if (odd.contains(item.getType()))
				return;
		}

		Block clicked = event.getClickedBlock();

		if (clicked != null) {
			if (oddBlocks.contains(clicked.getType()))
				return;
			if (clicked.getType().isInteractable() && !player.isSneaking()) // Signs
				return;
			Material next = clicked.getRelative(event.getBlockFace()).getType();
			if (next.isInteractable())
				return;
			if (next == Material.REDSTONE_WIRE)
				return;
			// BlockCanBuild is not called if a button is on the blockface
			if (clicked.getType() == Material.SCAFFOLDING)
				return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - swung.getOrDefault(player.getUniqueId(), 0L) < 400)
					return;

				cp.flagHack(NoSwing1.this, 10, "LastSwing: &e"
						+ (System.currentTimeMillis() - swung.getOrDefault(player.getUniqueId(), 0L)) + "&7 >= &a400");
			}
		}.runTaskLater(plugin, 1);
	}

	@EventHandler
	public void onPlayerCanBuild(BlockCanBuildEvent event) {
		Player player = event.getPlayer();
		if (event.isBuildable())
			return;
		swung.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@Override
	public String getCategory() {
		return "NoSwing";
	}

	@Override
	public String getDebugName() {
		return "NoSwing#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
