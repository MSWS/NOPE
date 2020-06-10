package xyz.msws.nope.modules.trust;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.reports.ReportTracker;

public class ReportRating implements TrustRating, Listener {

	private ReportTracker tracker;

	public ReportRating(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		this.tracker = plugin.getModule(ReportTracker.class);
	}

	@Override
	public double getTrust(UUID uuid) {
		double v = .5;
		v += Math.min(tracker.getReports(uuid).size(), 3);
		v -= Math.min(tracker.getReported(uuid).size(), 5);

		return Math.min(Math.max(0, v / 3.5), 1);
	}

	@Override
	public float getWeight() {
		return .075f;
	}

}
