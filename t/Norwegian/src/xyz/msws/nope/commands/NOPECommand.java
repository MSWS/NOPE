pakke xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importer xyz.msws.nope.NOPE;
importer xyz.msws.nope.commands.sub.BanwaveSubcommand;
Importer xyz.msws.nope.commands.sub.ChecksSubcommand;
Importer xyz.msws.nope.commands.sub.Clearsubcommand;
Importer xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
Importer xyz.msws.nope.commands.sub.HelpSubcommand;
importer xyz.msws.nope.commands.sub.Lookupsubcommand;
Importer xyz.msws.nope.commands.sub.OnlineSubcommand;
importer xyz.msws.nope.commands.sub.Reloadsubkommando;
importer xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
importer xyz.msws.nope.commands.sub.ReportSubcommand;
Importer xyz.msws.nope.commands.sub.ResetSubcommand;
importere xyz.msws.nope.commands.sub.StatsSubcommand;
importer xyz.msws.nope.commands.sub.TestAnimationUnderkommando;
importer xyz.msws.nope.commands.sub.Testlagsubkommando;
importer xyz.msws.nope.commands.sub.TimeSubcommand;
Importer xyz.msws.nope.commands.sub.ToggleSubcommand;
importer xyz.msws.nope.commands.sub.Trustsubkommando;
Importer xyz.msws.nope.commands.sub.VLSubcommand;
Importer xyz.msws.nope.commands.sub.Warnsubkommando;

offentlig klasse NOPECommand utvider AbstractCommand {

	privat hjelp hjelp av underkommandoen;

	offentlig NOPECommand(NOPE plugin) {
		super(plugin);
		hjelp = ny HelpSubcommand(plugin, dette, 8);
	}

	@Overstyring
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		etikett = label.toUpperCase();
		hvis (super.onCommand(sender, kommando, args))
			retursann;
		"help.execute(sender, args);
		retursann;
	}

	@Overstyring
	offentlig aktivering() {
		over.aktiver();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(ny VLSubcommand(plugin));
		cmds.add(ny ClearSubcommand(plugin));
		cmds.add(ny ReportUndercommand(plugin));
		cmds.add(ny LookupSubcommand(plugin));
		cmds.add(ny ToggleSubcommand(plugin));
		cmds.add(ny StatsSubcommand(plugin));
		cmds.add(ny TimeSubcommand(plugin));
		cmds.add(ny BanwaveSubcommand(plugin));
		cmds.add(ny ReloadSubcommand(plugin));
		cmds.add(ny ResetSubcommand(plugin));
		cmds.add(ny TestlagSubcommand(plugin));
		cmds.add(ny RemovebanwaveSubcommand(plugin));
		cmds.add(ny EnablechecksSubcommand(plugin));
		cmds.add(ny OnlineSubcommand(plugin));
		cmds.add(ny TestAnimationSubcommand(plugin));
		cmds.add(ny WarnSubcommand(plugin));
		cmds.add(ny ChecksSubcommand(plugin));
		cmds.add(ny TrustSubcommand(plugin));
		cmds.add(hjelp);
	}

	@Overstyring
	public String getName() {
		returner "uavn";
	}
}
