package xyz.msws.nope.modules.data;

import java.util.Arrays;
import java.util.List;

/**
 * A PlayerOption represents a Boolean value for a specified player
 * 
 * @author imodm
 *
 */
public class PlayerOption implements Option {

	private CPlayer cp;
	private String key;
	private boolean value;

	/**
	 * Construct a PlayerOption, the data is automatically loaded and assigned if
	 * available
	 * 
	 * @param cp
	 * @param key
	 */
	public PlayerOption(CPlayer cp, String key) {
		this.cp = cp;
		this.key = key;
		if (!cp.hasSaveData(key)) {
			this.value = false;
		} else {
			this.value = cp.getSaveData(key, Boolean.class);
		}
	}

	/**
	 * Same as the parent constructor however allows for specifying a default (def)
	 * value if the player doesn't have it originally set
	 * 
	 * @param cp
	 * @param key
	 * @param def
	 */
	public PlayerOption(CPlayer cp, String key, boolean def) {
		this.cp = cp;
		this.key = key;
		if (!cp.hasSaveData(key)) {
			this.value = def;
		} else {
			this.value = cp.getSaveData(key, Boolean.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean toggle() {
		this.value = !this.value;
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean set(Object value) {
		if (!(value instanceof Boolean))
			throw new IllegalArgumentException("PlayerOption only accepts true or false");
		this.value = (boolean) value;
		cp.setSaveData(key, this.value);
		return this.value;
	}

	@Override
	public List<? extends Object> getOptions() {
		return Arrays.asList(true, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	public String getKey() {
		return key;
	}

}
