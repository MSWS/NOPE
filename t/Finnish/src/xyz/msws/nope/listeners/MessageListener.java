package xyz.msws.nope.listeners;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.utils.MSG;

/**
 * Listens for messages from other NOPE plugins on the same bungee network.
 * 
 * @author imodm
 *
 */
public class MessageListener extends AbstractModule implements PluginMessageListener {

	public MessageListener(NOPE plugin) {
		super(plugin);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!"BungeeCord".equals(channel))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String sub = in.readUTF();

		if (sub.equals("GetServer")) {
			plugin.setServerName(in.readUTF());
			return;
		}

		if (!"NOPE".equals(sub))
			return;

		String msg = in.readUTF();

		if (msg.startsWith("perm:")) {
			String perm = msg.split(" ")[0].substring("perm:".length());
			MSG.tell(perm, msg.substring(msg.split(" ")[0].length()));
		} else if (msg.startsWith("console:")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg.substring("console:".length()));
		} else if (msg.startsWith("playercommand:")) {
			player.performCommand(msg.substring("playercommand:".length()));
		} else if (msg.startsWith("playerchat:")) {
			player.chat(msg.substring("playerchat:".length()));
		} else if (msg.startsWith("setvl:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("setvl:".length()));
			String hack = msg.split(" ")[1];
			int vl = Integer.parseInt(msg.split(" ")[2]);
			plugin.getCPlayer(off).setSaveData("vls." + hack, vl);
		} else if (msg.startsWith("addvl:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("setvl:".length()));
			String hack = msg.split(" ")[1];
			int vl = Integer.parseInt(msg.split(" ")[2]);
			plugin.getCPlayer(off).setSaveData("vls." + hack,
					plugin.getCPlayer(off).getSaveData("vls." + hack, int.class) + vl);
		} else if (msg.startsWith("clearvl:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("clearvl:".length()));
			plugin.getCPlayer(off).clearVls();
		} else if (msg.startsWith("banwave:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("banwave:".length()));
			Banwave wave = plugin.getModule(Banwave.class);
			Check decoy = new Check() {

				@Override
				public void register(NOPE plugin) throws OperationNotSupportedException {
				}

				@Override
				public boolean lagBack() {
					return false;
				}

				@Override
				public CheckType getType() {
					return CheckType.MISC;
				}

				@Override
				public String getDebugName() {
					return "ManuallyIssued#3";
				}

				@Override
				public String getCategory() {
					return "Manual";
				}
			};
			String reason = String.join(" ",
					(CharSequence[]) ArrayUtils.subarray(msg.split(" "), 2, msg.split(" ").length));
			plugin.getModule(Banwave.class).addPlayer(off.getUniqueId(),
					wave.new BanwaveInfo(off.getUniqueId(), decoy, Long.parseLong(msg.split(" ")[1]), reason));
		} else if (msg.startsWith("removebanwave:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("removebanwave:".length()));
			plugin.getCPlayer(off).removeSaveData("isBanwaved");
		} else if (msg.equals("banwave")) {
			plugin.getModule(Banwave.class).runBanwave();
		} else {
			MSG.announce(msg);
		}
	}

	@Override
	public void enable() {
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
	}

	@Override
	public void disable() {
	}
}
