package xyz.msws.nope.checks.combat.killaura;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.npc.NPC;
import xyz.msws.nope.modules.npc.NPCModule;
import xyz.msws.nope.protocols.WrapperPlayClientUseEntity;

/**
 * Yes this uses NPCs
 * 
 * @author imodm
 *
 */
public class KillAura3 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private NOPE plugin;
	private NPCModule npcs;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;
		this.npcs = plugin.getModule(NPCModule.class);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());

				Player player = event.getPlayer();

				if (packet.getType() != EntityUseAction.ATTACK)
					return;
				if (!npcs.hasNPC(player.getUniqueId()))
					return;
				NPC npc = npcs.getNPC(player.getUniqueId());

				if (npc.getEntityID() != packet.getTargetID())
					return;

				event.setCancelled(true);

				npcs.removeNPC(player);
				CPlayer cp = KillAura3.this.plugin.getCPlayer(player);
				Bukkit.getScheduler().runTask(plugin, () -> {
					cp.flagHack(KillAura3.this, 20);
				});
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

		new BukkitRunnable() {
			@Override
			public void run() {
				Map<UUID, NPC> list = npcs.getNPCs();

				Iterator<Entry<UUID, NPC>> it = list.entrySet().iterator();
				while (it.hasNext()) {
					Entry<UUID, NPC> entry = it.next();

					if (!Bukkit.getOfflinePlayer(entry.getKey()).isOnline())
						continue;
					Player player = Bukkit.getPlayer(entry.getKey());
					NPC npc = entry.getValue();

					Location loc = player.getLocation().clone();
					loc.setPitch(ThreadLocalRandom.current().nextFloat() * 90);
					Location target = loc.add(loc.getDirection().normalize().multiply(-3));
					target.setDirection(player.getLocation().toVector().subtract(target.toVector()));

					if (!target.getWorld().equals(npc.getLocation().getWorld())) {
						npcs.removeNPC(player);
						npc = npcs.getOrSpawn(player);
						list.put(entry.getKey(), npc);
						return;
					}

					npc.moveOrTeleport(target);
				}

			}
		}.runTaskTimerAsynchronously(plugin, 0, 1);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Map<UUID, BukkitTask> removes = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getTo().getPitch() > -60)
			return;
		if (!npcs.hasNPC(player.getUniqueId()))
			return;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (removes.containsKey(player.getUniqueId()))
					removes.get(player.getUniqueId()).cancel();
				removes.remove(player.getUniqueId());
				npcs.removeNPC(player);
			}
		}.runTaskLater(plugin, 10);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			spawnNPC(player);
		}

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			spawnNPC(player);
		}
	}

	private void spawnNPC(Player player) {
		NPCModule module = plugin.getModule(NPCModule.class);
		if (module == null)
			return;

		NPC npc = module.getOrSpawn(player);
		npc.setHealth(player.getHealth() - 3);
		npc.setPing(ThreadLocalRandom.current().nextInt(90, 150));

		npc.setItem(ItemSlot.MAINHAND, player.getInventory().getItemInMainHand());
		npc.setItem(ItemSlot.CHEST, player.getInventory().getChestplate());
		npc.setOnGround(true);

		if (removes.containsKey(player.getUniqueId())) {
			removes.get(player.getUniqueId()).cancel();
		}

		removes.put(player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if (!npcs.hasNPC(player.getUniqueId()))
					return;
				npcs.removeNPC(player);
				removes.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, 20 * 5));
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#3";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
