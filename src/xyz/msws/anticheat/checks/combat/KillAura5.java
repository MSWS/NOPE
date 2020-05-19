package xyz.msws.anticheat.checks.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

/**
 * 
 * @author imodm
 *
 */
public class KillAura5 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private Map<UUID, Location> directions = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws UnsupportedOperationException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new UnsupportedOperationException("ProtocolLib is not enabled");
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				directions.put(player.getUniqueId(), player.getLocation());
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);
	}

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

//		if (!cp.hasTempData("lastEntityHitDirection"))
//			return;
		if (!directions.containsKey(player.getUniqueId()))
			return;

		Location eLoc = player.getLocation(), packet = directions.get(player.getUniqueId());

		double diff = Math.abs(eLoc.getYaw() - packet.getYaw()) + Math.abs(eLoc.getPitch() - packet.getPitch());

		if (diff == 0)
			return;

		if (plugin.getTPS() < 18)
			return;

		cp.flagHack(this, (int) Math.round(diff * 5) + 20, "Invalid Packet Diff: &e" + diff);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#5";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
