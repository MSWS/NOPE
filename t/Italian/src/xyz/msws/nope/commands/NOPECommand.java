pacchetto xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;
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

public class NOPECommand extends AbstractCommand {

	aiuto privato HelpSubcommand;

	public NOPECommand(NOPE plugin) {
		super(plugin);
		help = nuovo HelpSubcommand(plugin, questo, 8);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		etichetta = etichetta.toUpperCase();
		if (super.onCommand(sender, command, label, args))
			return true;
		help.execute(sender, args);
		return true;
	}

	@Override
	public void enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(nuovo VLSubcommand(plugin));
		cmds.add(new ClearSubcommand(plugin));
		cmds.add(new ReportSubcommand(plugin));
		cmds.add(new LookupSubcommand(plugin));
		cmds.add(new ToggleSubcommand(plugin));
		cmds.add(new StatsSubcommand(plugin));
		cmds.add(new TimeSubcommand(plugin));
		cmds.add(nuovo BanwaveSubcommand(plugin));
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

	@Override
	public String getName() {
		restituisce "nope";
	}
}
