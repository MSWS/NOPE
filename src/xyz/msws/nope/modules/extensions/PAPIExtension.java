package xyz.msws.nope.modules.extensions;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Checks;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

public class PAPIExtension extends PlaceholderExpansion {

	private NOPE plugin;

	public PAPIExtension(NOPE plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getAuthor() {
		return "MSWS";
	}

	@Override
	public String getIdentifier() {
		return "nope";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player p, String params) {
		if (params.equalsIgnoreCase("banwave"))
			return MSG.getTime(plugin.getModule(Banwave.class).timeToNextBanwave());
		if (params.equalsIgnoreCase("version"))
			return plugin.getDescription().getVersion();
		if (params.equalsIgnoreCase("online_version"))
			return plugin.getPluginInfo().getVersion();
		if (params.equalsIgnoreCase("check_amo"))
			return "" + plugin.getModule(Checks.class).getAllChecks().size();

		if (params.toLowerCase().startsWith("check_amo_")) {
			try {
				return plugin.getModule(Checks.class)
						.getChecksWithType(CheckType.valueOf(params.substring("check_amo_".length()).toUpperCase()))
						.size() + "";
			} catch (IllegalArgumentException e) {
				return plugin.getModule(Checks.class).getChecksByCategory(params.substring("check_amo_".length()))
						.size() + "";
			}
		}

		if (p == null)
			return null;
		CPlayer cp = plugin.getCPlayer(p);
		if (params.equalsIgnoreCase("vl"))
			return cp.getTotalVL() + "";
		if (params.toLowerCase().startsWith("vl_"))
			return cp.getVL(params.toLowerCase().substring("vl_".length())) + "";
		if (params.equalsIgnoreCase("vl_highest"))
			return cp.getVL(cp.getHighestHack()) + "";
		if (params.equalsIgnoreCase("banwaved"))
			return plugin.getModule(Banwave.class).isInBanwave(p.getUniqueId()) ? "true" : "false";
		if (params.equalsIgnoreCase("highest_flag"))
			return cp.getHighestHack();

		return null;
	}

}
