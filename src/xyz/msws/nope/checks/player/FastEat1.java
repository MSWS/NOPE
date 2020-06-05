package xyz.msws.nope.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks the time between holding right click and actually eating
 * 
 * @author imodm
 *
 */
public class FastEat1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, Long> start = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Material[] foods = { Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.PUFFERFISH,
			Material.TROPICAL_FISH, Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.BEEF, Material.CHICKEN,
			Material.APPLE, Material.SPIDER_EYE, Material.RABBIT, Material.RABBIT_STEW, Material.PUMPKIN_PIE,
			Material.POTATO, Material.BAKED_POTATO, Material.COOKIE, Material.MELON, Material.GLISTERING_MELON_SLICE,
			Material.MUSHROOM_STEW, Material.BREAD, Material.GOLDEN_APPLE, Material.GOLDEN_CARROT,
			Material.ROTTEN_FLESH };

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = player.getInventory().getItemInMainHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!isFood(hand.getType()))
			return;

		start.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onSaturationChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getFoodLevel() <= player.getFoodLevel())
			return;

		if (player.hasPotionEffect(PotionEffectType.SATURATION))
			return;

//		double diff = cp.timeSince("foodStartEating");
		long diff = System.currentTimeMillis() - start.getOrDefault(player.getUniqueId(), 0L);

		if (diff > 1400)
			return;

		cp.flagHack(this, (int) Math.round((1400 - diff) / 5), "Delay: &e" + diff + "&7 <= &a1400");
	}

	private boolean isFood(Material mat) {
		for (Material m : foods)
			if (mat == m)
				return true;
		return false;
	}

	@Override
	public String getCategory() {
		return "FastEat";
	}

	@Override
	public String getDebugName() {
		return "FastEat#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
