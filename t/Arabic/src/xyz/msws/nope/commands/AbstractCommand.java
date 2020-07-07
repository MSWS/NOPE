حزمة xyz.msws.nope.command;

استيراد java.util.ArrayList؛
استيراد java.util.List؛

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

استيراد xyz.msws.nope.NOPE؛
استيراد xyz.msws.nope.modules.AbstractModule؛
استيراد xyz.msws.nope.utils.MSG؛

يوسع صف تجريدي عام AbstractCommand نطاق تطبيق APstractModule لأجهزة القائد التنفيذي، TabCompleter {

	القائمة المحمية<Subcommand> cmds = قائمة مصفوفة جديدة<>();

	الملخص العام (NOPE plugin) {
		فائق (إضافة)؛
	}

	ملخص عام getName();

	@تجاوز
	الفراغ العام تمكين () {
		pluginCommand cmd = plugin.getcommand(getName())؛
		cmd.setExecutor(هذا)؛
		cmd.setTabCompleter(هذا)؛
	}

	تعطيل الفراغ العام() {
		pluginCommand cmd = plugin.getcommand(getName())؛
		cmd.setExecutor(null);
		cmd.setTabCompleter(null)؛
	}

	القائمة العامة<Subcommand> getSubcommands() {
		إرجاع سمد;
	}

	@تجاوز
	علبة عامة على القائد (قائد المرسل، أمر الأوامر ، تسمية String ، String[] args) {
		if (args.length < 1)
			إرجاع خاطئ؛
		السلسلة cmd = الأبراج[0]؛
		ل (Subcommand c : cmds) {
			إذا (c.getName().equalsIgnoreCase(cmd) <unk> <unk> c.getAliases().contains(cmd.toLowerCase())) {
				إذا (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(المرسل)،
							MSG.getString("command.Nopermission",
									"&4&l[&c&lNOPE&4&l] &cأنت تفتقر إلى &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission())؛
					إرجاع صحيح؛
				}
				نتيجة القيادة = c.execute(sender, arg);
				إذا (نتيجة == commandult.SUCCESS)
					إرجاع صحيح؛
				إذا (result == commandult.NO_PERMISSION) {
					MSG.tell(المرسل)،
							MSG.getString("command.Nopermission",
									"&4&l[&c&lNOPE&4&l] &cأنت تفتقر إلى &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission())؛
					إرجاع صحيح؛
				}
				MSG.tell(المرسل، "&4" + التسمية + " > &cProper الاستخدام ل " + c.getName())؛
				MSG.tell(sender, "&7/" + label + " + c.getName() + " + c.getUsage())؛
				MSG.tell(sender, result.getMessage())؛
				إرجاع صحيح؛
			}
		}
		إرجاع خاطئ؛
	}

	@تجاوز
	القائمة العامة<String> onTabComplete(القائد المرسل وأمر الأوامر وعلامة String وString[] args) {
		قائمة<String> نتيجة = قائمة جديدة <>();

		ل (Subcommand subb : cmds) {
			قائمة<String> أسماء مستعارة = sub.getAliases();
			أسماء مستعارة.add(sub.getName())؛
			قائمة<String[]> تكملة = sub.tabCompletions(sender)؛
			if (args.length > 1) {
				إذا (المكملات == null <unk> completions.isEmpty())
					مواصلة؛
				إذا (completions.size() < args.length - 1)
					مواصلة؛
				إذا (!aliases.contains(args[0].toLowerCase()))
					مواصلة؛
				String[] res = completions.get(args.length - 2)؛
				إذا (res == null)
					مواصلة؛
				لـ (String r : res)
					إذا (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r)؛
				مواصلة؛
			}
			لـ (الاسم المستعار: أسماء مستعارة) {
				إذا (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(مستعار)؛
			}
		}

		إرجاع result.isEmpty() ? لاغ: النتيجة؛
	}

}
