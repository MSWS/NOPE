パッケージ xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

/**
 * コマンドの結果を表します。
 * 
 * @author imodm
 *
 */
public enum CommandResult {
	/**
	 * コマンドは正常に完了しました。 カスタム成功メッセージは
	 * 送信済み
	 */
	成功しました。
	/**
	 * 送信者にはコマンドに対する適切な権限がありません。
	 */
	アクセス許可がありません。
	/**
	 * 引数がありません。
	 */
	アルゴリズムがありません。
	/**
	 * 無効な引数が与えられています。
	 */
	無効な引数
	/**
	 * プレイヤーのみがコマンドを使用でき、送信者は1つではありません。
	 */
	プレイヤーのみ
	/**
	 * 実行者はプレイヤーを与えませんでした。
	 * {@link CommandResult#MISSING_ARGUMENT} ですが、もっと具体的に
	 */
	必要なプレイヤー:
	/**
	 * 不明なエラーが発生しました
	 */
	エラー;

	public String getMessage() {
		switch (this) {
			case INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cAn invalid argument");
			case MISSIING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cYou are missing an argent.");
			case NOMISSION:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cあなたは &a%perm% &cpermission.");
			case Playerのみ:
				return MSG.getString("Command.PlayerOnly",
						"&cこのコマンドを実行するプレイヤーをコンソールとして指定する必要があります。
			case PLAYER_REQUIRED:
				return MSG.getString("Command.SpecifyPlayer", "&cプレイヤーを引数として指定する必要があります。
			caseSuccess:
				return "";
			デフォルト:
				休憩;
		}
		return "&4コマンド実行中にエラーが発生しました。
	}
}
