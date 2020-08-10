package xyz.msws.nope.checks.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Compares timings between clicks
 * 
 * @author imodm
 *
 */
public class AutoClicker1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20;

	private Map<UUID, List<Double>> timings = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Block block = event.getClickedBlock();

		ItemStack hand = player.getInventory().getItemInMainHand();

		if (hand != null && hand.containsEnchantment(Enchantment.DIG_SPEED))
			return;

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;

		if (block != null && (!block.getType().isSolid()
				|| block.getType() == Material.SLIME_BLOCK
				|| block.getType() == Material.REDSTONE_ORE)) // redstone ore triggers a false positive
			return;

		List<Double> clickTimings = timings.getOrDefault(player.getUniqueId(), new ArrayList<>());

		clickTimings.add(0, (double) System.currentTimeMillis());

		for (int i = SIZE; i < clickTimings.size(); i++)
			clickTimings.remove(i);

		timings.put(player.getUniqueId(), clickTimings);

		if (clickTimings.size() < SIZE)
			return;

		HashMap<Double, Integer> repeats = new HashMap<>();
		double last = System.currentTimeMillis();

		for (int i = 0; i < clickTimings.size(); i++) {
			double diff = last - clickTimings.get(i);
			repeats.put(diff, repeats.containsKey(diff) ? repeats.get(diff) + 1 : 1);
			last = clickTimings.get(i);
		}

		repeats = repeats.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		int biggest = (int) repeats.values().toArray()[repeats.values().size() - 1];

		if (biggest < SIZE / 2)
			return;
		cp.flagHack(this, (int) Math.round(biggest - (SIZE / 2)) * 5 + 5,
				"Similar Values: &e" + biggest + "&7 >= &a" + (SIZE / 2));
	}

	@Override
	public String getCategory() {
		return "AutoClicker";
	}

	@Override
	public String getDebugName() {
		return "AutoClicker#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
