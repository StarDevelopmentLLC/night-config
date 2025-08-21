package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;
import java.util.function.Predicate;

/**
 * Indicates that the value of the field must be validated by the specified validator class.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecValidator {
	/**
	 * The validator class used to check that the value is correct.
	 *
	 * @return the validator class
	 */
	Class<? extends Predicate<Object>> value();
}