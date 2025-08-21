package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must be in a certain range (inclusive).
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecIntInRange {
	/** @return the minimum possible value, inclusive */
	int min();

	/** @return the maximum possible value, inclusive */
	int max();
}