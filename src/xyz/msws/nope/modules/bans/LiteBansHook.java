package xyz.msws.nope.modules.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import litebans.api.Database;
import xyz.msws.nope.NOPE;

public class LiteBansHook extends BanHook {

	// LiteBans has a questionable [b]api[/b]

	public LiteBansHook(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void ban(UUID player, String reason, long time) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + Bukkit.getOfflinePlayer(player).getName()
				+ " --sender=NOPE " + (reason == null ? "Hacking" : reason));
	}

	@Override
	public int bans(UUID player) {
		try {
			PreparedStatement statement = Database.get().prepareStatement("SELECT * FROM {bans} WHERE uuid=?");
			statement.setString(1, player.toString());

			int amo = 0;

			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					amo++;
				}
			}
			return amo;

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
