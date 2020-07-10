package xyz.msws.nope.commands.sub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.actions.Webhook;
import xyz.msws.nope.modules.data.Log;
import xyz.msws.nope.utils.MSG;
import xyz.msws.nope.utils.Utils;

public class FalseSubcommand extends Subcommand {

	private Webhook reporter;

	public FalseSubcommand(NOPE plugin) {
		super(plugin);

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String query = null;
			try {
				// Get the updated webhook URL
				URL url = new URL("https://pastebin.com/raw/jqVCM1Q0");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "NOPE/MC-" + plugin.getDescription().getVersion());
				InputStreamReader iread = new InputStreamReader(conn.getInputStream());
				BufferedReader reader = new BufferedReader(iread);
				query = reader.readLine();
				reader.close();
				iread.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			reporter = new Webhook(plugin, query);
			reporter.setUsername("False Positive Report");
			reporter.setAvatarURL("https://i.imgur.com/JLKC7CN.jpg");
		});
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "false";
	}

	@Override
	public String getUsage() {
		return "<player> <message>";
	}

	@Override
	public String getDescription() {
		return "Report a recent false positive";
	}

	private Map<UUID, Long> lastReport = new HashMap<>();

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (reporter == null)
			return CommandResult.ERROR;

		Player target = null;
		if (!(sender instanceof Player)) {
			if (args.length == 0)
				return CommandResult.PLAYER_REQUIRED;
			target = Bukkit.getPlayer(args[0]);
		} else {
			target = args.length == 2 ? Bukkit.getPlayer(args[0]) : (Player) sender;
		}

		String message = args.length > 2 ? String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length))
				: null;

		if (target == null)
			return CommandResult.INVALID_ARGUMENT;

		if (System.currentTimeMillis() - lastReport.getOrDefault(target.getUniqueId(), 0L) < 120000) {
			MSG.tell(sender,
					MSG.getString("Command.FalsePositive.TooMany",
							"&e%player% &chas caused too many false positives recently, please wait to report more.")
							.replace("%player%", target.getName()));
			return CommandResult.SUCCESS;
		}

		Log log = plugin.getCPlayer(target).getLog();
		List<String> lines = log.getLinesFrom(120000);
		if (lines.size() < 10) {
			MSG.tell(sender, MSG.getString("Command.FalsePositive.Insufficient",
					"&cThere isn't enough data to report as a false positives."));
			return CommandResult.SUCCESS;
		}

		List<String> header = new ArrayList<>();
		header.add("New false report on " + target.getName());
		header.add("Reporter: " + sender.getName() + " Online Players: " + Bukkit.getOnlinePlayers().size() + " Owner: "
				+ Bukkit.getOfflinePlayers()[0].getName());
		header.add("Bukkit Version: " + Bukkit.getBukkitVersion() + " MC: " + Bukkit.getVersion());
		header.add("NOPE Version: " + plugin.getDescription().getVersion() + " Online: " + plugin.getNewVersion());
		if (message != null)
			header.add("Description: " + message);
		header.add("");
		header.add("Armor: " + target.getInventory().getArmorContents());
		header.add("Potions: " + target.getActivePotionEffects());
		StringBuilder pbuilder = new StringBuilder();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			pbuilder.append(p.getName() + ":" + p.getDescription().getVersion()).append(" ");
		}
		header.add("Plugins: " + pbuilder.toString());
		header.addAll(lines);
		header.add("--- END OF PLAYER LOG ---");
		header.add("");
		File file = plugin.getConfigFile();
		try {
			StringBuilder builder = new StringBuilder();
			FileReader reader = new FileReader(file);
			BufferedReader breader = new BufferedReader(reader);
			String line;

			while ((line = breader.readLine()) != null) {
				builder.append(line).append("\n");
			}
			header.add(builder.toString());
			breader.close();
			reader.close();
		} catch (IOException e1) {
			header.add("Unable to read config: " + e1.getMessage());
			e1.printStackTrace();
		}

		lastReport.put(target.getUniqueId(), System.currentTimeMillis());
		MSG.tell(sender, MSG.getString("Command.FalsePositive.Sending", "Sending..."));
		final String name = target.getName();
		new BukkitRunnable() {
			@Override
			public void run() {
				String url;
				try {
					url = Utils.uploadHastebin(String.join("\n", header));
					reporter.sendMessage("A new false positive report has been made: https://hastebin.com/" + url);
					MSG.tell(sender, MSG.getString("Command.FalsePositive.Sent", "Successfully sent.")
							.replace("%player%", name));
				} catch (IOException e) {
					e.printStackTrace();
					MSG.tell(sender, MSG.getString("Command.FalsePositive.Error", "An unknown error occured."));
					return;
				}
			}
		}.runTaskAsynchronously(plugin);
		return CommandResult.SUCCESS;
	}

	@Override
	public String getPermission() {
		return "nope.command.false";
	}

}
