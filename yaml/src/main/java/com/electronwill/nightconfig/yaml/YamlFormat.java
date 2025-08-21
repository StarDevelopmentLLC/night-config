package com.electronwill.nightconfig.yaml;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FormatDetector;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import org.yaml.snakeyaml.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author TheElectronWill
 */
public final class YamlFormat implements ConfigFormat<Config> {
	private static final ThreadLocal<YamlFormat> LOCAL_DEFAULT_FORMAT = ThreadLocal.withInitial(
			() -> {
				LoaderOptions loaderOptions = new LoaderOptions();
				loaderOptions.setProcessComments(true);
				DumperOptions dumperOptions = new DumperOptions();
				dumperOptions.setProcessComments(true);
				return new YamlFormat(new Yaml(loaderOptions, dumperOptions));
			});

	/**
	 * @return the default instance of HoconFormat
	 */
	public static YamlFormat defaultInstance() {
		return LOCAL_DEFAULT_FORMAT.get();
	}

	/**
	 * Creates an instance of YamlFormat, set with the specified Yaml object.
	 *
	 * @param yaml the Yaml object to use
	 * @return a new instance of YamlFormat
	 */
	public static YamlFormat configuredInstance(Yaml yaml) {
		return new YamlFormat(yaml);
	}

	/**
	 * @return a new config with the format {@link YamlFormat#defaultInstance()}.
	 */
	public static Config newConfig() {
		return defaultInstance().createConfig();
	}

	/**
	 * @return a new config with the given map creator
	 */
	public static Config newConfig(Supplier<Map<String, Object>> mapCreator) {
		return defaultInstance().createConfig(mapCreator);
	}

	/**
	 * @return a new concurrent config with the format {@link YamlFormat#defaultInstance()}.
	 */
	public static Config newConcurrentConfig() {
		return defaultInstance().createConcurrentConfig();
	}

	static {
		FormatDetector.registerExtension("yaml", YamlFormat::defaultInstance);
		FormatDetector.registerExtension("yml", YamlFormat::defaultInstance);
	}

	final Yaml yaml;

	private YamlFormat(Yaml yaml) {
		this.yaml = yaml;
	}

	@Override
	public ConfigWriter createWriter() {
		return new YamlWriter(yaml);
	}

	@Override
	public ConfigParser<Config> createParser() {
		return new YamlParser(this);
	}

	@Override
	public Config createConfig(Supplier<Map<String, Object>> mapCreator) {
		return Config.of(mapCreator, this);
	}

	@Override
	public boolean supportsComments() {
		return true;
	}

	@Override
	public boolean supportsType(Class<?> type) {
		return type == null
            || type.isEnum()
            || type == Boolean.class
            || type == String.class
            || type == java.util.Date.class
            || type == java.sql.Date.class
            || type == java.sql.Timestamp.class
            || type == byte[].class
            || type == Object[].class
            || Number.class.isAssignableFrom(type)
            || Set.class.isAssignableFrom(type)
            || List.class.isAssignableFrom(type)
            || Config.class.isAssignableFrom(type);
	}
}