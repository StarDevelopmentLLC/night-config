package com.stardevllc.nightconfig.core.serde;

import com.stardevllc.nightconfig.core.UnmodifiableConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * Deserialize a {@code Config} to the fields of a Plain-Old-Java-Object (POJO).
 */
final class ConfigToPojoDeserializer
		implements ValueDeserializer<UnmodifiableConfig, Object> {

	@Override
	public Object deserialize(UnmodifiableConfig value, Optional<TypeConstraint> resultType,
                              DeserializerContext ctx) {
		if (!resultType.isPresent()) {
			// no constraint, we don't know the type of the POJO!
			// Assume the easiest result: return the value as is
			return value;
		} else {
			TypeConstraint t = resultType.get();
			Class<?> cls = t.getSatisfyingRawType().orElseThrow(() -> new SerdeException(
					"Could not find a concrete type that can satisfy the constraint " + t));
            Object instance;
            try {
                Constructor<?> constructor = cls.getDeclaredConstructor();
                if (!Modifier.isPublic(constructor.getModifiers())) {
                    constructor.setAccessible(true);
                }
                instance = constructor.newInstance();
            } catch (Exception e) {
                throw new SerdeException("Failed to create an instance of " + cls, e);
            }
            ctx.deserializeFields(value, instance);
            return instance;
		}
	}
}
