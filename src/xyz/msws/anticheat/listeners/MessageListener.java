package xyz.msws.anticheat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.bans.Banwave;
import xyz.msws.anticheat.utils.MSG;

public class MessageListener implements PluginMessageListener {

	private NOPE plugin;

	public MessageListener(NOPE plugin) {
		this.plugin = plugin;
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
//			plugin.getCPlayer(off).setSaveData("vls." + hack,
//					plugin.getCPlayer(off).getSaveInteger("vls." + hack) + vl);
			plugin.getCPlayer(off).setSaveData("vls." + hack,
					plugin.getCPlayer(off).getSaveData("vls." + hack, int.class) + vl);
		} else if (msg.startsWith("clearvl:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("clearvl:".length()));
			plugin.getCPlayer(off).clearVls();
		} else if (msg.startsWith("banwave:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("banwave:".length()));
			plugin.getCPlayer(off).setSaveData("isBanwaved",
					msg.substring("banwave:".length() + off.getName().length() + 1));
		} else if (msg.startsWith("removebanwave:")) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(msg.split(" ")[0].substring("removebanwave:".length()));
			plugin.getCPlayer(off).removeSaveData("isBanwaved");
		} else if (msg.equals("banwave")) {
			plugin.getModule(Banwave.class).runBanwave(true);
		} else {
			MSG.announce(msg);
		}
	}
}
