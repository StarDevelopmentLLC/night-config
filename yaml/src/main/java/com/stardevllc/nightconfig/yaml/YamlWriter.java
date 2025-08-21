package com.stardevllc.nightconfig.yaml;

import com.stardevllc.nightconfig.core.UnmodifiableConfig;
import com.stardevllc.nightconfig.core.concurrent.StampedConfig;
import com.stardevllc.nightconfig.core.io.ConfigWriter;
import com.stardevllc.nightconfig.core.io.WritingException;
import com.stardevllc.nightconfig.core.utils.TransformingList;
import com.stardevllc.nightconfig.core.utils.TransformingMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import static com.stardevllc.nightconfig.core.NullObject.NULL_OBJECT;

/**
 * A YAML writer that uses the snakeYaml library.
 *
 * @author TheElectronWill
 */
public final class YamlWriter implements ConfigWriter {
	private final Yaml yaml;

	public YamlWriter() {
		this(new Yaml());
	}

	public YamlWriter(Yaml yaml) {
		this.yaml = yaml;
	}

	public YamlWriter(DumperOptions options) {
		this(new Yaml(options));
	}

	@Override
	public void write(UnmodifiableConfig config, Writer writer) {
		if (config instanceof StampedConfig) {
			// StampedConfig does not support valueMap(), use the accumulator
			config = ((StampedConfig)config).newAccumulatorCopy();
		}
		try {
			Map<String, Object> unwrappedMap = unwrap(config);
			yaml.dump(unwrappedMap, writer);
		} catch (Exception e) {
			throw new WritingException("YAML writing failed", e);
		}
	}

	private static Map<String, Object> unwrap(UnmodifiableConfig config) {
		return new TransformingMap<>(config.valueMap(), YamlWriter::unwrap, v -> v, v -> v);
	}

	private static List<Object> unwrapList(List<Object> list) {
		return new TransformingList<>(list, YamlWriter::unwrap, v -> v, v -> v);
	}

	@SuppressWarnings("unchecked")
	private static Object unwrap(Object value) {
		if (value instanceof UnmodifiableConfig) {
			return unwrap((UnmodifiableConfig)value);
		}
		if (value instanceof List) {
			return unwrapList((List<Object>)value);
		}
		if (value == NULL_OBJECT) {
			return null;
		}
		return value;
	}
}