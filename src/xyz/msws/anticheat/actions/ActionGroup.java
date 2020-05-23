package xyz.msws.anticheat.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.checks.Check;

/**
 * Represents the line of actions that one check can have
 * 
 * An example would be: "cancel|vl>50|log:FILE:%player% flagged %check%"
 * 
 * This is used for easy management and easy combination of actions.
 * 
 * @author imodm
 *
 */
public class ActionGroup implements List<AbstractAction> {
	private List<AbstractAction> actions;

	public ActionGroup() {
		this.actions = new ArrayList<>();
	}

	public ActionGroup(List<AbstractAction> actions) {
		this();
		this.actions = actions;
	}

	public void activate(OfflinePlayer player, Check check) {
		for (int i = 0; i < this.size(); i++) {
			AbstractAction act = this.get(i);
			if (act instanceof AbstractConditionalAction) {
				AbstractConditionalAction cond = (AbstractConditionalAction) act;
				if (!cond.getValue(player, check)) {
					if (cond instanceof DelayAction) {
						// DelayAction ONLY should apply to only the next action
						i++;
						continue;
					}
					break;
				}
			}
			act.execute(player, check);
		}
	}

	@Override
	public int size() {
		return actions.size();
	}

	@Override
	public boolean isEmpty() {
		return actions.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return actions.contains(o);
	}

	@Override
	public Iterator<AbstractAction> iterator() {
		return actions.iterator();
	}

	@Override
	public Object[] toArray() {
		return actions.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return actions.toArray(a);
	}

	@Override
	public boolean add(AbstractAction e) {
		return actions.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return actions.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return actions.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends AbstractAction> c) {
		return actions.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends AbstractAction> c) {
		return actions.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return actions.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return actions.retainAll(c);
	}

	@Override
	public void clear() {
		actions.clear();
	}

	@Override
	public AbstractAction get(int index) {
		return actions.get(index);
	}

	@Override
	public AbstractAction set(int index, AbstractAction element) {
		return actions.set(index, element);
	}

	@Override
	public void add(int index, AbstractAction element) {
		actions.add(index, element);
	}

	@Override
	public AbstractAction remove(int index) {
		return actions.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return actions.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return actions.lastIndexOf(o);
	}

	@Override
	public ListIterator<AbstractAction> listIterator() {
		return actions.listIterator();
	}

	@Override
	public ListIterator<AbstractAction> listIterator(int index) {
		return actions.listIterator(index);
	}

	@Override
	public List<AbstractAction> subList(int fromIndex, int toIndex) {
		return actions.subList(fromIndex, toIndex);
	}

}
