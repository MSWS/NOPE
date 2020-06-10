package xyz.msws.nope.modules.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.player.PlayerReportEvent;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.MSG;

/**
 * Tracks reports
 * 
 * @author imodm
 *
 */
public class ReportTracker extends AbstractModule {

	private Map<String, Report> reports;

	public ReportTracker(NOPE plugin) {
		super(plugin);
	}

	public void addReport(Report report) {
		PlayerReportEvent event = new PlayerReportEvent(report);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		String msg = MSG.getString("Report.Notification",
				"&4Report #%id% > &e%player% &7has reported &a%target% &7for &b%reason%&7.");
		msg = msg.replace("%player%", Bukkit.getOfflinePlayer(report.getReporter()).getName());
		msg = msg.replace("%target%", Bukkit.getOfflinePlayer(report.getTarget()).getName());
		msg = msg.replace("%reasoon%", report.getReason());
		msg = msg.replace("%id%", report.getId());

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.hasPermission("nope.message.report"))
				continue;
			CPlayer cp = plugin.getCPlayer(p);
			if (!cp.getOption("reports").asBoolean())
				continue;
			MSG.tell(p, msg);
		}

		reports.put(report.getId(), report);
	}

	public List<Report> getReports(UUID player) {
		return reports.values().stream().filter(r -> r.getTarget().equals(player)).collect(Collectors.toList());
	}

	public List<Report> getReported(UUID player) {
		return reports.values().stream().filter(r -> r.getReporter().equals(player)).collect(Collectors.toList());
	}

	public List<Report> getReports(UUID reporter, UUID target) {
		return getReported(reporter).stream().filter(r -> r.getTarget().equals(target)).collect(Collectors.toList());
	}

	@Override
	public void enable() {
		reports = new HashMap<>();
	}

	@Override
	public void disable() {
		if (reports != null)
			reports.clear();
	}

}
