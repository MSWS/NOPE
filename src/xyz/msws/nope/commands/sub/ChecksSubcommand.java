package xyz.msws.nope.commands.sub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Checks;
import xyz.msws.nope.utils.MSG;

public class ChecksSubcommand extends Subcommand {

	private Checks checks;

	public ChecksSubcommand(NOPE plugin) {
		super(plugin);
		this.checks = plugin.getModule(Checks.class);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
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

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "List all NOPE checks";
	}

}
