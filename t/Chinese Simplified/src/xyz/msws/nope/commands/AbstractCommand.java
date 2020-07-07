包 xyz.msws.nope.commands;

导入 java.util.ArrayList;
导入 java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

导入 xyz.msws.nope.NOPE;
导入 xyz.msws.nope.modules.AbstractModule;
导入 xyz.msws.nope.utils.MSG;

公共抽象类的摘要命令扩展摘要模块实现指挥官，TabCompleter v.

	受保护的列表<Subcommand> cmds = 新的数组列表<>();

	公开的 AbstractCommand(NOPE plugin)
		super(插件)；
	}

	公开摘要字符串GetName();

	@覆盖
	公开无效启用()
		PluginCommand cmd = plugin.getCommand(getName() );
		cmd.setExecutor(这是);
		cmd.setTabCompleter (this);
	}

	public invoice disabl()
		PluginCommand cmd = plugin.getCommand(getName() );
		cmd.setExecutor(null);
		cmd.setTabComplter(null)；
	}

	公开列表<Subcommand> getSubCommands()
		返回cmds；
	}

	@覆盖
	Public boolian onCommander(Commander Sender, Command Command, String label, String[]args)
		if (args.length < 1)
			返回 false;
		字符串 cmd = args[0];
		for (Subcommand c : cmds)
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase()))
				if (c.getPermission) != null && !sender.hasPermission(c.getPermission()))
					MSG.tell(发件人)
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &c你缺少&a%perm% &cpermission。")
									.replace("%perm%", c.getPermission());
					返回真；
				}
				CommandResults = c.execute(发送者、args)；
				if (results == CommandResult.SUCCESS)
					返回真；
				if (results == CommandResult.NO_PERMISSION)
					MSG.tell(发件人)
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &c你缺少&a%perm% &cpermission。")
									.replace("%perm%", c.getPermission());
					返回真；
				}
				MSG.tell(发件人, "&4" + 标签+ " + c.getName() 的&cProper使用量);
				MSG.tell(发送者, "&7/" + 标签+ " + c.getName() + " + " + c.getUsage() );
				MSG.tell(发送者，结果.getMessage())；
				返回真；
			}
		}
		返回 false;
	}

	@覆盖
	公开列表<String> onTabComplete(Commander Sender, Command Command, String label, String[args)
		列表<String> 结果 = 新的数组列表<>();

		for (Subcommand sub-b: cmds)
			邮件列表<String> 别名 = sub.getAliases();
			别名.add(sub.getName() );
			邮件列表<String[]> 补全 = sub.tabCompltions(发送者)；
			if (args.length > 1) {
				if (compltions == null || completitions.isEmpty())
					继续；
				如果(completitions.size) < args.length - 1)
					继续；
				如果(!aliases.contains(args[0].toLowerCase()))
					继续；
				字符串[] res = complettions.get(args.length - 2)；
				if (res == null)
					继续；
				适合(String r : )
					如果(r.toLowerCase().startsWid(args[args.length - 1].toLowerCase()))
						结果添加(r)；
				继续；
			}
			for (String alias : aliases)
				如果(alias.toLowerCase().startsWid(args[args.length - 1].toLowerCase()))
					结果添加(别名)；
			}
		}

		返回 result.isEmpty() ? 空：结果;
	}

}
