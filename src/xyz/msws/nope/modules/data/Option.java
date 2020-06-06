package xyz.msws.nope.modules.data;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple interface to represent options
 * 
 * @author imodm
 *
 */
public interface Option {
	/**
	 * This should return a unique identifier for a specific option.
	 * 
	 * @return
	 */
	@Nonnull
	public String getKey();

	/**
	 * Toggle a value, this can be used to iterate through the list of
	 * {@link Option#getOptions()}.
	 * 
	 * @return The result of what the *NEW* value is, return null if this operation
	 *         is unsupported.
	 */
	public <T extends Object> T toggle();

	/**
	 * Set a value, should be used for Strings or non-boolean values.
	 * 
	 * @param value
	 * @return Returns the resulting object, most likely will just be @param value
	 *         This can also be null if an invalid value was specified
	 */
	@Nullable
	public <T extends Object> T set(T value);

	/**
	 * Returns the list of options, the <b>first</b> item in this list should be the
	 * default
	 * 
	 * @return
	 */
	public List<? extends Object> getOptions();

	/**
	 * Returns the current value of the option
	 * 
	 * @return
	 */
	public <T extends Object> T getValue();

	/**
	 * Returns the current value casted to the specified class
	 * 
	 * @param <T>
	 * @param cast
	 * @return
	 */
	public default <T extends Object> T getValue(Class<T> cast) {
		return cast.cast(getValue());
	}

	/**
	 * Check if the value set is also the first option
	 * 
	 * @return
	 */
	public default boolean isDefault() {
		if (getValue() == null && getOptions() == null)
			return true;
		if (getOptions() == null)
			return false;
		return getOptions().get(0).equals(getValue());
	}

	/**
	 * Returns the current value casted to Boolean
	 * 
	 * @return
	 */
	public default boolean asBoolean() {
		return getValue(Boolean.class);
	}

	/**
	 * Returns the current value from {@link Number#doubleValue()}
	 * 
	 * @return
	 */
	public default double asDouble() {
		return getValue(Number.class).doubleValue();
	}

	/**
	 * Returns the current value from {@link Number#intValue()()}
	 * 
	 * @return
	 */
	public default int asInteger() {
		return getValue(Number.class).intValue();
	}

	/**
	 * Returns the current value from {@link Number#longValue()}
	 * 
	 * @return
	 */
	public default long asLong() {
		return getValue(Number.class).longValue();
	}

	/**
	 * Returns the current value from {@link Number#floatValue()()}
	 * 
	 * @return
	 */
	public default float asFloat() {
		return getValue(Number.class).floatValue();
	}

	/**
	 * Returns the current value from {@link Number#shortValue()Value()}
	 * 
	 * @return
	 */
	public default short asShort() {
		return getValue(Number.class).shortValue();
	}

}
