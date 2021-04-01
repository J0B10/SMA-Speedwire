package io.github.joblo2213.sma.speedwire.protocol.exceptions;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.Telegram;

/**
 * Exception that indicates any error while parsing a telegram
 */
public abstract class TelegramException extends Exception {
    protected final Telegram telegram;

    public TelegramException(Telegram telegram, String message) {
        super(message);
        this.telegram = telegram;
    }

    public TelegramException(Telegram telegram, String message, Throwable cause) {
        super(message, cause);
        this.telegram = telegram;
    }

    public TelegramException(Telegram telegram, Throwable cause) {
        super(cause);
        this.telegram = telegram;
    }

    /**
     * <p>
     * Returns the telegram that caused this exception.
     * </p><p>
     * <b>Depending on the exception reason the telegram might be incomplete so be carefully with which methods you call.</b>
     * </p><p>
     * The following methods should always be safe to call:<br>
     * <ul>
     *     <li>{@link Telegram#getOrigin()}</li>
     *     <li>{@link Telegram#getBytes()}</li>
     *     <li>{@link Telegram#getUnsigned()}</li>
     *     <li>{@link Telegram#length()}</li>
     *     <li>{@link Telegram#toString()}</li>
     * </ul></p>
     *
     * @return telegram that caused this
     */
    public Telegram getTelegram() {
        return telegram;
    }
}
