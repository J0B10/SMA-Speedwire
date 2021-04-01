package io.github.joblo2213.sma.speedwire.protocol.exceptions;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.Telegram;

/**
 * Indicates an error while parsing a telegram.<br>
 * Telegram was corrupted.
 */
public class TelegramInvalidException extends TelegramException {

    public TelegramInvalidException(Telegram telegram, String message) {
        super(telegram, message);
    }

    public TelegramInvalidException(Telegram telegram, String message, Throwable cause) {
        super(telegram, message, cause);
    }

    public TelegramInvalidException(Telegram telegram, Throwable cause) {
        super(telegram, cause);
    }
}
