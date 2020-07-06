包 xyz.msws.nope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

导入 xyz.msws.nope.NOPE;
导入 xyz.msws.nope.commands.sub.BanwaveSubcommand;
导入 xyz.msws.nope.commands.sub.checksSubcommand;
导入 xyz.msws.nope.commands.subClearSubcommand;
导入 xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.FalseSubcommand;
导入 xyz.msws.nope.commands.sub.HelpSubcommand;
导入 xyz.msws.nope.commands.sub.LookupSubcommand;
导入 xyz.msws.nope.commands.sub.OnlineSubcommand;
导入 xyz.msws.nope.commands.sub.ReloadSubcommand;
导入 xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
导入 xyz.msws.nope.commands.sub.ReportSubcommand;
导入 xyz.msws.nope.commands.sub.ResetSubcommand;
导入 xyz.msws.nope.commands.sub.StatsSubcommand;
导入 xyz.msws.nope.commands.sub.TestAnimationSubcommand;
导入 xyz.msws.nope.commands.sub.TestlagSubcommand;
导入 xyz.msws.nope.commands.sub.TimeSubcommand;
导入 xyz.msws.nope.commands.sub.ToggleSubcommand;
导入 xyz.msws.nope.commands.sub.TrustSubcommand;
导入 xyz.msws.nope.commands.sub.VLSubcommand;
导入 xyz.msws.nope.commands.sub.WarnSubcommand;

公共课堂NOPECommand exts AbstractCommand

	私有HelpSub命令帮助；

	公开NOPECommand(NOPE plugin)
		super(插件)；
		help = new HelpSubcommand(plugin, this 8);
	}

	@覆盖
	Public boolian onCommander(Commander Sender, Command Command, String label, String[]args)
		label = label.toUpperCase();
		如果(super.onCommand(发送者、命令、标签、args))
			返回真；
		help.execute(发送者、参数)；
		返回真；
	}

	@覆盖
	公开无效启用()
		启用();
		cmds.add(new FalseSubcommand(plugin));
		cmds.add(新 VLSubcommand(plugin));
		cmds.add(新 ClearSubcommand(plugin));
		cmds.add(新 ReportSubcommand(plugin));
		cmds.add(新 LookupSubcommand(plugin));
		cmds.add(新 ToggleSubcommand(plugin));
		cmds.add(新 StatsSubcommand(plugin));
		cmds.add(新 TimeSubcommand(plugin));
		cmds.add(新 BanwaveSubcommand(plugin));
		cmds.add(新 ReloadSubcommand(plugin));
		cmds.add(新 ResetSubcommand(plugin));
		cmds.add(新 TestlagSubcommand(plugin));
		cmds.add(新 RemovebanwaveSubcommand(plugin));
		cmds.add(新 EnablechecksSubcommand(plugin));
		cmds.add(新 OnlineSubcommand(plugin));
		cmds.add(新 TestAnimationSubcommand(plugin));
		cmds.add(新 WarnSubcommand(plugin));
		cmds.add(新 ChecksSubcommand(plugin));
		cmds.add(新 TrustSubcommand(plugin));
		cmds.add(帮助)；
	}

	@覆盖
	public String getName()
		返回 "nope";
	}
}
