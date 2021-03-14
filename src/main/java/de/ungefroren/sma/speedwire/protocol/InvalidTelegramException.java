package de.ungefroren.sma.speedwire.protocol;

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
