пакет xyz.msws.nope.command;

импорт java.util.ArrayList;
импорт java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand расширяет AbstractModule реализует CommandExecutor, TabCompleter {

	защищённый список<Subcommand> cmds = new ArrayList<>();

	public AbstractCommand(NOPE plugin) {
		супер(плагин);
	}

	публичная абстрактная строка getName();

	@Переопределение
	публичная пустота enable() {
		Команда PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}

	public void disable() {
		Команда PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(нулево);
	}

	публичный список<Subcommand> getSubCommands() {
		возвращать команд;
	}

	@Переопределение
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			return false;
		Строка cmd = дуги[0];
		for (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cВы не можете получить &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					возврат true;
				}
				Результат CommandResult = c.execute(sender, args);
				если (результат == CommandResult.SUCCESS)
					возврат true;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cВы не можете получить &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					возврат true;
				}
				MSG.tell(sender, "&4" + label + " > &cПравильное использование для " + c.getName());
				MSG.tell(sender, "&7/" + label + " " + c.getName() + " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				возврат true;
			}
		}
		return false;
	}

	@Переопределение
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Список<String> = новый список ArrayList<>();

		for (Subcommand sub : cmds) {
			Список<String> псевдонимов = sub.getAliases();
			aliases.add(sub.getName());
			Список дополнений<String[]> = sub.tabCompletions(sender);
			if (args.length > 1) {
				если (completions == null || completions.isEmpty())
					продолжать;
				если (completions.size() < args.length - 1)
					продолжать;
				если (!aliases.contains(args[0].toLowerCase()))
					продолжать;
				Строка[] res = completions.get(args.length - 2);
				если (res == null)
					продолжать;
				для (String r : res)
					если (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				продолжать;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		return result.isEmpty() ? null : результат;
	}

}
