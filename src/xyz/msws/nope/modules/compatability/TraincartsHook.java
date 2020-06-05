package xyz.msws.nope.modules.compatability;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.player.PlayerFlagEvent;
import xyz.msws.nope.modules.checks.Check;

public class TraincartsHook extends AbstractHook {

	public TraincartsHook(NOPE plugin) {
		super(plugin);
	}

	@EventHandler
	public void check(PlayerFlagEvent event) {
		Check check = event.getCheck();
		Player player = event.getPlayer();
		if (!check.getCategory().equals("NoSwing"))
			return;
		Block target = player.getTargetBlockExact(7);
		if (target == null || target.getType() == Material.AIR)
			return;
		if (!target.getType().toString().contains("RAIL"))
			return;
		event.setCancelled(true);
	}

	@Override
	public String getName() {
		return "TrainCarts";
	}

}
