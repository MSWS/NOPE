Pakiet xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
importuj xyz.msws.nope.commands.sub.BanwaveSubcommand;
importuj xyz.msws.nope.commands.sub.ChecksSubcommand;
importuj xyz.msws.nope.commands.sub.ClearSubcommand;
importuj xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importuj xyz.msws.nope.commands.sub.HelpSubcommand;
importuj xyz.msws.nope.commands.sub.LookupSubcommand;
importuj xyz.msws.nope.commands.sub.OnlineSubcommand;
importuj xyz.msws.nope.commands.sub.ReloadSubcommand;
importuj xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
importuj xyz.msws.nope.commands.sub.ReportSubcommand;
importuj xyz.msws.nope.commands.sub.ResetSubcommand;
importuj xyz.msws.nope.commands.sub.StatsSubcommand;
importuj xyz.msws.nope.commands.sub.TestAnimationSubcommand;
importuj xyz.msws.nope.commands.sub.TestlagSubcommand;
importuj xyz.msws.nope.commands.sub.TimeSubcommand;
importuj xyz.msws.nope.commands.sub.ToggleSubcommand;
importuj xyz.msws.nope.commands.sub.TrustSubcommand;
importuj xyz.msws.nope.commands.sub.VLSubcommand;
importuj xyz.msws.nope.commands.sub.WarnSubcommand;

klasa publiczna NOPECommand rozszerza AbstractCommand {

	prywatna pomoc PomocPodPolecenia;

	publiczny NOPECommand(NOPE plugin) {
		super(wtyczka);
		pomoc = nowa HelpSubcommand(plugin, This 8);
	}

	@Nadpisz
	Publiczny boolean onCommand(CommandSender sender, Command Command, String label, String[] args) {
		etykieta = etykieta.toperCase();
		Jeśli (super.onCommand(sender, polecenie, etykieta, args))
			zwróć prawdę;
		pomoc (nadawca, arg);
		zwróć prawdę;
	}

	@Nadpisz
	włączono ubytek () {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(nowy VLSubcommand(plugin));
		cmds.add(nowy ClearSubcommand(plugin));
		cmds.add(nowy ReportSubcommand(plugin));
		cmds.add(nowa wtyczka LookupSubcommand);
		cmds.add(new ToggleSubcommand(plugin));
		cmds.add(nowy StatsSubcommand(plugin));
		cmds.add(nowy TimeSubcommand(plugin));
		cmds.add(nowy BanwaveSubcommand(plugin));
		cmds.add(nowy ReloadSubcommand(plugin));
		cmds.add(nowe polecenie ResetSubcommand(plugin));
		cmds.add(nowy TestlagSubcommand(plugin));
		cmds.add(nowy RemovebanwaveSubcommand(plugin));
		cmds.add(nowy EnablechecksSubcommand(plugin));
		cmds.add(nowy OnlineSubcommand(plugin));
		cmds.add(nowe TestAnimationSubcommand(plugin));
		cmds.add(nowe polecenie WarnSubComm(plugin));
		cmds.add(nowe ChecksSubcommand(plugin));
		cmds.add(nowy TrustSubcommand(plugin));
		cmds.add(pomoc);
	}

	@Nadpisz
	Publiczny ciąg getName() {
		Powrót „nope”;
	}
}
