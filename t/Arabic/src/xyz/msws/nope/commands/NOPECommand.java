حزمة xyz.msws.nope.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

استيراد xyz.msws.nope.NOPE؛
استيراد xyz.msws.nope.commands.sub.BanwaveSubcommand؛
استيراد xyz.msws.nope.commands.sub.ChecksSubcommand؛
استيراد xyz.msws.nope.commands.sub.clearSubcommand؛
استيراد xyz.msws.nope.commands.sub.EnablechecksSubcommand؛
import xyz.msws.nope.commands.sub.FalseSubcommand;
استيراد xyz.msws.nope.commands.sub.HelpSubcommand؛
استيراد xyz.msws.nope.commands.sub.LookupSubcommand؛
استيراد xyz.msws.nope.commands.sub.OnlineSubcommand؛
استيراد xyz.msws.nope.commands.sub.ReloadSubcommand؛
استيراد xyz.msws.nope.commands.sub.RemovebanwaveSubcommand؛
استيراد xyz.msws.nope.commands.sub.ReportSubcommand؛
استيراد xyz.msws.nope.commands.sub.ResetSubcommand؛
استيراد xyz.msws.nope.commands.sub.StatsSubcommand؛
استيراد xyz.msws.nope.commands.sub.TestAnimationSubcommand؛
استيراد xyz.msws.nope.commands.sub.TestlagSubcommand؛
استيراد xyz.msws.nope.commands.sub.TimeSubcommand؛
استيراد xyz.msws.nope.commands.sub.ToggleSubcommand؛
استيراد xyz.msws.nope.commands.sub.TrustSubcommand؛
استيراد xyz.msws.nope.commands.sub.VLSubcommand؛
استيراد xyz.msws.nope.commands.sub.WarnSubcommand؛

الطبقة العامة NOPECommand توسع AbstractCommand {

	مساعدة خاصة في الأمر المساعد؛

	الجمهور NOPEFAE (إضافة NOPE) {
		فائق (إضافة)؛
		المساعدة = مساعدة جديدة (الملحق، هذا 8)؛
	}

	@تجاوز
	علبة عامة على القائد (قائد المرسل، أمر الأوامر ، تسمية String ، String[] args) {
		تسمية = label.toUpperCase();
		إذا (super.oncommand(sender, command, label, args))
			إرجاع صحيح؛
		help.execute(sender, arg);
		إرجاع صحيح؛
	}

	@تجاوز
	الفراغ العام تمكين () {
		super.enable();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(VLSubcommand(plugin));
		cmds.add(clearSubcommand(plugin))؛
		cmds.add(جديد ReportSubcommand(plugin))؛
		cmds.add(جديد LookupSubcommand(plugin))؛
		cmds.add(جديد ToggleSubcommand(plugin))؛
		cmds.add(New StatsSubcommand(plugin))؛
		cmds.add(TimeSubcommand(plugin));
		cmds.add(جديد BanwaveSubcommand(plugin))؛
		cmds.add(جديد ReloadSubcommand(plugin))؛
		cmds.add(ReetSubcommand(plugin));
		cmds.add(جديد TestlagSubcommand(plugin))؛
		cmds.add(new RemovebanwaveSubcommand(plugin))؛
		cmds.add(جديد EnablechecksSubcommand(plugin))؛
		cmds.add(OnlineSubcommand(plugin))؛
		cmds.add(TestAnimationSubcommand(plugin))؛
		cmds.add(WarnSubcommand(plugin))؛
		cmds.add(ChecksSubcommand(plugin))؛
		cmds.add(TrustSubcommand(plugin))؛
		cmds.add(help)؛
	}

	@تجاوز
	المقطع العام getName() {
		إرجاع "nope";
	}
}
