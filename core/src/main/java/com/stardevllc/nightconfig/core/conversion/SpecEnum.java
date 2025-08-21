package com.stardevllc.nightconfig.core.conversion;

import com.stardevllc.nightconfig.core.EnumGetMethod;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must correspond to an enum and
 * that the value must be read using the given {@link EnumGetMethod}.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecEnum {
	/**
	 * How to interpret the config value. For instance, should we match the name and the ordinal()
	 * or just the name? Should we ignore the case of the string value or not?
	 */
	EnumGetMethod method();
}
