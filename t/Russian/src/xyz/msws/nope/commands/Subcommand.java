пакет xyz.msws.nope.command;

импорт java.util.ArrayList;
импорт java.util.List;

импорт javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * Представляет собой подкоманду главной команды. Ожидается каждая субкоманда
 * обрабатывает свою логику внутри. Это может включать в себя подкоманды подсети
 * команды.
 * 
 * @author imodm
 *
 */
публичная абстрактная субкоманда класса {

	защищенный NOPE плагин;

	public Subcommand(NOPE plugin) {
		this.plugin = плагин;
	}

	@Nullable
	публичный список абстрактных<String[]> tabCompletions(отправитель команды);

	публичная абстрактная строка getName();

	публичная абстрактная строка getUsage();

	публичное описание абстрактной строки getDescription();

	публичный список<String> getAliases() {
		вернуть новый ArrayList<>();
	}

	публичная строка getPermission() {
		возврат null;
	}

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
