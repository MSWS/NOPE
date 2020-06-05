package xyz.msws.nope.checks.combat;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.protocols.WrapperPlayClientUseEntity;

/**
 * Checks to see if the entity that was hit is behind a wall/block
 * 
 * @author imodm
 *
 */
public class KillAura4 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
				Player player = event.getPlayer();

				if (packet.getType() != EntityUseAction.ATTACK)
					return;

				Entity ent = packet.getTarget(player.getWorld());
				CPlayer cp = KillAura4.this.plugin.getCPlayer(player);

				if (ent == null) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						cp.flagHack(KillAura4.this, 30, "Hit invalid entity");
					});
					return;
				}

				RayTraceResult result = player.rayTraceBlocks(20);

				if (result == null || result.getHitBlock() == null)
					return;

				Location pos = result.getHitPosition().toLocation(player.getWorld());

				double bLength = pos.distanceSquared(player.getLocation());
				double eLength = player.getLocation().distanceSquared(ent.getLocation());

				if (bLength > eLength)
					return;

				Bukkit.getScheduler().runTask(plugin, () -> {

					cp.flagHack(KillAura4.this, 30,
							String.format("Ent Dist: &e%.2f&7\n&7Wall Dist: &a%.2f", eLength, bLength));
				});
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#4";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
