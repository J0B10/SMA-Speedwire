package io.github.joblo2213.sma.speedwire.protocol.exceptions;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.Telegram;

/**
 * Indicates an error while parsing a telegram.<br>
 * Telegram doesn't match the type that is tried to parse.
 */
public class TelegramMismatchException extends TelegramException {

    public TelegramMismatchException(Telegram telegram, String message) {
        super(telegram, message);
    }

    public TelegramMismatchException(Telegram telegram, String message, Throwable cause) {
        super(telegram, message, cause);
    }

    public TelegramMismatchException(Telegram telegram, Throwable cause) {
        super(telegram, cause);
    }
}
