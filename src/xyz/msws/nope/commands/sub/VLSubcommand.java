package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.PlayerManager;
import xyz.msws.nope.utils.MSG;

public class VLSubcommand extends Subcommand {

	public VLSubcommand(NOPE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "vl";
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandResult execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission("nope.command.vl"))
			return CommandResult.NO_PERMISSION;

		CPlayer cp;
		if (args.length == 1) {
			boolean shown = false;
			for (Player p : Bukkit.getOnlinePlayers()) {
				cp = plugin.getCPlayer(p);
				String vls = formatVls(p);
				if (vls.isEmpty())
					continue;
				shown = true;
				MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + p.getName() + "&7: " + formatVls(p));
			}
			if (!shown)
				MSG.tell(sender, MSG.getString("Command.VL.NoVLs", "There are no violation levels right now."));
			return CommandResult.SUCCESS;
		}

		OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);

		cp = plugin.getCPlayer(t);

		if (formatVls(t).isEmpty()) {
			MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + " &chas no VLs.");
			return CommandResult.SUCCESS;
		}

		MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + "&7: " + formatVls(t));
		return CommandResult.SUCCESS;
	}

	private String formatVls(OfflinePlayer player) {
		CPlayer cp = plugin.getModule(PlayerManager.class).getPlayer(player.getUniqueId());
		HashMap<String, Integer> vls = new HashMap<>();
		ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return "";

		for (String hack : vlSection.getKeys(false)) {
			if (vlSection.getInt(hack) > 0)
				vls.put(MSG.camelCase(hack), vlSection.getInt(hack));
		}

		String result = "";
		for (String hack : vls.keySet()) {
			result += hack + " " + vls.get(hack) + "&7, ";
		}
		result = result.substring(0, Math.max(result.length() - 2, 0));
		return result;
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public String getUsage() {
		return "<target>";
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "View VLs of a player";
	}

}
