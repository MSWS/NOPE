package xyz.msws.nope.modules.data;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents an option in the config
 * 
 * @author imodm
 *
 */
public class ConfigOption implements Option {
	private List<? extends Object> options;
	private Object value;
	private FileConfiguration conf;
	private String key;

	public ConfigOption(FileConfiguration conf, String key, @Nullable List<? extends Object> options) {
		this.options = options;
		this.conf = conf;
		this.key = key;

		if (conf.contains(key)) {
			this.value = conf.get(key);
		} else if (options != null) {
			this.value = options.get(0);
			conf.set(key, this.value);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> ConfigOption(FileConfiguration conf, String key, @Nullable T... options) {
		this.conf = conf;
		this.key = key;
		if (options == null) {
			this.options = null;
			return;
		}
		this.options = Arrays.asList(options);
		if (conf.contains(key)) {
			this.value = conf.get(key);
		} else if (options != null) {
			this.value = options[0];
			conf.set(key, this.value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toggle() {
		if (options == null || options.isEmpty())
			return null;
		set(options.get((options.indexOf(value) + 1) % options.size()));
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object set(Object value) {
		this.value = value;
		conf.set(key, value);
		return value;
	}

	@Override
	@Nullable
	public List<? extends Object> getOptions() {
		return options;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getKey() {
		return key;
	}
}
