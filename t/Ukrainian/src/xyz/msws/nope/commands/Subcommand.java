пакет xyz.msw.nope.command;

імпорт jav.util.ArrayList;
імпорт java.util.List;

імпортувати javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

імпортувати xyz.msws.nope;

/**
 * Представляє собою підкоманду головної команди. Очікується, що кожна субкоманда
 * Всередину справляється зі своєю логікою. Це може включати в себе субкоманди
 * команди.
 * 
 * @автор імодм
 *
 */
абстрактний клас {

	плагін NOPE захищено;

	публічна Subcommand(NOPE плагін) {
		this.plugin = плагін;
	}

	@Подальша
	публічний абстрактний список<String[]> tabtions(CommandSender відправника);

	публічний абстрактний рядок getName();

	публічний абстрактний рядок getUsage();

	публічний абстрактний рядок getDescription();

	публічний список<String> getAlias() {
		повернути новий ArrayList<>();
	}

	публічний рядок getPermission() {
		повернути нуль;
	}

	публічний абстрактний виконуваний файл CommandSender відправник, String[] args);
}
