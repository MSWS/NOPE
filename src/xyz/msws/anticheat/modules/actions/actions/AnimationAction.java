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
import xyz.msws.anticheat.utils.MSG;

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

		Player p = player.getPlayer();
		if (p == null || !p.isValid()) {
			MSG.warn(p.getName() + " is invalid?");
			group.activate(player, check);
			return;
		}

		AbstractAnimation animation = plugin.getModule(AnimationManager.class).createAnimation(type, p, check);
		animation.setEndAction(this.group);
		if (plugin.getModule(AnimationManager.class).startAnimation(p, animation))
			return;
		MSG.warn("Unable to start animation for " + player.getName());
	}

}
