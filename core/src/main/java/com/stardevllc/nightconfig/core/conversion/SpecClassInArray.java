package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must have a specific class.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecClassInArray {
	/**
	 * @return the classes that are allowed
	 */
	Class<?>[] value();

	/**
	 * @return {@code true} to allow only the acceptable classes, {@code false} to allow their
	 * subclasses too. Default is {@code false}.
	 */
	boolean strict() default false;
}