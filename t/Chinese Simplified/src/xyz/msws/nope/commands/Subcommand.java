包 xyz.msws.nope.commands;

导入 java.util.ArrayList;
导入 java.util.List;

导入 javax.annotation.Nullab;

import org.bukkit.command.CommandSender;

导入 xyz.msws.nope.NOPE;

/**
 * 代表一个主命令的子命令。 预计每个子命令
 * 从内部处理逻辑。 这可以包括子命令的子命令
 * 命令。
 * 
 * @author imodm
 *
 */
公共抽象类子命令

	受保护的 NOPE 插件；

	公开Subcommand(NOPE plugin)
		插件 = 插件；
	}

	@Nullable
	公开摘要列表<String[]> tabCompltions(Commander Sender)；

	公开摘要字符串GetName();

	公共摘要字符串getUsage();

	公开摘要字符串getDescription();

	公开列表<String> getAliases()
		返回新的数组列表<>();
	}

	public String getPermission()
		返回 null;
	}

	公共抽象命令执行(Commander Sender, String[]args)；
}
