paketet xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importera xyz.msws.nope.NOPE;
importera xyz.msws.nope.commands.sub.BanwaveUnderkommando;
importera xyz.msws.nope.commands.sub.ChecksUnderkommando;
importera xyz.msws.nope.commands.sub.ClearUnderkommando;
importera xyz.msws.nope.commands.sub.EnablechecksUnderkommando;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importera xyz.msws.nope.commands.sub.HelpUnderkommando;
importera xyz.msws.nope.commands.sub.LookupUnderkommando;
importera xyz.msws.nope.commands.sub.OnlineUnderkommando;
importera xyz.msws.nope.commands.sub.ReloadUnderkommando;
importera xyz.msws.nope.commands.sub.RemovebanwaveUnderkommando;
importera xyz.msws.nope.commands.sub.ReportUnderkommando;
importera xyz.msws.nope.commands.sub.ResetUnderkommando;
importera xyz.msws.nope.commands.sub.StatsUnderkommando;
importera xyz.msws.nope.commands.sub.TestAnimationUnderkommando;
importera xyz.msws.nope.commands.sub.TestlagUnderkommando;
importera xyz.msws.nope.commands.sub.TimeUnderkommando;
importera xyz.msws.nope.commands.sub.ToggleUnderkommando;
importera xyz.msws.nope.commands.sub.TrustUnderkommando;
importera xyz.msws.nope.commands.sub.VLUnderkommando;
importera xyz.msws.nope.commands.sub.WarnUnderkommando;

offentlig klass NOPECommand utökar AbstractCommand {

	privat HelpSubcommand help

	offentliga NOPECommand(NOPE plugin) {
		super(plugin)
		hjälp = nytt HelpSubcommand(plugin, denna, 8);
	}

	@Åsidosätt
	offentlig boolean onCommand(CommandSender sender, Kommandokommando, Sträng etikett, String[] args) {
		etikett = etikett.toUpperCase();
		om (super.onCommand(avsändare, kommando, etikett, args))
			returnera sant
		help.execute(avsändare, args);
		returnera sant
	}

	@Åsidosätt
	public void enable() {
		super.enable()
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(nytt VLSubcommand(plugin));
		cmds.add(nytt ClearSubcommand(plugin));
		cmds.add(nytt ReportUnderkommando (plugin));
		cmds.add(nytt LookupSubcommand(plugin));
		cmds.add(nytt ToggleUnderkommando (plugin));
		cmds.add(nytt StatsUnderkommando (plugin));
		cmds.add(nytt TimeSubcommand(plugin));
		cmds.add(nytt BanwaveUnderkommando (plugin));
		cmds.add(nytt ReloadUnderkommando (plugin));
		cmds.add(nytt återställningUnderkommando (plugin));
		cmds.add(nytt TestlagSubcommand(plugin));
		cmds.add(nytt RemovebanwaveUnderkommando (plugin));
		cmds.add(nytt EnablechecksUnderkommando (plugin));
		cmds.add(nytt OnlineUnderkommando (plugin));
		cmds.add(nytt TestAnimationUnderkommando (plugin));
		cmds.add(nytt WarnSubcommand(plugin));
		cmds.add(nya ChecksUnderkommando (plugin));
		cmds.add(nytt TrustSubcommand(plugin));
		cmds.add(hjälp);
	}

	@Åsidosätt
	offentlig sträng getName() {
		retur "nope"
	}
}
