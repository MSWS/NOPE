balíček xyz.msws.nope.commands;

importovat xyz.msws.nope.utils.MSG;

/**
 * Představuje výsledek příkazu.
 * 
 * @autor imodm
 *
 */
public enum CommandResult {
	/**
	 * Příkaz byl úspěšně dokončen. Vlastní zpráva o úspěchu by měla být
	 * odesláno.
	 */
	ÚKOLY
	/**
	 * Odesílatel nemá správná oprávnění pro příkaz.
	 */
	Nedošlo k navrácení,
	/**
	 * Chybí argument.
	 */
	Provedení ARGUMENTu,
	/**
	 * Uveden je neplatný argument.
	 */
	Neplatný ARGUMENT,
	/**
	 * Pouze hráč může použít příkaz a odesílatel není jeden.
	 */
	@1 hry,
	/**
	 * Spouštěč neposkytl hráči totéž jako
	 * {@link CommandResult#MISSING_ARGUMENT} ale konkrétnější
	 */
	Jsou vyžadovány
	/**
	 * Došlo k neznámé chybě
	 */
	CHYBA;

	public String getMessage() {
		přepínač (this) {
			INVALID_ARGUMENT:
				vrácení MSG.getString("Command.InvalidArgument", "&cAn neplatný argument byl poskytnut.");
			Případ MISSING_ARGUMENT:
				vrátit MSG.getString("Command.MissingArgument", "&cChybí vám argument.");
			NE_PERMISSION:
				vrátit MSG.getString("Command.NoPermission",
						„&4&l[&c&lNOPE&4&l] &cChybí vám &a%perm% &cpermission.";
			případ PLAYER_ONLY:
				vrátit MSG.getString("Command.PlayerOnly",
						"&cMusíte zadat hráče pro spuštění tohoto příkazu jako konzole.");
			VYŽADUJE případ PLAYER:
				vrátit MSG.getString("Command.SpecifyPlayer", "&cYou musí zadat hráče jako argument.");
			POSUZOVÁNÍ případu:
				návrat "";
			výchozí:
				přerušení;
		}
		vracet "&4An error při provádění příkazu.";
	}
}
