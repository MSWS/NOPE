пакет xyz.msw.nope.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

імпортувати xyz.msws.nope;
імпортувати xyz.msws.nope.commands.sub.BanwaveSubcommand;
імпортувати xyz.msws.nope.commands.sub.ChecksSubcommand;
імпортувати файли xyz.msw.nope.command.sub.ClearSubcommand;
імпортувати xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
імпортувати xyz.msws.nope.commands.sub.HelpSubcommand;
імпортувати xyz.msws.nope.commands.sub.LookupSubcommand;
імпортувати файли xyz.msws.nope.command.sub.OnlineSubcommand;
імпортувати xyz.msw.nope.commands.sub.ReloadSubcommand;
імпортувати команди xyz.msws.nope.command.RemovebanwaveSubcommand;
імпортувати xyz.msws.nope.command.ReportSubcommand;
імпортувати xyz.msws.nope.command.ResetSubcommand;
імпортувати xyz.msws.nope.commands.sub.StatsSubcommand;
імпортувати xyz.msws.nope.commands.sub.TestAnimationSubcommand;
імпортувати xyz.msws.nope.commands.sub.TestlagSubcommand;
імпортувати xyz.msws.nope.commands.sub.TimeSubcommand;
імпортувати файли xyz.msws.nope.command.sub.ToggleSubcommand;
імпортувати xyz.msws.nope.commands.sub.TrustSubcommand;
імпортувати xyz.msws.nope.command.VLSubcommand;
імпортувати файли xyz.msw.nope.command.WarnSubcommand;

публічний клас NOPECommand розширює AbstractCommand {

	внутрішня допомога по команді;

	публічна NOPECommand(NOPE плагін) {
		супер(плагін);
		help = new HelpSubcommand(plugin, це, 8);
	}

	@Перевизначити
	публічний логічний onCommand(CommandSender відправник, командна команда, рядкова етикетка, String[] args) {
		label = label.toUpperCase();
		якщо (super.onCommand(sender, команда, мітка, арг))
			повернути вірну;
		help.execute(sender, args);
		повернути вірну;
	}

	@Перевизначити
	публічний void enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(нова VLSubcommand(plugin));
		cmds.add(новий ClearSubcommand(plugin));
		cmds.add(новий ReportSubcommand(plugin));
		cmds.add(новий LookupSubcommand(plugin));
		cmds.add(новий ToggleSubcommand(plugin));
		cmds.add(new StatsSubcommand(plugin));
		cmds.add(новий TimeSubcommand(plugin));
		cmds.add(нова BanwaveSubcommand(plugin));
		cmds.add(new ReloadSubcommand(plugin));
		cmds.add(новий ResetSubcommand(plugin));
		cmds.add(новий TestlagSubcommand(plugin));
		cmds.add(новий RemovebanwavwaveSubcommand(плагін));
		cmds.add(новий EnablechecksSubcommand(plugin));
		cmds.add(нова OnlineSubcommand(plugin));
		cmds.add(новий TestAnimationSubcommand(plugin));
		cmds.add(new WarnSubcommand(plugin));
		cmds.add(new ChecksSubcommand(plugin));
		cmds.add(новий TrustSubcommand(plugin));
		cmds.add(допомога);
	}

	@Перевизначити
	публічний рядок getName() {
		return "nope";
	}
}
