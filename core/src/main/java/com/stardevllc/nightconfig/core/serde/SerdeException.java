package com.stardevllc.nightconfig.core.serde;

/**
 * Thrown when an error occurs during the serialization
 * or deserialization process.
 */
public class SerdeException extends RuntimeException {
    public SerdeException(String message) {
        super(message);
    }

    public SerdeException(String message, Throwable cause) {
        super(message, cause);
    }
}
