package xyz.msws.anticheat.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * 
 * Compares a player's previous fire ticks and flags if they've dropped too
 * suddenly this WILL flag commands such as /heal
 * 
 * @author imodm
 * 
 */
public class AntiFire1 implements Check {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, Integer> ticks = new HashMap<>();

	private final int RATE = 20;

	@Override
	public void register(NOPE plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
						continue;
					CPlayer cp = plugin.getCPlayer(player);

					int oldFireTicks = ticks.getOrDefault(player.getUniqueId(), 0);

//					cp.setTempData("oldFireTicks", player.getFireTicks());
					ticks.put(player.getUniqueId(), player.getFireTicks());

					if (oldFireTicks - player.getFireTicks() <= RATE + 10)
						continue;

					if (cp.timeSince(Stat.IN_LIQUID) < 500)
						continue;

					cp.flagHack(AntiFire1.this,
							(int) Math.round((oldFireTicks - player.getFireTicks() - (RATE + 10)) / 20));
				}
			}
		}.runTaskTimer(plugin, 0, RATE);
	}

	@Override
	public String getCategory() {
		return "AntiFire";
	}

	@Override
	public String getDebugName() {
		return "AntiFire#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
