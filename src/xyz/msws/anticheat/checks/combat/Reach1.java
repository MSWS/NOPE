package xyz.msws.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.CheckType;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

/**
 * Compares distances
 * 
 * @author imodm
 *
 *
 * @deprecated
 */
public class Reach1 implements Check, Listener {

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

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() == null || !(event.getDamager() instanceof Player))
			return;
		Player damager = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(damager);

		double dist = Math.abs(damager.getLocation().getX() - event.getEntity().getLocation().getX())
				+ Math.abs(damager.getLocation().getZ() - event.getEntity().getLocation().getZ());

		double maxDist = -1;

		switch (event.getEntityType()) {
		case CREEPER:
		case ZOMBIE:
		case PIG_ZOMBIE:
		case VILLAGER:
		case PLAYER:
			maxDist = 5.051;
			break;
		case SHEEP:
		case COW:
		case MUSHROOM_COW:
			maxDist = 5.298;
			break;
		case SPIDER:
			maxDist = 5.601;
			break;
		case CAVE_SPIDER:
			maxDist = 4.8659;
			break;
		case WOLF:
			maxDist = 4.909;
			break;
		case HORSE:
			maxDist = 5.806;
			break;
		case GUARDIAN:
			maxDist = 5.0956;
			break;
		case SNOWMAN:
			maxDist = 5.0793;
			break;
		case ENDERMAN:
			maxDist = 5.0517;
			break;
		case CHICKEN:
			maxDist = 4.6032;
			break;
		case GIANT:
			maxDist = 8.0237;
			break;
		case IRON_GOLEM:
			maxDist = 5.8423;
			break;
		case ENDERMITE:
			maxDist = 4.5158;
			break;
		case RABBIT:
			maxDist = 4.80865;
			break;
		case SILVERFISH:
			maxDist = 4.4343;
			break;
		case ARMOR_STAND:
			maxDist = 4.8995;
			break;
		case SQUID:
			maxDist = 5.2988;
			break;
		case SKELETON:
			maxDist = 4.9875;
			break;
		case WITCH:
			maxDist = 5.02733;
			break;
		case OCELOT:
			maxDist = 4.8468;
			break;
		case PIG:
			maxDist = 5.197556;
			break;
		case BAT:
			maxDist = 4.83962;
			break;
		case BLAZE:
			maxDist = 5.0166;
			break;
		case WITHER:
			maxDist = 5.327;
			break;
		case GHAST:
			maxDist = 6.1373;
			break;
		case MAGMA_CUBE:
			MagmaCube cube = (MagmaCube) event.getEntity();
			switch (cube.getSize()) {
			case 1:
				maxDist = 4.7171;
				break;
			case 2:
				maxDist = 5.39693;
				break;
			case 3:
				maxDist = 5.97905;
				break;
			case 4:
				maxDist = 6.4702;
				break;
			default:
				maxDist = (.51 + cube.getSize() * .55) + 3.7845;
				break;
			}
			break;
		case SLIME:
			Slime slime = (Slime) event.getEntity();
			switch (slime.getSize()) {
			case 1:
				maxDist = 4.7171;
				break;
			case 2:
				maxDist = 5.39693;
				break;
			case 3:
				maxDist = 5.97905;
				break;
			case 4:
				maxDist = 6.4702;
				break;
			default:
				maxDist = (.51 + slime.getSize() * .55) + 3.7845;
				break;
			}
			break;
		default:
			break;
		}

		if (damager.getGameMode() == GameMode.CREATIVE)
			maxDist += 2.5;

		if (dist <= maxDist)
			return;

		cp.flagHack(this, (int) (Math.round((dist - maxDist) * 15) + 5),
				"Entity: &e" + MSG.camelCase(event.getEntityType().toString()) + "\n&7Reach: &e" + dist + "&7 > &a"
						+ maxDist + "\n&7Diff: " + (dist - maxDist));
	}

	@Override
	public String getCategory() {
		return "Reach";
	}

	@Override
	public String getDebugName() {
		return "Reach#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
