package com.stardevllc.nightconfig.json;

import com.stardevllc.nightconfig.core.Config;
import com.stardevllc.nightconfig.core.ConfigFormat;
import com.stardevllc.nightconfig.core.concurrent.ConcurrentConfig;
import com.stardevllc.nightconfig.core.io.*;
import com.stardevllc.nightconfig.core.utils.FastStringReader;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A JSON parser.
 *
 * @author TheElectronWill
 */
public final class JsonParser implements ConfigParser<Config> {
	private static final char[] SPACES = {' ', '\t', '\n', '\r'};
	private static final char[] TRUE_LAST = {'r', 'u', 'e'}, FALSE_LAST = {'a', 'l', 's', 'e'};
	private static final char[] NULL_LAST = {'u', 'l', 'l'};
	private static final char[] NUMBER_END = {',', '}', ']', ' ', '\t', '\n', '\r'};

	private final ConfigFormat<Config> configFormat;
	private boolean emptyDataAccepted = false;
	private boolean trailingDataAccepted = false;

	public JsonParser() {
		this(JsonFormat.fancyInstance());
	}

	JsonParser(ConfigFormat<Config> configFormat) {
		this.configFormat = configFormat;
	}

	@Override
	public ConfigFormat<Config> getFormat() {
		return configFormat;
	}

	/**
	 * @return true if the parser accepts empty data as a valid input, false otherwise (default)
	 */
	public boolean isEmptyDataAccepted() {
		return emptyDataAccepted;
	}

	/**
	 * Enables or disables the acceptance of empty input data. False by default. If set to true,
	 * the parser will return an empty config (or an empty list in the case of a
	 * {@link #parseList(Reader)} call) when the input is empty.
	 *
	 * @param emptyDataAccepted true to accept empty data as a valid input, false to reject it
	 */
	public JsonParser setEmptyDataAccepted(boolean emptyDataAccepted) {
		this.emptyDataAccepted = emptyDataAccepted;
		return this;
	}

	/**
	 * @return true if the parser accepts trailing data (data after the end of the JSON document) as a valid input, false otherwise (default)
	 */
	public boolean isTrailingDataAccepted() {
		return trailingDataAccepted;
	}

	/**
	 * Enables or disables the acceptance of trailing input data. False by default. If set to true,
	 * the parser will ignore any data that is after the end of the document.
	 *
	 * @param trailingDataAccepted true to accept trailing data (data after the end of the JSON document) as a valid input, false to reject it
	 */
	public JsonParser setTrailingDataAccepted(boolean trailingDataAccepted) {
		this.trailingDataAccepted = trailingDataAccepted;
		return this;
	}

	/**
	 * Parses a JSON document, either a JSON object (parsed to a JsonConfig) or a JSON array
	 * (parsed to a List).
	 *
	 * @param json the data to parse
	 * @return either a JsonConfig or a List, depending on the document's type
	 */
	public Object parseDocument(String json) {
		return parseDocument(new FastStringReader(json));
	}

	/**
	 * Parses a JSON document, either a JSON object (parsed to a JsonConfig) or a JSON array
	 * (parsed to a List).
	 *
	 * @param reader the Reader to parse
	 * @return either a JsonConfig or a List, depending on the document's type
	 */
	public Object parseDocument(Reader reader) {
		return parseDocument(reader, configFormat.createConfig());
	}

	/**
	 * Parses a JSON document, either a JSON object (parsed to a JsonConfig) or a JSON array
	 * (parsed to a List).
	 *
	 * @param reader the Reader to parse
	 * @return either a JsonConfig or a List, depending on the document's type
	 */
	public Object parseDocument(Reader reader, Config configModel) {
		CharacterInput input = new ReaderInput(reader);
		if (input.peek() == -1) {
			if (emptyDataAccepted) {
				// If data is empty && we accept empty data => return empty config
				return configModel.createSubConfig();
			} else {
				throw new ParsingException("No json data: input is empty");
			}
		}
		char firstChar = input.readCharAndSkip(SPACES);
		Object result;
		if (firstChar == '{') {
			result = parseObject(input, configModel.createSubConfig(), ParsingMode.MERGE);
		} else if (firstChar == '[') {
			result = parseArray(input, new ArrayList<>(), ParsingMode.MERGE, configModel.createSubConfig());
		} else {
			throw new ParsingException("Invalid first character for a json document: " + firstChar);
		}
		checkNoTrailingData(input);
		return result;
	}

	private void checkNoTrailingData(CharacterInput input) {
		if (!trailingDataAccepted) {
			int trailing = input.readAndSkip(SPACES);
			if (trailing >= 0) {
				input.pushBack((char) trailing);
				String msg = String.format(
						"Invalid data at the end of the JSON document: %s (use JsonParser.setTrailingDataAccepted(true) if you intend this to work)",
						input.read(6).toString());
				throw new ParsingException(msg);
			}
		}
	}

	/**
	 * Parses a JSON object to a Config.
	 */
	@Override
	public Config parse(Reader reader) {
		Config config = configFormat.createConfig();
		parse(reader, config, ParsingMode.MERGE);
		return config;
	}

	/**
	 * Parses a JSON object to a Config.
	 */
	@Override
	public void parse(Reader reader, Config destination, ParsingMode parsingMode) {
		CharacterInput input = new ReaderInput(reader);
		if (input.peek() == -1) {
			if (emptyDataAccepted) {
				// If data is empty && we accept empty data => let the config as it is
				return;
			} else {
				throw new ParsingException("No json data: input is empty");
			}
		}
		char firstChar = input.readCharAndSkip(SPACES);
		if (firstChar != '{') {
			throw new ParsingException("Invalid first character for a json object: " + firstChar);
		}
		if (destination instanceof ConcurrentConfig) {
			((ConcurrentConfig)destination).bulkUpdate(view -> {
				parsingMode.prepareParsing(view);
				parseObject(input, view, parsingMode);
			});
		} else {
			parsingMode.prepareParsing(destination);
			parseObject(input, destination, parsingMode);
		}
		checkNoTrailingData(input);
	}

	/**
	 * Parses a JSON array to a List.
	 *
	 * @param json the data to parse
	 * @return a List with the content of the parsed array
	 */
	public <T> List<T> parseList(String json) {
		return parseList(new FastStringReader(json));
	}

	/**
	 * Parses a JSON array to a List.
	 *
	 * @param reader the Reader to parse
	 * @return a List with the content of the parsed array
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> parseList(Reader reader) {
		List<Object> list = new ArrayList<>();
		parseList(reader, list, ParsingMode.MERGE, configFormat.createConfig());
		return (List<T>)list;
	}

	/**
	 * Parses a JSON array to a List.
	 *
	 * @param reader      the Reader to parse
	 * @param destination the List where to put the data
	 */
	public void parseList(Reader reader, List<?> destination, ParsingMode parsingMode) {
		parseList(reader, destination, parsingMode, configFormat.createConfig());
	}

	/**
	 * Parses a JSON array to a List, with a model for the configurations (JSON objects) contained in the list.
	 *
	 * @param reader      the Reader to parse
	 * @param destination the List where to put the data
	 */
	public void parseList(Reader reader, List<?> destination, ParsingMode parsingMode, Config configModel) {
		CharacterInput input = new ReaderInput(reader);
		if (input.peek() == -1) {
			if (emptyDataAccepted) {
				// If data is empty && we accept empty data => let the config as it is
				return;
			} else {
				throw new ParsingException("No json data: input is empty");
			}
		}
		char firstChar = input.readCharAndSkip(SPACES);
		if (firstChar != '[') {
			throw new ParsingException("Invalid first character for a json array: " + firstChar);
		}
		parseArray(input, destination, parsingMode, configModel);
		checkNoTrailingData(input);
	}

	private <T extends Config> T parseObject(CharacterInput input, T config, ParsingMode parsingMode) {
		char kfirst = input.readCharAndSkip(SPACES);
		if (kfirst == '}') {
			return config;
		} else if (kfirst != '"') {
			throw new ParsingException("Invalid beginning of a key: " + kfirst);
		}
		parseKVPair(input, config, parsingMode);
		while (true) {
			char vsep = input.readCharAndSkip(SPACES);
			if (vsep == '}') {// end of the object
				return config;
			} else if (vsep != ',') {
				throw new ParsingException("Invalid value separator: " + vsep);
			}
			kfirst = input.readCharAndSkip(SPACES);
			if (kfirst != '"') {
				throw new ParsingException("Invalid beginning of a key: " + kfirst);
			}
			parseKVPair(input, config, parsingMode);
		}
	}

	private void parseKVPair(CharacterInput input, Config config, ParsingMode parsingMode) {
		List<String> key = Collections.singletonList(parseString(input)); // the list is necessary if there are dots in the key
		char sep = input.readCharAndSkip(SPACES);
		if (sep != ':') {
			throw new ParsingException("Invalid key-value separator: " + sep);
		}

		char vfirst = input.readCharAndSkip(SPACES);
		Object value = parseValue(input, vfirst, parsingMode, config);
		parsingMode.put(config, key, value);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> parseArray(CharacterInput input, List<T> list, ParsingMode parsingMode, Config parentConfig) {
		boolean first = true;
		while (true) {
			char valueFirst = input.readCharAndSkip(SPACES);// the first character of the value
			if (first && valueFirst == ']') {
				return list;
			}
			first = false;
			T value = (T)parseValue(input, valueFirst, parsingMode, parentConfig);
			list.add(value);
			char next = input.readCharAndSkip(SPACES);// the next character, should be ']' or ','
			if (next == ']') {// end of the array
				return list;
			} else if (next != ',') {// invalid separator
				throw new ParsingException("Invalid value separator: " + valueFirst);
			}
		}
	}

	private Object parseValue(CharacterInput input, char firstChar, ParsingMode parsingMode, Config parentConfig) {
		switch (firstChar) {
			case '"':
				return parseString(input);
			case '{':
				return parseObject(input, parentConfig.createSubConfig(), parsingMode);
			case '[':
				return parseArray(input, new ArrayList<>(), parsingMode, parentConfig);
			case 't':
				return parseTrue(input);
			case 'f':
				return parseFalse(input);
			case 'n':
				return parseNull(input);
			default:
				input.pushBack(firstChar);
				return parseNumber(input);
		}
	}

	private Number parseNumber(CharacterInput input) {
		CharsWrapper chars = input.readCharsUntil(NUMBER_END);
		if (chars.contains('.') || chars.contains('e') || chars.contains('E')) {// must be a double
			return Utils.parseDouble(chars);
		}
		long l = Utils.parseLong(chars, 10);
		int small = (int)l;
		if (l == small) {// small value => return an int instead of a long
			return small;
		}
		return l;
	}

	private boolean parseTrue(CharacterInput input) {
		CharsWrapper chars = input.readChars(3);
		if (!chars.contentEquals(TRUE_LAST)) {
			throw new ParsingException("Invalid value: t" + chars + " - expected boolean true");
		}
		return true;
	}

	private boolean parseFalse(CharacterInput input) {
		CharsWrapper chars = input.readChars(4);
		if (!chars.contentEquals(FALSE_LAST)) {
			throw new ParsingException("Invalid value: f" + chars + " - expected boolean false");
		}
		return false;
	}

	private Object parseNull(CharacterInput input) {
		CharsWrapper chars = input.readChars(3);
		if (!chars.contentEquals(NULL_LAST)) {
			throw new ParsingException("Invaid value: n" + chars + " - expected null");
		}
		return null;
	}

	private String parseString(CharacterInput input) {
		StringBuilder builder = new StringBuilder();
		boolean escape = false;
		char c;
		while ((c = input.readChar()) != '"' || escape) {
			if (escape) {
				builder.append(unescape(c, input));
				escape = false;
			} else if (c == '\\') {
				escape = true;
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	private char unescape(char c, CharacterInput input) {
		switch (c) {
			case '"':
			case '\\':
			case '/':
				return c;
			case 'b':
				return '\b';
			case 'f':
				return '\f';
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 't':
				return '\t';
			case 'u':
				CharsWrapper chars = input.readChars(4);
				return (char)Utils.parseInt(chars, 16);
			default:
				throw new ParsingException("Invalid escapement: \\" + c);
		}
	}
}