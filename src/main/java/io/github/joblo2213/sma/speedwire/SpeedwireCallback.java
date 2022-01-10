package io.github.joblo2213.sma.speedwire;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.*;

/**
 * <p>
 * The callback that is run on any received data packets over sma speedwire.
 * </p><p>
 * You can listen for specific telegrams using {@link Speedwire#onData(Class, SpeedwireCallback)}.
 * </p><p>
 * Currently implemented telegrams:<br>
 * <ul>
 *     <li>{@link DiscoveryResponse}</li>
 *     <li>{@link EnergyMeterTelegram}</li>
 * </ul></p><p>
 * <b>Example:</b>
 * <pre>{@code
 * speedwire.onData(DiscoveryResponse.class, data -> {
 *    //incoming data is a discovery response. Log the ip.
 *    System.out.println("Found device on " + data.getOrigin().getHostAddress());
 * });
 * speedwire.onData(EnergyMeterTelegram.class, data -> {
 *    //handle energy meter data
 *    // ...
 * });
 * }</pre>
 * @param <T> type of the telegram implementation for which the callback is registered
 */
public interface SpeedwireCallback<T extends Telegram> {

    /**
     * method called upon received data
     *
     * @param data data parsed as valid telegram
     */
    void onDataReceived(T data);
}
