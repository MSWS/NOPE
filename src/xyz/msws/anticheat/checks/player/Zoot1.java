package xyz.msws.anticheat.checks.player;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;

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

	private final int RATE = 20;

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
						int oldPotionTicks = cp.getTempInteger("old" + type + "Ticks");
						cp.setTempData("old" + type + "Ticks", currentTicks);

						if (oldPotionTicks - currentTicks <= RATE + 15)
							continue;

						if (cp.timeSince("lastPotionReset") < 2000)
							return;

						cp.flagHack(Zoot1.this, (int) Math.round((oldPotionTicks - currentTicks - (RATE + 15)) / 20));
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
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		ItemStack hand = event.getItem();

		if (hand == null || hand.getType() != Material.MILK_BUCKET)
			return;

		cp.setTempData("lastPotionReset", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData("lastPotionReset", (double) System.currentTimeMillis());
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
