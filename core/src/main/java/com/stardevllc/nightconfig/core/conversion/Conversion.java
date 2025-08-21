package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Indicates that the value of the field must be converted with the specified converter class.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Conversion {
	/**
	 * The conversion class used to convert the field's value when it is put into/read from a
	 * configuration
	 *
	 * @return the conversion class
	 */
	Class<? extends Converter<?, ?>> value();
}
