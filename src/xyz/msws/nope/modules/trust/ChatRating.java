package xyz.msws.nope.modules.trust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatRating implements TrustRating, Listener {

	public ChatRating(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Set<String> words = new HashSet<>(
			Arrays.asList("hack", "watch this", "scripts", "skripts", "bypass", "ez", "noob", "not", "speed", "ka"));

	private Map<UUID, List<Long>> times = new HashMap<>();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage().toLowerCase();
		int triggers = 0;
		for (String word : words)
			if (msg.contains(word))
				triggers++;
		if (triggers == 0)
			return;
		List<Long> t = times.getOrDefault(player.getUniqueId(), new ArrayList<>());
		t.add(System.currentTimeMillis());
		times.put(player.getUniqueId(), t);
	}

	@Override
	public double getTrust(UUID uuid) {
		List<Long> t = times.getOrDefault(uuid, new ArrayList<>());
		Iterator<Long> it = t.iterator();
		while (it.hasNext()) {
			long v = it.next();
			if (System.currentTimeMillis() - v > TimeUnit.MINUTES.toMillis(30))
				it.remove();
		}

		return 1 - Math.min(t.size() / 10.0, 1);
	}

	@Override
	public float getWeight() {
		return .05f;
	}

}
