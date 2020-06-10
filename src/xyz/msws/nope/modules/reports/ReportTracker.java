package xyz.msws.nope.modules.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.player.PlayerReportEvent;
import xyz.msws.nope.modules.AbstractModule;

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
