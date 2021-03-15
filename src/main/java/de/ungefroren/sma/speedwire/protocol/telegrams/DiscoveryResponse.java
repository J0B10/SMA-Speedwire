package de.ungefroren.sma.speedwire.protocol.telegrams;

import de.ungefroren.sma.speedwire.Speedwire;
import de.ungefroren.sma.speedwire.protocol.InvalidTelegramException;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * This telegram is send by all SMA devices that support the speedwire protocol if they receive a discovery request.<br>
 * For sending discovery requests, use {@link Speedwire#sendDiscoveryRequest()}
 */
public class DiscoveryResponse extends Telegram {

    private static final byte[] SUBARRAY = DatatypeConverter.parseHexBinary("534d4100000402A000000001000200000001");

    DiscoveryResponse(InetAddress origin, byte[] data) throws InvalidTelegramException {
        super(origin, data);
    }

    @Override
    protected void validate() throws InvalidTelegramException {
        super.validate();
        if (!Arrays.equals(getBytes(0, 18), SUBARRAY))
            throw new InvalidTelegramException("Not a discovery response");

    }
}
