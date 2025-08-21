package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must be a String that is contained in a certain array.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecStringInArray {
	/**
	 * @return the values that are allowed
	 */
	String[] value();

	/**
	 * @return {@code true} to ignore the case, {@code false} to check the case. Default is
	 * {@code false}.
	 */
	boolean ignoreCase() default false;
}