pakket xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importeer xyz.msws.nope.NEPE;
importeer xyz.msws.nope.commands.sub.BanwaveSubcommand;
importeer xyz.msws.nope.commands.sub.ChecksSubcommand;
importeer xyz.msws.nope.commands.sub.ClearSubcommand;
importeer xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importeer xyz.msws.nope.commands.sub.HelpSubcommand;
importeer xyz.msws.nope.commands.sub.LookupSubcommand;
importeer xyz.msws.nope.commands.sub.OnlineSubcommand;
importeer xyz.msws.nope.commands.sub.ReloadSubcommand;
importeer xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
importeer xyz.msws.nope.commands.sub.ReportSubcommand;
importeer xyz.msws.nope.commands.sub.ResetSubcommand;
importeer xyz.msws.nope.commands.sub.StatsSubcommand;
importeer xyz.msws.nope.commands.sub.TestAnimationSubcommand;
importeer xyz.msws.nope.commands.sub.TestlagSubcommand;
importeer xyz.msws.nope.commands.sub.TimeSubcommand;
importeer xyz.msws.nope.commands.sub.ToggleSubcommand;
importeer xyz.msws.nope.commands.sub.TrustSubcommand;
importeer xyz.msws.nope.commands.sub.VLSubcommand;
importeer xyz.msws.nope.commands.sub.WarnSubcommand;

publieke klasse NOPECommand breidt AbstractCommand { uit

	Privé HelpSubcommand hulp;

	publieke NOPECommand(NOPE plugin) {
		super(plugin);
		help = nieuwe HelpSubcommand(plugin, dit, 8);
	}

	@Overschrijven
	publieke boolean onCommand(CommandSender afzender, Opdracht commando, String label, String[] args) {
		label = label.toUpperCase();
		if (super.onCommand(afzender, commando, label, args))
			keer waar;
		help.execute(afzender, args);
		keer waar;
	}

	@Overschrijven
	publieke ongeldig enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(new VLSubcommand(plugin));
		cmds.add(new ClearSubcommand(plugin));
		cmds.add(new ReportSubcommand(plugin));
		cmds.add(new LookupSubcommand(plugin));
		cmds.add(new ToggleSubcommand(plugin));
		cmds.add(new StatsSubcommand(plugin));
		cmds.add(new TimeSubcommand(plugin));
		cmds.add(new BanwaveSubcommand(plugin));
		cmds.add(new ReloadSubcommand(plugin));
		cmds.add(new ResetSubcommand(plugin));
		cmds.add(new TestlagSubcommand(plugin));
		cmds.add(new RemovebanwaveSubcommand(plugin));
		cmds.add(new EnablechecksSubcommand(plugin));
		cmds.add(new OnlineSubcommand(plugin));
		cmds.add(new TestAnimationSubcommand(plugin));
		cmds.add(new WarnSubcommand(plugin));
		cmds.add(new ChecksSubcommand(plugin));
		cmds.add(new TrustSubcommand(plugin));
		cmds.add(help);
	}

	@Overschrijven
	publieke tekenreeks getName() {
		return "nope";
	}
}
