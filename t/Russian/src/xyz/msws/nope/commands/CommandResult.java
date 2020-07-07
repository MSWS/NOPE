пакет xyz.msws.nope.command;

import xyz.msws.nope.utils.MSG;

/**
 * Представляет собой результат команды.
 * 
 * @author imodm
 *
 */
public enum CommandResult {
	/**
	 * Команда успешно выполнена. Пользовательское сообщение об успехе должно быть
	 * отправлено.
	 */
	УСПЕШНО
	/**
	 * Отправитель не имеет соответствующих прав для этой команды.
	 */
	NO_PERMISSION,
	/**
	 * Аргумент отсутствует.
	 */
	MISSING_ARGUMENT,
	/**
	 * Указан неверный аргумент.
	 */
	НЕВЕРНОЙ_ARGUMENT,
	/**
	 * Только игрок может использовать команду, и отправитель не один.
	 */
	ТОЛЬКО PLAYER,
	/**
	 * Исполнитель не дал игроку, так же, как
	 * {@link CommandResult#MISSING_ARGUMENT}, но более специфичный
	 */
	PLAYER_PLAYER_REQUIRED
	/**
	 * Произошла неизвестная ошибка
	 */
	Ошибка;

	публичная строка getMessage() {
		сменить (это) {
			НЕВЕРНОЙ_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cAn invalid argument was provided.");
			случай MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cYou are missing an argument.");
			регистр NO_PERMISSION:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cВы не имеете &a%perm% &cpermission.");
			регистр PLAYER_ТОЛЬКО
				возвращать MSG.getString("Command.PlayerOnly",
						"&cВы должны указать игрока, чтобы запустить эту команду как консоль.");
			регистр PLAYER_REQUIRED:
				возвращать MSG.getString("Command.SpecifyPlayer", "&cYou должен указать игрока в качестве аргумента.");
			регистр УСПЕШС:
				вернуть "";
			по умолчанию:
				разрыва;
		}
		return "&4Произошла ошибка при выполнении команды.";
	}
}
