package org.mswsplex.anticheat.checks.player;

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
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

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

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Material[] foods = { Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.COOKED_FISH,
			Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH,
			Material.APPLE, Material.SPIDER_EYE, Material.RABBIT, Material.RABBIT_STEW, Material.PUMPKIN_PIE,
			Material.POTATO_ITEM, Material.BAKED_POTATO, Material.COOKIE, Material.MELON, Material.SPECKLED_MELON,
			Material.MUSHROOM_SOUP, Material.BREAD, Material.GOLDEN_APPLE, Material.GOLDEN_CARROT,
			Material.ROTTEN_FLESH };

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!isFood(hand.getType()))
			return;

		cp.setTempData("foodStartEating", (double) System.currentTimeMillis());
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

		double diff = cp.timeSince("foodStartEating");

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

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}
}
