Paket xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importieren xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.sub.BanwaveSubcommand;
import xyz.msws.nope.commands.sub.ChecksSubcommand;
import xyz.msws.nope.commands.sub.ClearSubcommand;
import xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
import xyz.msws.nope.commands.sub.HelpSubcommand;
import xyz.msws.nope.commands.sub.LookupSubcommand;
import xyz.msws.nope.commands.sub.OnlineSubcommand;
import xyz.msws.nope.commands.sub.ReloadSubcommand;
import xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
import xyz.msws.nope.commands.sub.ReportSubcommand;
import xyz.msws.nope.commands.sub.ResetSubcommand;
import xyz.msws.nope.commands.sub.StatsSubcommand;
import xyz.msws.nope.commands.sub.TestAnimationSubcommand;
import xyz.msws.nope.commands.sub.TestlagSubcommand;
import xyz.msws.nope.commands.sub.TimeSubcommand;
import xyz.msws.nope.commands.sub.ToggleSubcommand;
import xyz.msws.nope.commands.sub.TrustSubcommand;
import xyz.msws.nope.commands.sub.VLSubcommand;
import xyz.msws.nope.commands.sub.WarnSubcommand;

public class NOPECommand erweitert AbstractCommand {

	private HelpSubcommand Hilfe;

	öffentliches NOPECommand(NOPE-Plugin) {
		super(plugin);
		help = neues HelpSubcommand(plugin, this, 8);
	}

	@Überschreiben
	public boolean onCommand(CommandSender sender, Befehlsbefehl, String[] args) {
		label = label.toUpperCase();
		if (super.onCommand(sender, Befehl, Beschriftung, args))
			kehre wahr zurück;
		help.execute(Absender, Args);
		kehre wahr zurück;
	}

	@Überschreiben
	public void enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(neues VLSubcommand(plugin));
		cmds.add(new ClearSubcommand(plugin));
		cmds.add(new ReportSubcommand(plugin));
		cmds.add(neues LookupSubcommand(plugin));
		cmds.add(neues ToggleSubcommand(plugin));
		cmds.add(new StatsSubcommand(plugin));
		cmds.add(neues TimeSubcommand(plugin));
		cmds.add(neues BanwaveSubcommand(plugin));
		cmds.add(neues ReloadSubcommand(plugin));
		cmds.add(new ResetSubcommand(plugin));
		cmds.add(new TestlagSubcommand(plugin));
		cmds.add(neues RemovebanwaveSubcommand(plugin));
		cmds.add(new EnablechecksSubcommand(plugin));
		cmds.add(new OnlineSubcommand(plugin));
		cmds.add(neues TestAnimationSubcommand(plugin));
		cmds.add(new WarnSubcommand(plugin));
		cmds.add(new ChecksSubcommand(plugin));
		cmds.add(neues TrustSubcommand(plugin));
		cmds.add(help);
	}

	@Überschreiben
	öffentliche String getName() {
		return "nope";
	}
}
