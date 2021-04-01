package io.github.joblo2213.sma.speedwire;

import io.github.joblo2213.sma.speedwire.protocol.exceptions.TelegramInvalidException;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.IllegalBlockingModeException;

/**
 * <p>
 * The callback that is run on any occurring exceptions while reading incoming packets from sma speedwire.
 * </p><p>
 * If you want to listen for specific types of exceptions, check the type with {@code instanceof}.
 * </p><p>
 * Likely exceptions are:<br>
 * <ul>
 * <li>{@link TelegramInvalidException} - if an incoming packet can't be parsed as valid speedwire telegram</li>
 * <li>{@link IOException} – if an I/O error occurs.</li>
 * <li>{@link PortUnreachableException} –   may be thrown if the socket is connected to a currently unreachable destination.
 * Note, there is no guarantee that the exception will be thrown.</li>
 * <li>{@link IllegalBlockingModeException} –   if the socket has an associated channel,
 * and the channel is in non-blocking mode.</li>
 * </ul></p><p>
 * <b>Example:</b>
 * <pre>{@code
 * speedwire.onError(e -> {
 *    if (e instanceof IOException) {
 *        //io error
 *        // ...
 *    } else if (e instanceof InvalidTelegramException) {
 *        //packet parsing error
 *        // ...
 *    }
 * });
 * }</pre>
 */
public interface SpeedwireErrorHandler {

    /**
     * method called upon any exception while receiving or parsing data
     *
     * @param e exception that was thrown
     */
    void onError(Exception e);
}
