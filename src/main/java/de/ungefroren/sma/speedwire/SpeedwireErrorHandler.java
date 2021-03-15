package de.ungefroren.sma.speedwire;

import de.ungefroren.sma.speedwire.protocol.InvalidTelegramException;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.IllegalBlockingModeException;

/**
 * <p>
 * The callback that is run on any occurring exceptions while reading incoming packets from sma speedwire.
 * </p><br><p>
 * If you want to listen for specific types of exceptions, check the type with {@code instanceof}.
 * </p><br><p>
 * Likely exceptions are:<br>
 * {@link InvalidTelegramException} - if an incoming packet can't be parsed as valid speedwire telegram
 * {@link IOException} – if an I/O error occurs.<br>
 * {@link PortUnreachableException} –   may be thrown if the socket is connected to a currently unreachable destination.
 * Note, there is no guarantee that the exception will be thrown.<br>
 * {@link IllegalBlockingModeException} –   if the socket has an associated channel,
 * and the channel is in non-blocking mode.<br>
 * </p><br><p>
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
