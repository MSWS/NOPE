package xyz.msws.anticheat.compatability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.events.PlayerFlagEvent;

public class CrazyEnchantsHook extends AbstractHook {

	private CrazyEnchantments ce;

	public CrazyEnchantsHook(NOPE plugin) {
		super(plugin);
		this.ce = CrazyEnchantments.getInstance();
	}

	@Override
	public String getName() {
		return "CrazyEnchantments";
	}

	@EventHandler
	public void blastCheck(PlayerFlagEvent event) {
		Player player = event.getPlayer();
		Check check = event.getCheck();
		if (!check.getCategory().equals("FastBreak"))
			return;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!ce.hasEnchantment(item, CEnchantments.BLAST))
			return;
		event.setCancelled(true);
	}

}
