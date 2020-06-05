package xyz.msws.nope.modules.scoreboard;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

public class DevScoreboard extends CScoreboard implements Listener {

	private Player player;

	public DevScoreboard(NOPE plugin, Player player) {
		super(plugin, player);
		setTitle(MSG.color("&c&l[&4&lNOPE&c&l - &dDEV&c&l]"));
		this.player = player;
		lines.clear();
		lines.add("");
		lines.add("&7- &cMSWS");
		lines.add("&aThank you for using NOPE");

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void onTick() {
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.equals(this.player))
			return;
		CPlayer cp = plugin.getCPlayer(player);

		Location from = event.getFrom(), to = event.getTo();

		lines.clear();
		lines.add("&9From: &b" + String.format("%.3f, %.3f, %.3f", from.getX(), from.getY(), from.getZ()));
		lines.add("&9To: &b" + String.format("%.3f, %.3f, %.3f", to.getX(), to.getY(), to.getZ()));
		lines.add("&9Diff: &e" + String.format("%.3f, %.3f, %.3f", to.getX() - from.getX(), to.getY() - from.getY(),
				to.getZ() - from.getZ()));
		lines.add("&9Fall: &e" + String.format("%.3f", player.getFallDistance()));
		double dst = cp.distanceToGround();
		lines.add(String.format("&9DST: &b%.3f &b(&a%s&b)", dst,
				MSG.camelCase(player.getLocation().clone().subtract(0, dst, 0).getBlock().getType().toString())));
		lines.add("");
		lines.add(String.format("&9In: &e%s &7(S: %s&7, L: %s&7)", MSG.camelCase(to.getBlock().getType().toString()),
				MSG.TorF(to.getBlock().getType().isSolid()), MSG.TorF(to.getBlock().isLiquid())));

		lines.add(String.format("&9OnGround&7: %s", MSG.TorF(player.isOnGround())));
		lines.add("");

		Collections.reverse(lines);
	}

}
