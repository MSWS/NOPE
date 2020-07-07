paquete xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importar xyz.msws.nope.NOPE;
importar xyz.msws.nope.commands.sub.BanwaveSubcommand;
importar xyz.msws.nope.commands.sub.ChecksSubcommand;
importar xyz.msws.nope.commands.sub.ClearSubcommand;
importar xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importar xyz.msws.nope.commands.sub.HelpSubcommand;
importar xyz.msws.nope.commands.sub.LookupSubcommand;
importar xyz.msws.nope.commands.sub.OnlineSubcommand;
importar xyz.msws.nope.commands.sub.ReloadSubcommand;
importar xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
importar xyz.msws.nope.commands.sub.ReportSubcommand;
importar xyz.msws.nope.commands.sub.ResetSubcommand;
importar xyz.msws.nope.commands.sub.StatsSubcommand;
importar xyz.msws.nope.commands.sub.TestAnimationSubcommand;
importar xyz.msws.nope.commands.sub.TestlagSubcommand;
importar xyz.msws.nope.commands.sub.TimeSubcommand;
importar xyz.msws.nope.commands.sub.ToggleSubcommand;
importar xyz.msws.nope.commands.sub.TrustSubcommand;
importar xyz.msws.nope.commands.sub.VLSubcomando;
importar xyz.msws.nope.commands.sub.WarnSubcommand;

public class NOPECommand extends AbstractCommand {

	ayuda privada HelpSubcommand

	public NOPECommand(NOPE plugin) {
		super(plugin);
		help = new HelpSubcommand(plugin, this, 8);
	}

	@Sobreescribir
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		label = label.toUpperCase();
		if (super.onCommand(sender, command, label, args))
			retorno verdadero;
		help.execute(sender, args);
		retorno verdadero;
	}

	@Sobreescribir
	public void enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(new VLSubcommand(plugin));
		cmds.add(new Clear Subcommand(plugin));
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

	@Sobreescribir
	public String getName() {
		devolver "no";
	}
}
