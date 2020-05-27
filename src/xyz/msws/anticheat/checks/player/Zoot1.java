package xyz.msws.anticheat.checks.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * 
 * Compares a player's previous potion ticks and flags if they've dropped too
 * suddenly this WILL flag commands such as /heal
 * 
 * @author imodm
 * 
 */
public class Zoot1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, Map<PotionEffectType, Integer>> potionTimes = new HashMap<>();
	private Map<UUID, Long> reset = new HashMap<>();

	private final int RATE = 20;

	@SuppressWarnings("unused")
	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);

					for (PotionEffectType type : PotionEffectType.values()) {
						int currentTicks = getPotionDuration(player.getActivePotionEffects(), type);
						Map<PotionEffectType, Integer> oldActive = potionTimes.getOrDefault(player.getUniqueId(),
								new HashMap<>());

						int oldPotionTicks = oldActive.getOrDefault(type, 0);
						oldActive.put(type, currentTicks);
						potionTimes.put(player.getUniqueId(), oldActive);

						if (oldPotionTicks - currentTicks <= RATE + 15)
							continue;

						if (System.currentTimeMillis() - reset.getOrDefault(player.getUniqueId(), 0L) < 2000)
							return;

						cp.flagHack(Zoot1.this,
								Math.min((int) Math.round((oldPotionTicks - currentTicks - (RATE + 15)) / 20), 200));
					}
				}
			}
		}.runTaskTimer(plugin, 0, RATE);
	}

	private int getPotionDuration(Collection<PotionEffect> effects, PotionEffectType type) {
		for (PotionEffect effect : effects) {
			if (effect.getType() == type)
				return effect.getDuration();
		}
		return 0;
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = event.getItem();

		if (hand == null || hand.getType() != Material.MILK_BUCKET)
			return;

		reset.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		reset.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onEntityEffect(EntityPotionEffectEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof Player))
			return;
		Player player = (Player) ent;
		reset.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@Override
	public String getCategory() {
		return "Zoot";
	}

	@Override
	public String getDebugName() {
		return "Zoot#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
