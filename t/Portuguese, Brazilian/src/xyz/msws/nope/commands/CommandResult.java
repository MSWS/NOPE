pacote xyz.msws.nope.commands;

importar xyz.msws.nope.utils.MSG;

/**
 * Representa o resultado de um comando.
 * 
 * Iodm @autor
 *
 */
public enum CommandResultult {
	/**
	 * O comando foi concluído com sucesso. Uma mensagem de sucesso personalizada deve ser
	 * enviado.
	 */
	SUCESSO,
	/**
	 * O remetente não tem as permissões adequadas para o comando.
	 */
	NO_PERMISSION,
	/**
	 * Está faltando um argumento.
	 */
	PLAYLIST_DESCRIPTION_DESCRIPTION
	/**
	 * Um argumento inválido é dado.
	 */
	INVALID_ARGUMENT,
	/**
	 * Somente um jogador pode usar o comando e o remetente não é.
	 */
	PLAYER_APLIS,
	/**
	 * O executor não deu um jogador, o mesmo que
	 * {@link CommandResult#MISSING_ARGUMENT} mas mais específico
	 */
	PLAYER_REINTROD_TOOLTIP_TEXT
	/**
	 * Ocorreu um erro desconhecido
	 */
	ERRO;

	public String getMessage() {
		switch (isto) {
			caso INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cUm argumento inválido foi fornecido.");
			caso MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cVocê está faltando um argumento.");
			caso NO_PERMISSION:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cVocê não possui a &a permissão de%perm%.");
			case PLAYER_ONLY:
				return MSG.getString("Command.PlayerOnly",
						&cVocê precisa especificar um jogador para executar este comando como console.");
			case PLAYER_REQUIRED:
				return MSG.getString("Command.SpecifyPlayer", "&cVocê precisa especificar um jogador como um argumento.");
			case SUCCESS:
				retornar "";
			padrão:
				quebrar;
		}
		retorne "&4Um erro ocorreu durante a execução do comando";
	}
}
