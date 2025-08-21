package com.stardevllc.nightconfig.toml;

import com.stardevllc.nightconfig.core.CommentedConfig;
import com.stardevllc.nightconfig.core.io.CharacterInput;
import com.stardevllc.nightconfig.core.io.ParsingException;
import java.util.List;

/**
 * @author TheElectronWill
 * @see <a href="https://github.com/toml-lang/toml#user-content-array">TOML specification - Arrays</a>
 */
final class ArrayParser {
	/**
	 * Parses a plain array, not an array of tables.
	 */
	static List<?> parse(CharacterInput input, com.stardevllc.nightconfig.toml.TomlParser parser, CommentedConfig parentConfig) {
		List<Object> list = parser.createList();
		boolean first = true;
		while (true) {
			char firstChar = com.stardevllc.nightconfig.toml.Toml.readUsefulChar(input);
			if (firstChar == ']') {// End of the array
				return list;// handle [] and [v1,v2,... ,]
			} else if (firstChar == ',') {// Handles [,] or [v1,,] which are both invalid
				if (first) {
					throw new ParsingException("Invalid array: [,]");
				} else {
					throw new ParsingException("Invalid double comma in array.");
				}
			}
			Object value = com.stardevllc.nightconfig.toml.ValueParser.parse(input, firstChar, parser, parentConfig);
			list.add(value);
			char after = com.stardevllc.nightconfig.toml.Toml.readUsefulChar(input);
			if (after == ']') {// End of the array
				return list;
			}
			if (after != ',') {// Invalid character between two elements of the array
				throw new ParsingException("Invalid separator '" + after + "' in array.");
			}
			first = false;
		}
	}

	private ArrayParser() {}
}