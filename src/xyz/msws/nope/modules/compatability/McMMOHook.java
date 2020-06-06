package xyz.msws.nope.modules.compatability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.player.PlayerFlagEvent;
import xyz.msws.nope.modules.checks.Check;

public class McMMOHook extends AbstractCompatability {

	public McMMOHook(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, Long> lastTreeFeller = new HashMap<>();

	@EventHandler
	public void treeFellerCheck(PlayerFlagEvent event) {
		Check check = event.getCheck();
		Player player = event.getPlayer();
		if (!check.getCategory().equals("FastBreak"))
			return;

		if (AbilityAPI.treeFellerEnabled(player))
			return;

		if (System.currentTimeMillis() - lastTreeFeller.getOrDefault(player.getUniqueId(), 0L) > 1000)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onAbilityActivate(McMMOPlayerAbilityActivateEvent event) {
		Player player = event.getPlayer();
		if (event.getAbility() != SuperAbilityType.TREE_FELLER)
			return;
		lastTreeFeller.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@Override
	public String getName() {
		return "mcMMO";
	}

}
