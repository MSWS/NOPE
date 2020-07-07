Paket xyz.msws.nope.commands;

importiere java.util.ArrayList;
importiere java.util.List;

importiere javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importieren xyz.msws.nope.NOPE;

/**
 * Stellt einen Unterbefehl eines Hauptkommandos dar. Es wird von jedem Unterbefehl erwartet
 * behandelt seine Logik intern. Dies kann Unterbefehle von Unterbefehle beinhalten
 * Befehle.
 * 
 * @author imodm
 *
 */
öffentlicher abstrakter Klassenunterbefehl {

	geschütztes NOPE-Plugin;

	öffentlicher Unterbefehl (NOPE-Plugin) {
		this.plugin = plugin;
	}

	@Nullbar
	öffentliche abstrakte Liste<String[]> TabCompletions(CommandSender Absender);

	öffentliche abstrakte Zeichenkette getName();

	öffentliche abstrakte Zeichenkette();

	öffentliche abstrakte Zeichenkette();

	öffentliche Liste<String> getAliases() {
		return new ArrayList<>();
	}

	öffentliche String getPermission() {
		zurück null;
	}

	öffentliche abstrakte CommandResult ausgeführt (CommandSender sender, String[] args);
}
