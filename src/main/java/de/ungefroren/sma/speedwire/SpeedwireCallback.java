package de.ungefroren.sma.speedwire;

import de.ungefroren.sma.speedwire.protocol.telegrams.*;

/**
 * <p>
 * The callback that is run on any received data packets over sma speedwire.
 * </p><br><p>
 * If you want to listen for specific telegrams or parse special data, check if the provided data is of a
 * specific subclass of telegram.
 * </p><br><p>
 * Currently implemented telegrams:<br>
 * {@link DiscoveryResponse}, {@link EnergyMeterTelegram}
 * </p><br><p>
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
