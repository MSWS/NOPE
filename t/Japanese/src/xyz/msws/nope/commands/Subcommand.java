パッケージ xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * メインコマンドのサブコマンドを表します。 それぞれのサブコマンドが期待されています
 * 内部でロジックを処理します。 サブコマンドを含めることができます
 * コマンド
 * 
 * @author imodm
 *
 */
public abstract class Subcommand {

	保護された NOPE プラグイン;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	public abstract List<String[]> tabCompletions(CommandSender sender);

	public abstract String getName();

	public abstract String getUsage();

	public abstract String getDescription();

	public List<String> getAliases() {
		return new ArrayList<>();
	}

	public String getPermission() {
		return null;
	}

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
