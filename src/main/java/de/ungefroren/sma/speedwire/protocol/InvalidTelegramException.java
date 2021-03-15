package de.ungefroren.sma.speedwire.protocol;

/**
 * Indicates an error while parsing a telegram.<br>
 * This may be because the telegram does not match the type that is tried to parse or because it was corrupted.
 */
public class InvalidTelegramException extends Exception {

    public InvalidTelegramException(String message) {
        super(message);
    }

    public InvalidTelegramException(Throwable cause) {
        super(cause);
    }

    public InvalidTelegramException(String message, Throwable cause) {
        super(message, cause);
    }
}
