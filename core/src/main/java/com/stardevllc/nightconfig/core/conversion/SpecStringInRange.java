package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must be a String in a certain range (inclusive, comparison
 * done lexicographically).
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecStringInRange {
	/** @return the minimum possible value, inclusive */
	String min();

	/** @return the maximum possible value, inclusive */
	String max();
}