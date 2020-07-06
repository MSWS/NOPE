пакет xyz.msw.nope.command;

імпорт jav.util.ArrayList;
імпорт java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

імпортувати xyz.msws.nope;
імпортувати xyz.mswpe.modules.AbstractModule;
імпортувати xyz.msws.nope.utils.MSG;

публічний абстрактний клас AbstractCommand розширює живопадання CommandExecExecutor, TabCompleter {

	захищений список<Subcommand> cmds = new ArrayList<>();

	публічна AbstractCommand(NOPE плагін) {
		супер(плагін);
	}

	публічний абстрактний рядок getName();

	@Перевизначити
	публічний void enable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(це);
		cmd.setTabCompleter(це);
	}

	публічна порожнеча disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	публічний список<Subcommand> getSubCommands() {
		повертати cms;
	}

	@Перевизначити
	публічний логічний onCommand(CommandSender відправник, командна команда, рядкова етикетка, String[] args) {
		if (args.length < 1)
			повернути хибність;
		String cmd = args[0];
		для (Subcommand : cmds) {
			якщо (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Команда.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cВам не вистачає &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					повернути вірну;
				}
				CommandResult результат = c.execute(sender, args);
				якщо (результат == CommandResult.SUCCESS)
					повернути вірну;
				якщо (результат == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Команда.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cВам не вистачає &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					повернути вірну;
				}
				MSG.tell(sender, "&4" + label + " > &cProper використовувати для " + c.getName());
				MSG.tell(send(sender, "&7/" + label + " + c.getName() + " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				повернути вірну;
			}
		}
		повернути хибність;
	}

	@Перевизначити
	публічний список<String> onTabComplete(CommandSender відправник, командна команда, назва String Strel, String[] args) {
		Список<String> результат = new ArrayList<>();

		до (підкоманда підкоманда: cmds) {
			Список<String> псевдонімів = sub.getAliases();
			aliases.add(sub.getName());
			Список<String[]> completions = sub.tabCompletions(sender);
			if (args.length > 1) {
				якщо (completions == null || completions.isEmpty())
					продовжувати;
				якщо (completions.size() < args.length - 1)
					продовжувати;
				if (!aliases.contains(args[0].toLowerCase()))
					продовжувати;
				String[] res = completions.get(args.length - 2);
				якщо (res == null)
					продовжувати;
				для (Рядок : res)
					якщо (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						результат. add(r);
				продовжувати;
			}
			для (псевдоніми рядка: псевдоніма) {
				якщо (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					результат.add(псевдонім);
			}
		}

		повернути результат.isEmpty()? null : результат;
	}

}
