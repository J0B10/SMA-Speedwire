package io.github.joblo2213.sma.speedwire;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.*;

/**
 * <p>
 * The callback that is run on any received data packets over sma speedwire.
 * </p><p>
 * If you want to listen for specific telegrams or parse special data, check if the provided data is of a
 * specific subclass of telegram.
 * </p><p>
 * Currently implemented telegrams:<br>
 * <ul>
 *     <li>{@link DiscoveryResponse}</li>
 *     <li>{@link EnergyMeterTelegram}</li>
 * </ul></p><p>
 * <b>Example:</b>
 * <pre>{@code
 * speedwire.onData(data -> {
 *    if (data instanceof DiscoveryResponse) {
 *        //incoming data is a discovery response. Log the ip.
 *        System.out.println("Found device on " + data.getOrigin().getHostAddress());
 *    } else if (data instanceof EnergyMeterTelegram) {
 *        EnergyMeterTelegram emData = (EnergyMeterTelegram) data;
 *        //handle energy meter data
 *        // ...
 *    }
 * });
 * }</pre>
 */
public interface SpeedwireCallback {

    /**
     * method called upon received data
     *
     * @param data data parsed as valid telegram
     */
    void onDataReceived(Telegram data);
}
