package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Specifies that the value of a field must not be null.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecNotNull {}