package com.stardevllc.nightconfig.json;

import com.stardevllc.nightconfig.core.UnmodifiableConfig;
import com.stardevllc.nightconfig.core.io.*;

import java.io.Writer;
import java.util.*;

import static com.stardevllc.nightconfig.core.NullObject.NULL_OBJECT;

/**
 * A simple JSON writer that produces a minimized output: no line breaks, no spaces, no indentation.
 * Use the {@link FancyJsonWriter} if you want a nicer output.
 *
 * @author TheElectronWill
 */
public final class MinimalJsonWriter implements ConfigWriter {
	static final char[] NULL_CHARS = {'n', 'u', 'l', 'l'};
	static final char[] TRUE_CHARS = {'t', 'r', 'u', 'e'};
	static final char[] FALSE_CHARS = {'f', 'a', 'l', 's', 'e'};
	static final char[] TO_ESCAPE = {'"', '\n', '\r', '\t', '\\'};
	static final char[] ESCAPED = {'"', 'n', 'r', 't', '\\'};
	static final char[] EMPTY_OBJECT = {'{', '}'}, EMPTY_ARRAY = {'[', ']'};

	/**
	 * Writes a configuration in the JSON object format.
	 */
	@Override
	public void write(UnmodifiableConfig config, Writer writer) {
		writeConfig(config, new WriterOutput(writer));
	}

	/**
	 * Writes a Collection in the JSON array format.
	 */
	public void writeCollection(Collection<?> collection, Writer writer) {
		writeCollection(collection, new WriterOutput(writer));
	}

	/**
	 * Writes a String in the JSON string format.
	 */
	public void writeString(CharSequence csq, Writer writer) {
		writeString(csq, new WriterOutput(writer));
	}

	/**
	 * Writes a value in the JSON format.
	 */
	public void writeValue(Object value, Writer writer) {
		writeValue(value, new WriterOutput(writer));
	}

	private void writeConfig(UnmodifiableConfig config, CharacterOutput output) {
		if (config.isEmpty()) {
			output.write(EMPTY_OBJECT);
			return;
		}
		Iterator<? extends UnmodifiableConfig.Entry> it = config.entrySet().iterator();
		output.write('{');
		while (true) {
			UnmodifiableConfig.Entry entry = it.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			writeString(key, output);// key
			output.write(':');// separator
			writeValue(value, output);// value
			if (it.hasNext()) {
				output.write(',');
			} else {
				break;
			}
		}
		output.write('}');
	}

	private void writeValue(Object v, CharacterOutput output) {
		if (v == null || v == NULL_OBJECT) {
			output.write(NULL_CHARS);
		} else if (v instanceof CharSequence) {
			writeString((CharSequence)v, output);
		} else if (v instanceof Enum) {
			writeString(((Enum<?>)v).name(), output);
		} else if (v instanceof Number) {
			output.write(v.toString());
		} else if (v instanceof UnmodifiableConfig) {
			writeConfig((UnmodifiableConfig)v, output);
		} else if (v instanceof Collection) {
			writeCollection((Collection<?>)v, output);
		} else if (v instanceof Boolean) {
			writeBoolean((boolean)v, output);
		} else if (v instanceof Object[]) {
			List<Object> list = Arrays.asList((Object[])v);
			writeCollection(list, output);
		} else if (v instanceof long[]) {
			output.write(Arrays.toString((long[])v));
		} else if (v instanceof int[]) {
			output.write(Arrays.toString((int[])v));
		} else if (v instanceof double[]) {
			output.write(Arrays.toString((double[])v));
		} else if (v instanceof float[]) {
			output.write(Arrays.toString((float[])v));
		} else if (v instanceof short[]) {
			output.write(Arrays.toString((short[])v));
		} else if (v instanceof byte[]) {
			output.write(Arrays.toString((byte[])v));
		} else {
			throw new WritingException("Unsupported value type: " + v.getClass());
		}
	}

	private void writeCollection(Collection<?> collection, CharacterOutput output) {
		if (collection.isEmpty()) {
			output.write(EMPTY_ARRAY);
			return;
		}
		output.write('[');
		if (collection instanceof RandomAccess) {
			List<?> list = (List<?>)collection;// A class implementing RandomAccess should be a List
			int lastIndex = list.size() - 1;
			for (int i = 0; i < lastIndex; i++) {
				Object value = list.get(i);
				writeValue(value, output);
				output.write(',');
			}
			writeValue(list.get(lastIndex), output);
		} else {
			Iterator<?> it = collection.iterator();
			while (true) {
				Object value = it.next();
				writeValue(value, output);// the value
				if (it.hasNext()) {
					output.write(',');// the separator
				} else {
					break;
				}
			}
		}
		output.write(']');
	}

	private void writeBoolean(boolean b, CharacterOutput output) {
		if (b) {
			output.write(TRUE_CHARS);
		} else {
			output.write(FALSE_CHARS);
		}
	}

	private void writeString(CharSequence csq, CharacterOutput output) {
		output.write('"');
		final int length = csq.length();
		for (int i = 0; i < length; i++) {
			char c = csq.charAt(i);
			int escapeIndex = Utils.arrayIndexOf(TO_ESCAPE, c);
			if (escapeIndex == -1) {
				output.write(c);
			} else {// the character must be escaped
				char escaped = ESCAPED[escapeIndex];
				output.write('\\');
				output.write(escaped);
			}
		}
		output.write('"');
	}
}