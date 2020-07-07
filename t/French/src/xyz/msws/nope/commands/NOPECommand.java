paquet xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

Importer xyz.msws.nope.NOPE ;
Importer xyz.msws.nope.commands.sub.BanwaveSubcommand;
importer xyz.msws.nope.commands.sub.ChecksSubcommand;
importer xyz.msws.nope.commands.sub.ClearSubcommand;
importer xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importer xyz.msws.nope.commands.sub.HelpSubcommand;
importer xyz.msws.nope.commands.sub.LookupSubcommand;
importer xyz.msws.nope.commands.sub.OnlineSubcommand;
importer xyz.msws.nope.commands.sub.ReloadSubcommand;
import xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
import xyz.msws.nope.commands.sub.ReportSubcommand;
importer xyz.msws.nope.commands.sub.ResetSubcommand;
importer xyz.msws.nope.commands.sub.StatsSubcommand;
importer xyz.msws.nope.commands.sub.TestAnimationSubcommand;
import xyz.msws.nope.commands.sub.TestlagSubcommand;
importer xyz.msws.nope.commands.sub.TimeSubcommand;
importer xyz.msws.nope.commands.sub.ToggleSubcommand;
importer xyz.msws.nope.commands.sub.TrustSubcommand;
importer xyz.msws.nope.commands.sub.VLSubcommand;
importer xyz.msws.nope.commands.sub.WarnSubcommand;

la classe publique NOPECommand extends AbstractCommand {

	aide de sous-commande privée ;

	public NOPECommand(plugin NOPE) {
		super(plugin);
		help = new HelpSubcommand(plugin, ceci, 8);
	}

	@Remplacer
	public boolean onCommand(CommandSender sender, Commande de commande, Label de chaîne, String[] args) {
		label = label.toUpperCase();
		if (super.onCommand(sender, commande, label, args))
			retourner vrai;
		help.execute(sender, args);
		retourner vrai;
	}

	@Remplacer
	public void enable() {
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
		cmds.add(aide);
	}

	@Remplacer
	chaîne publique getName() {
		retourner "nope";
	}
}
