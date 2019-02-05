package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class GeneralMovement1 implements Check, Listener {

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

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (cp.isOnGround() || player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;
		if (player.getFallDistance() > 4)
			return;
		if (cp.hasMovementRelatedPotion())
			return;
		if (cp.timeSince("lastLiquid") < 400)
			return;
		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;

		Location to = event.getTo();
		Vector vectorToSafe = to.toVector().subtract(cp.getLastSafeLocation().toVector());
		double distFromLast = Math.abs(cp.getTempDouble("generalMovement1") - vectorToSafe.getY());
		cp.setTempData("generalMovement1", vectorToSafe.getY());

		if (cp.timeSince("lastWeirdBlock") < 1200)
			return;

		double[] requires = {

				// Regular Movements
				0.7531999805212024, 0.24813599859093927, 0.1647732818260721, 0.08307781780646906, 0.0,
				0.07840000152587834, 0.15523200451660557, 0.23052736891295922, 0.30431682745754074, 0.37663049823865435,
				0.1040803780930446, 0.015555072702198913, 0.2468000194787976, 0.41999998688697815, 0.33319999363422426,
				0.44749789698342113, 0.5169479491049742, 0.13963453200464926, 1.7531999805212024, 0.5850090015087517,
				0.6517088341626192, 0.7170746714355971, 0.1858420248976813, 2.7531999805212024, 1.9199999868869781,
				0.05462553049589758, 2.6731999674081806, 1.8399999737739563, 0.8972556010190971, 0.5206251027804427,
				0.2850277037892397, 0.6731999674081806, 0.07524648888445995, 0.4047088316877989, 6.753199980521202,
				1.2468000194787976, 0.4453744695041024, 0.23415796198929684, 3.7531999805212024, 0.5546255304958976,
				0.7658420380107032, 4.753199980521202, 2.2468000194787976, 2.0, 0.23052736891296632, 0.6491196024281649,
				0.15523200451659847, 0.1040803780930375, 1.6135654485165531, 2.613565448516553, 0.2336320060424839,
				0.2663679939575161, 0.7663679939575161, 1.233632006042484, 0.07840000252588197, 3.567357955623528,

				// Slab interactions
				0.23152379758701613, 0.03584062504455687, 1.5, .5, 1.0, 0.15658248110962347, 0.08133599222516352,
				1.4199999868869781, 0.2531999805212024, 1.2531999805212024, 0.3959196219069554, 0.3386639946618146,
				1.6731999674081806, 0.39937488410653543, 0.4844449272978011, 1.3386639946618146, 0.20369171156407617,
				0.5813359922251635, 0.9199999868869781, 0.3548932993483618, 0.7468000194787976, 2.2531999805212024,

				// Coming out of water
				3.567357955623521, 1.7658420380107032, 6.34107634432705, 3.8297130478045176, 2.6453931717845904,
				0.6453931717845904, 3.2468000194787976, 3.9802672360405325, 3.1095525467400336, 0.9644458460883953,
				2.1095525467400336, 4.694561548248785, 3.0311525452141552, 1.8759205406975497, 0.40444491418477924,
				3.2948688514340176, 5.861701447107492, 1.2050437520934025, 0.04150916224899959, 3.8154939542144604,
				2.616161546722907, 1.6945615482487852, 3.549454847597147, 0.19857445002530483, 1.1451067006516453,
				0.7170746714356042, 3.5991856788915655, 1.1284452226796233, 2.611483730442316, 1.5926045976350665,
				1.096617499411586, 1.3447534980025253, 1.5095267798285974, 1.3589725915925825, 1.5142045961091881,
				2.198574450025305, 3.1126383763689205, 7.105427357601002E-15, 3.2631925646049353, 0.7663679939575232,
				1.8983066811728406, 0.40739540236494065, 1.2491870787446828 };

		boolean normal = false;
		for (double d : requires) {
			if (d == distFromLast) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;

		MSG.tell(player, "&c" + distFromLast);

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "GeneralMovement";
	}

	@Override
	public String getDebugName() {
		return "GeneralMovement#1";
	}
}
