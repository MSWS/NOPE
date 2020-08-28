package xyz.msws.nope.commands.sub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.checks.Checks;
import xyz.msws.nope.modules.reports.Report;
import xyz.msws.nope.modules.reports.ReportTracker;
import xyz.msws.nope.utils.MSG;

public class ReportSubcommand extends Subcommand {

	private Set<String> cs = new HashSet<>();

	public ReportSubcommand(NOPE plugin) {
		super(plugin);
		this.cs = plugin.getModule(Checks.class).getAllChecks().stream().map(c -> c.getCategory())
				.collect(Collectors.toSet());
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		List<String[]> result = new ArrayList<>();
		result.add(null); // First argument is a player
		result.add(cs.toArray(new String[0]));
		return result;
	}

	@Override
	public String getName() {
		return "report";
	}

	@Override
	public String getUsage() {
		return "[player] [reason]";
	}

	@Override
	public String getDescription() {
		return "Report a player for cheating";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length < 3)
			return CommandResult.MISSING_ARGUMENT;

		if (!(sender instanceof Player))
			return CommandResult.PLAYER_ONLY;
		Player reporter = (Player) sender;
		Player target = Bukkit.getPlayer(args[1]);

		if (target == null)
			return CommandResult.INVALID_ARGUMENT;

		String reason = String.join(" ", (String[]) ArrayUtils.subarray(args, 2, args.length));

		ReportTracker tracker = plugin.getModule(ReportTracker.class);

		List<Report> old = tracker.getReports(reporter.getUniqueId(), target.getUniqueId());
		if (!old.isEmpty()) {
			if (old.get(old.size() - 1).getAge() < TimeUnit.MINUTES.toMillis(10)) {
				MSG.tell(sender, MSG.getString("Command.Report.RecentlyReported", "You reported %player% already.")
						.replace("%player%", target.getName()));
				return CommandResult.SUCCESS;
			}
		}

		old = tracker.getReports(reporter.getUniqueId());
		if (old.stream().filter(o -> o.getAge() < TimeUnit.MINUTES.toMillis(3)).count() >= 3) {
			MSG.tell(sender, MSG.getString("Command.eport.TooMany", "You've reported too many players recently.")
					.replace("%player%", target.getName()));
			return CommandResult.SUCCESS;
		}

		Report report = new Report(((Player) sender).getUniqueId(), target.getUniqueId(), reason);

		tracker.addReport(report);
		MSG.tell(sender, MSG.getString("Command.Report.Success", "(%id%) Reported %player% for %reason%")
				.replace("%player%", target.getName()).replace("%reason%", reason).replace("%id%", report.getId()));
		return CommandResult.SUCCESS;
	}

	@Override
	public String getPermission() {
		return "nope.command.report";
	}

}
