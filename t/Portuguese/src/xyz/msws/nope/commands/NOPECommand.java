pacote xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importar xyz.msws.nope.NOPE;
importar xyz.msws.nope.commands.sub.BanwaveSubcomando;
importar xyz.msws.nope.commands.sub.ChecksSubcomando;
importar xyz.msws.nope.commands.sub.ClearSubcomman;
importar xyz.msws.nope.commands.sub.EnablechecksSubcomando;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importar xyz.msws.nope.commands.sub.HelpSubcomman;
importar xyz.msws.nope.commands.sub.LookupSubcomando;
importar xyz.msws.nope.commands.sub.OnlineSubcomando;
importar xyz.msws.nope.commands.sub.ReloadSubcomando;
importar xyz.msws.nope.commands.sub.RemovebanwaveSubcomando;
importar xyz.msws.nope.commands.sub.ReportSubcomando;
importar xyz.msws.nope.commands.sub.ResetSubcomando;
importar xyz.msws.nope.commands.sub.StatsSubcomando;
import xyz.msws.nope.commands.sub.TestAnimationSubcommando;
import xyz.msws.nope.commands.sub.TestlagSubcomman;
importar xyz.msws.nope.commands.sub.TimeSubcomman;
importar xyz.msws.nope.commands.sub.ToggleSubcomando;
importar xyz.msws.nope.commands.sub.TrustSubcomando;
importar xyz.msws.nope.commands.sub.VLSubcomando;
importar xyz.msws.nope.commands.sub.WarnSubcomando;

classe pública NOPECommand estende o AbstractCommand {

	ajuda de Ajuda privada;

	public NOPECommand(plugin NÓPE) {
		super(plugin);
		help = new HelpSubcommand(plugin, this 8);
	}

	Ignorar
	booleano público onCommand(CommandSender sender, comando de comando, nome de string, String[] args) {
		etiqueta = label.toUpperCase();
		if (super.onCommand(sender, comando, rotulo, args))
			retornar verdadeiro;
		help.execute(sender, args);
		retornar verdadeiro;
	}

	Ignorar
	void void habilitado() {
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

	Ignorar
	public String getName() {
		retorne "nulo";
	}
}
