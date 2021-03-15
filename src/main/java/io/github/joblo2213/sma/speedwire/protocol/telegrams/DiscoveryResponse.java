package io.github.joblo2213.sma.speedwire.protocol.telegrams;

import io.github.joblo2213.sma.speedwire.Speedwire;
import io.github.joblo2213.sma.speedwire.protocol.InvalidTelegramException;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * This telegram is send by all SMA devices that support the speedwire protocol if they receive a discovery request.<br>
 * For sending discovery requests, use {@link Speedwire#sendDiscoveryRequest()}
 */
public class DiscoveryResponse extends Telegram {

    private static final byte[] SUBARRAY = new byte[]{
            (byte) 0x53, (byte) 0x4d, (byte) 0x41, (byte) 0x00,
            (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0xA0,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x01
    };

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
