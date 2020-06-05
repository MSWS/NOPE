package xyz.msws.nope.modules.actions.actions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.actions.ActionGroup;
import xyz.msws.nope.modules.actions.ActionManager;
import xyz.msws.nope.modules.animations.AbstractAnimation;
import xyz.msws.nope.modules.animations.AnimationManager;
import xyz.msws.nope.modules.animations.AnimationManager.AnimationType;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.utils.MSG;

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
