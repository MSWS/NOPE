包 xyz.msws.nope.commands;

导入 xyz.msws.nope.utils.MSG;

/**
 * 代表命令的结果。
 * 
 * @author imodm
 *
 */
公开编号CommandResults
	/**
	 * 命令已成功完成。 自定义成功消息应该是
	 * 已发送。
	 */
	订阅：
	/**
	 * 发件人对命令没有适当的权限。
	 */
	NO_PERMISSSION
	/**
	 * 缺少一个参数。
	 */
	MISSION_ARGUMENT,
	/**
	 * 给出的参数无效。
	 */
	INVALID_ARGUMENT,
	/**
	 * 只有玩家可以使用命令，而发件人不是。
	 */
	PLAYER_ONLY,
	/**
	 * 执行者没有给玩家相同的
	 * {@link CommandResult#MISSIG_ARGUMENT} 但更加具体。
	 */
	PLAYER_REQUIRED,
	/**
	 * 发生未知错误
	 */
	错误；

	public String getMessage()
		切换(这个)
			大小写：
				返回 MSG.getString("Command.InvalidArgument", "&c提供了无效的参数");
			大小写：
				返回 MSG.getString("Command.MissingArgument", "&cYou 缺少一个参数");
			案例NO_PERMISSION：
				返回 MSG.getString("Command.NoPermission")，
						"&4&l[&c&lNOPE&4&l] &c你缺少&a%perm% &cpermission.");
			PLAYER_ONLY：
				返回 MSG.getString("Command.PlayerOnly")，
						"&c你必须指定一个玩家作为控制台运行此命令。");
			PLAYER_REQUIRED：
				返回 MSG.getString("Command.SpecifyPlayer", "&c你必须指定一个玩家作为参数");
			客户反馈：
				返回 "";
			默认值：
				2. 间歇；
		}
		返回 "&4在执行命令时发生错误";
	}
}
