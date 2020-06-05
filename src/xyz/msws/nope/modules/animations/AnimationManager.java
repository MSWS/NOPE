package xyz.msws.nope.modules.animations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.checks.Check;

public class AnimationManager extends AbstractModule {

	public AnimationManager(NOPE plugin) {
		super(plugin);
	}

	private Map<UUID, AbstractAnimation> animations = new HashMap<>();

	@Override
	public void enable() {

	}

	public boolean isInAnimation(Player player) {
		if (animations.containsKey(player.getUniqueId()))
			return !animations.get(player.getUniqueId()).completed();
		return false;
	}

	public boolean startAnimation(Player player, AbstractAnimation animation) {
		animations.put(player.getUniqueId(), animation);
		return animation.queue();
	}

	@Override
	public void disable() {
		for (AbstractAnimation animation : animations.values().stream().filter(a -> !a.completed())
				.collect(Collectors.toList()))
			animation.stop();
	}

	public AbstractAnimation createAnimation(AnimationType type, Player player, Check check) {
		switch (type) {
			case GWEN:
				return new GWENAnimation(plugin, player, check);
			case NOPE:
				return new NOPEAnimation(plugin, player, check);
		}
		return null;
	}

	public enum AnimationType {
		GWEN, NOPE;
	}

}
