package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.actions.ActionGroup;
import xyz.msws.anticheat.modules.actions.ActionManager;
import xyz.msws.anticheat.modules.animations.AbstractAnimation;
import xyz.msws.anticheat.modules.animations.AnimationManager;
import xyz.msws.anticheat.modules.animations.AnimationManager.AnimationType;
import xyz.msws.anticheat.modules.checks.Check;

public class AnimationAction extends AbstractAction {

	@SuppressWarnings("unused")
	private String data;
	private ActionGroup group;
	private AnimationType type;

	public AnimationAction(NOPE plugin, AnimationType type, String data) {
		super(plugin);
		this.data = data;
		this.type = type;

		group = plugin.getModule(ActionManager.class).getActions(data).get(0);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if (!player.isOnline()) {
			group.activate(player, check);
			return;
		}

		AbstractAnimation animation = plugin.getModule(AnimationManager.class).createAnimation(type, (Player) player,
				check);
		animation.setEndAction(this.group);
		plugin.getModule(AnimationManager.class).startAnimation((Player) player, animation);
	}

}
