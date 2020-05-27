package xyz.msws.anticheat.modules.animations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class AnimationManager extends AbstractModule {

	public AnimationManager(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, AbstractAnimation> animations = new HashMap<>();

	@Override
	public void enable() {

	}

	public boolean startAnimation(Player player, AbstractAnimation animation) {
		if (animations.containsKey(player.getUniqueId())) {
			animations.get(player.getUniqueId()).stop(false);
		}
		animations.put(player.getUniqueId(), animation);
		animation.start();
		return false;
	}

	@Override
	public void disable() {
		for (AbstractAnimation animation : animations.values())
			animation.stop(true);
	}

}
