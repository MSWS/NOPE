package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Checks Y differences and flags if they aren't <i>normal</i>
 * 
 * @author imodm
 *
 */
public class FastClimb1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private double[] requires = { 0.11760000228882461, -0.12528413605794242, -0.15000000000000568, -0.09791587996380713,
			0.1544480052490229, -0.19155199856568572, -0.07840000152587834, 0.11215904732696913, -0.06569871584389375,
			-0.1099158685197068, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721, 0.08307781780646906,
			-0.1207870772187647, -0.08910296633784753, 0.0, 0.41999998688697815, -0.005440954961855482,
			-0.11984318109608694, 0.0368480029601983, -0.04228895792205378, -0.08384095648773382, -0.10427317570025707,
			-0.03502770378921127, -0.11215904732696913, -0.09430295184198201, 0.1176000022888104, -0.12528413605795663,
			-0.031424292715740876, -0.009102947264338468, -0.11231585311784897, -0.11310294344964689,
			-0.0799158554066679, -0.11760000228882461 - 0.07751587080852573, -0.10447491355785132, -0.16213213901814072,
			-0.012715875386163589, -0.1467638714799051, -0.12707974915488762, -0.008218816546985863,
			-0.05276385886753587, -0.11760000228882461, -0.11311584904088079, -0.027343570020093466,
			-0.07751587080852573, -0.04511587309734466, -0.06983173703940793, -0.14806349467819757, -0.1303158776749882,
			-0.15000000000000036, 0.1176000022888184, -0.12528413605794686, -0.11471592116250662, 0.06408410859212932,
			0.07248412919149239, -0.07751587080850797, -0.09791587996378048, 0.33319999363422337, 0.24813599859094548,
			0.16477328182606676, 0.08307781780646728, -0.07840000152587923, -0.12078707721879933, -0.12528413605794597,
			0.1176000022888175, -0.012715875386144049, 0.036848002960205406, -0.1544480052490238, -0.08070294573848891,
			-0.14551584675207163, -0.0054409549618528175, -0.11984318109609404, -0.10110295489376142,
			0.1544480052490238, -0.11755094869869431, -0.16213213901815227, -0.04751585769548505, -0.0943159120072341,
			-0.04228895792205822, -0.08403881097767041, -0.06829374383774223, -0.12114100255522686, -0.1176000022888184,
			-0.12263174390583398, -0.10991586851968993, -0.11215904732696558, -0.005031741617015584,
			-0.07602512945790085, -0.11310294344967087, -0.0698317370393795, 0.11215904732696558, -0.12487492271310963,
			-0.08254761242552355, -0.11269373010483363, -0.04150294497554974, 0.39025238132790907,
			-0.049563878346349455, -0.09430295184200421, 0.07473800922336427, -0.14999999999999858, };

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (!cp.isInClimbingBlock())
			return;

		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;

		Location to = event.getTo();
		double dist = to.getY() - event.getFrom().getY();

		boolean normal = false;

		for (double d : requires) {
			if (d == dist) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&a" + dist);
		cp.flagHack(this, 15);
	}

	@Override
	public String getCategory() {
		return "FastClimb";
	}

	@Override
	public String getDebugName() {
		return "FastClimb#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
