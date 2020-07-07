Pakiet xyz.msws.nope.commands;

importuj java.util.ArrayList;
importuj java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * Przedstawia podpolecenie głównego polecenia. Oczekiwane jest każde podpolecenie
 * wewnętrznie zajmuje się logiką. Może to obejmować posiadanie podzleceń podrzędnych
 * komendy.
 * 
 * @author imodm
 *
 */
publiczne abstrakcyjne podpolecenie {

	chroniona wtyczka NOPE;

	publiczne podpolecenie (NOPE plugin) {
		this.plugin = wtyczka;
	}

	@Nullable
	publiczna lista abstrakcyjna<String[]> tabCompletions(CommandSender nadawca);

	abstrakcyjny publiczny „String getName();

	abstrakcyjny String getUsage();

	Publiczny abstrakcyjny String getDescription();

	lista publiczna<String> getAliases() {
		zwróć nową ArrayList<>();
	}

	publiczny ciąg getPermission() {
		Zwrot nul;
	}

	publiczne polecenie wykonania (CommandSender sender, String[] args);
}
