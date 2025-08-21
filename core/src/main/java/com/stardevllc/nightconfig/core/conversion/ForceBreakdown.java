package com.stardevllc.nightconfig.core.conversion;

import java.lang.annotation.*;

/**
 * Indicates that a field must be broken down into its fields instead of being stored as it is,
 * even if its type is supported by the configuration we try to put the field's value into.
 *
 * @deprecated Use the new package {@link com.stardevllc.nightconfig.core.serde} with {@code serde.annotations}.
 * @author TheElectronWill
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForceBreakdown {}