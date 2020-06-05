package xyz.msws.anticheat.commands.sub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.CommandResult;
import xyz.msws.anticheat.commands.Subcommand;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Checks;
import xyz.msws.anticheat.utils.MSG;

public class ChecksSubcommand extends Subcommand {

	private Checks checks;

	public ChecksSubcommand(NOPE plugin) {
		super(plugin);
		this.checks = plugin.getModule(Checks.class);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender) {
		return null;
	}

	@Override
	public String getName() {
		return "checks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("nope.command.checks")) {
			return CommandResult.NO_PERMISSION;
		}

		for (CheckType type : checks.getCheckTypes()) {
			Map<String, Integer> checkMap = new HashMap<>();
			for (Check check : checks.getChecksWithType(type))
				checkMap.put(check.getCategory(), checkMap.getOrDefault(check.getCategory(), 0) + 1);

			if (checkMap.isEmpty())
				continue;
			MSG.tell(sender, " ");

			StringBuilder builder = new StringBuilder();

			String[] colors = { "&b", "&a" };

			for (int i = 0; i < checkMap.keySet().size(); i++) {
				builder.append(colors[i % colors.length] + checkMap.keySet().toArray()[i] + " "
						+ checkMap.values().toArray()[i] + " ");
			}

			MSG.tell(sender, "&6&l" + MSG.camelCase(type.toString()) + " &7(&e&l"
					+ checks.getChecksWithType(type).size() + "&7) " + type.getDescription());
			MSG.tell(sender, builder.toString());
		}

		MSG.tell(sender, "&c&lTotal Checks: &4" + checks.getAllChecks().size());
		return CommandResult.SUCCESS;
	}

}
