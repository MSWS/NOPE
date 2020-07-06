balíček xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

importovat xyz.msws.nope.NOPE;
importovat xyz.msws.nope.commands.sub.BanwaveSubcommand;
importovat xyz.msws.nope.commands.sub.ChecksSubcommand;
importovat xyz.msws.nope.commands.sub.ClearSubcommand;
importovat xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
importovat xyz.msws.nope.commands.sub.HelpSubcommand;
importovat xyz.msws.nope.commands.sub.LookupSubcommand;
importovat xyz.msws.nope.commands.sub.OnlineSubcommand;
importovat xyz.msws.nope.commands.sub.ReloadSubcommand;
importovat xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
importovat xyz.msws.nope.commands.sub.ReportSubcommand;
importovat xyz.msws.nope.commands.sub.ResetSubcommand;
importovat xyz.msws.nope.commands.sub.StatsSubcommand;
importovat xyz.msws.nope.commands.sub.TestAnimationSubcommand;
importovat xyz.msws.nope.commands.sub.TestlagSubcommand;
importovat xyz.msws.nope.commands.sub.TimeSubcommand;
importovat xyz.msws.nope.commands.sub.ToggleSubcommand;
importovat xyz.msws.nope.commands.sub.TrustSubcommand;
importovat xyz.msws.nope.commands.sub.VLSubcommand;
importovat xyz.msws.nope.commands.sub.WarnSubcommand;

veřejná třída NOPECommand rozšiřuje AbstractCommand {

	soukromá HelpSubcommand nápověda;

	veřejný NOPECommand(NOPE plugin) {
		super(plugin);
		help = nový HelpSubcommand(plugin, this 8);
	}

	@override
	veřejné boolean onCommand(CommandSender sender, velitelský příkaz, String label, String[] args) {
		štítek = label.toUpperCase();
		Pokud (super.onCommand(ender, příkaz, štítek, náklady))
			zpáteční pravda;
		help.execute(ender, arge);
		zpáteční pravda;
	}

	@override
	public void enable() {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(nový VLSubcommand(plugin));
		cmds.add(nový ClearSubcommand(plugin));
		cmds.add(nová ReportSubcommand(plugin));
		cmds.add(new LookupSubcommand(plugin));
		cmds.add(new ToggleSubcommand(plugin));
		cmds.add(nový StatsSubcommand(plugin));
		cmds.add(nový TimeSubcommand(plugin));
		cmds.add(nový BanwaveSubcommand(plugin));
		cmds.add(nové ReloadSubcommand(plugin));
		cmds.add(nový ResetSubcommand(plugin));
		cmds.add(nový TestlagSubcommand(plugin));
		cmds.add(nový RemovebanwaveSubcommand(plugin));
		cmds.add(nový EnablechecksSubcommand(plugin));
		cmds.add(nový OnlineSubcommand(plugin));
		cmds.add(nové TestAnimationSubcommand(plugin));
		cmds.add(nový WarnSubcommand(plugin));
		cmds.add(nový ChecksSubcommand(plugin));
		cmds.add(new TrustSubcommand(plugin));
		cmds.add(help);
	}

	@override
	public String getName() {
		vrátit „nope“;
	}
}
