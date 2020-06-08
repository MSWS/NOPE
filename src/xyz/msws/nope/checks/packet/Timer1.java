package xyz.msws.nope.checks.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks how frequent POSITION packets are sent from the client
 * 
 * @author imodm
 *
 */
public class Timer1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.PACKET;
	}

	private NOPE plugin;

	private Map<UUID, List<Long>> times = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;

		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				List<Long> ts = times.getOrDefault(player.getUniqueId(), new ArrayList<>());

				Iterator<Long> it = ts.iterator();
				double avg = 0;
				long last = System.currentTimeMillis();
				while (it.hasNext()) {
					long l = it.next();
					if (System.currentTimeMillis() - l > 10000)
						it.remove();
					else {
						avg += last - l;
						last = l;
					}
				}

				avg /= ts.size();

				ts.add(0, System.currentTimeMillis());
				times.put(player.getUniqueId(), ts);

				if (avg >= 50)
					return;
				final double finalAverage = avg;
				CPlayer cp = Timer1.this.plugin.getCPlayer(player);
				if (cp.timeSince(Stat.JOIN_TIME) < 1000)
					return;
				Bukkit.getScheduler().runTask(Timer1.this.plugin, () -> {
					cp.flagHack(Timer1.this, (int) ((50 - finalAverage) * 5), "Avg: &e" + finalAverage);
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
		return "Timer";
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
