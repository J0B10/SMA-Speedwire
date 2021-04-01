package io.github.joblo2213.sma.speedwire.protocol.telegrams;

import io.github.joblo2213.sma.speedwire.protocol.OBISIdentifier;
import io.github.joblo2213.sma.speedwire.protocol.exceptions.TelegramInvalidException;
import io.github.joblo2213.sma.speedwire.protocol.exceptions.TelegramMismatchException;
import io.github.joblo2213.sma.speedwire.protocol.measuringChannels.EnergyMeterChannels;
import io.github.joblo2213.sma.speedwire.protocol.measuringChannels.MeasuringChannel;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * A telegram from an SMA Energy Meter or an SMA Sunny Home Manager (2.0)
 */
public class EnergyMeterTelegram extends Telegram {

    private final int SUSyID;
    private final BigInteger serNo;
    private final Quantity<Time> measuringTime;
    private final HashMap<OBISIdentifier, BigInteger> measuredData;
    private String softwareVersion = "unknown";

    EnergyMeterTelegram(InetAddress origin, byte[] data) throws TelegramInvalidException, TelegramMismatchException {
        super(origin, data);
        try {
            SUSyID = get2ByteUnsignedInt(18);
            serNo = get4ByteUnsignedInt(20);
            measuringTime = Quantities.getQuantity(get4ByteUnsignedInt(24), EnergyMeterChannels.UNIT_TIME);

            measuredData = new HashMap<>();
            loadMeasurements(28, length() - 4);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TelegramInvalidException(this, e);
        }
    }

    @Override
    protected void validate() throws TelegramInvalidException, TelegramMismatchException {
        super.validate();
        try {

            //Tag: "SMA Net 2", version 0 (0x0010) is set
            if (get2ByteUnsignedInt(14) != 0x0010)
                throw new TelegramMismatchException(this, "telegram doesn't contain SMA Net 2 protocol data");

            //ProtocolID 0x6069 (energy meter protocol) is set
            if (get2ByteUnsignedInt(16) != 0x6069)
                throw new TelegramMismatchException(this, "protocol id isn't 0x6069");

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TelegramInvalidException(this, e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void loadMeasurements(int from, int to) throws TelegramInvalidException {
        for (int offset = from; offset < to; ) {
            OBISIdentifier identifier = new OBISIdentifier(getBytes(offset, 4));

            if (identifier.equals(new OBISIdentifier(144, 0, 0, 0))) {
                int major = getUnsigned(offset + 4);
                int minor = getUnsigned(offset + 5);
                int patch = getUnsigned(offset + 6);
                char revision = (char) getByte(offset + 7);
                softwareVersion = major + "." + minor + "." + patch + "." + revision;
                offset += 8;
                continue;
            }

            BigInteger value;
            switch (identifier.getDataLength()) {
                case 4:
                    value = get4ByteUnsignedInt(offset + 4);
                    break;
                case 8:
                    value = get8ByteUnsignedInt(offset + 4);
                    break;
                default:
                    throw new TelegramInvalidException(this, "invalid identifier (unknown type): " + identifier);
            }
            measuredData.put(identifier, value);
            offset += 4 + identifier.getDataLength();
        }

        for (MeasuringChannel<?> channel : EnergyMeterChannels.ALL) {
            if (!measuredData.containsKey(channel.getIdentifier())) {
                //log all missing channels
                System.err.println("telegram missing channel: " + channel.getIdentifier());
            }
        }
    }

    /**
     * <p>
     * Returns the devices SUSy ID.
     * </p><br><p>
     * The SUSy ID is a 2 byte long (unsigned) identifier of SMA hardware, located in the first two bytes of a devices
     * SMA device address.
     * </p>
     */
    public int getSUSyID() {
        return SUSyID;
    }

    /**
     * Returns the devices serial number (4 byte unsigned integer)
     */
    public BigInteger getSerNo() {
        return serNo;
    }

    /**
     * Returns the measuring time of the data provided by the telegram.<br>
     * This 4 byte unsigned integer with ms precision will overflow approximately every 50 days and start again at 0.
     */
    public Quantity<Time> getMeasuringTime() {
        return measuringTime;
    }

    /**
     * <p>
     * Returns the software version string of the smart meter
     * </p><br><p>
     * Syntax:<br>
     * <center><b>Major.Minor.Build.Revision</b></center><br>
     * </p>
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Retrieves measured data of a given channel from the telegram<br>
     * A list of all valid channels can be found in {@link EnergyMeterChannels}.
     *
     * @param channel channel of the data that should be retrieved
     * @return the measured data of the given channel as quantity
     * @throws IllegalArgumentException if the telegram does not contain valid data for the given channel
     */
    public <Q extends Quantity<Q>> Quantity<Q> getData(MeasuringChannel<Q> channel) throws IllegalArgumentException {
        BigInteger value = measuredData.get(channel.getIdentifier());
        if (value == null) throw new IllegalArgumentException("channel '" + channel + "' is not defined");
        return Quantities.getQuantity(value, channel.getUnit());
    }


}
