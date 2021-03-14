package de.ungefroren.sma.speedwire.protocol.telegrams;

import de.ungefroren.sma.speedwire.protocol.InvalidTelegramException;
import de.ungefroren.sma.speedwire.protocol.OBISIdentifier;
import de.ungefroren.sma.speedwire.protocol.measuringChannels.MeasuringChannel;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.HashMap;

import static de.ungefroren.sma.speedwire.protocol.measuringChannels.EnergyMeterChannels.*;

public class EnergyMeterTelegram extends Telegram {

    private final int SUSyID;
    private final BigInteger serNo;
    private final Quantity<Time> measuringTime;
    private final HashMap<OBISIdentifier, BigInteger> measuredData;
    private String softwareVersion = "unknown";

    EnergyMeterTelegram(InetAddress origin, byte[] data) throws InvalidTelegramException {
        super(origin, data);
        try {
            SUSyID = get2ByteUnsignedInt(18);
            serNo = get4ByteUnsignedInt(20);
            measuringTime = Quantities.getQuantity(get4ByteUnsignedInt(24), UNIT_TIME);

            measuredData = new HashMap<>();
            loadMeasurements(28, length() - 4);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidTelegramException(e);
        }
    }

    @Override
    protected void validate() throws InvalidTelegramException {
        super.validate();
        try {

            //Tag: "SMA Net 2", version 0 (0x0010) is set
            if (get2ByteUnsignedInt(14) != 0x0010)
                throw new InvalidTelegramException("telegram doesn't contain SMA Net 2 protocol data");

            //ProtocolID 0x6069 (energy meter protocol) is set
            if (get2ByteUnsignedInt(16) != 0x6069)
                throw new InvalidTelegramException("protocol id isn't 0x6069");

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidTelegramException(e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void loadMeasurements(int from, int to) throws InvalidTelegramException {
        for (int offset = from; offset < to; ) {
            OBISIdentifier identifier = new OBISIdentifier(getBytes(offset, 4));

            if (identifier.equals(new OBISIdentifier(144, 0,0,0))) {
                int major = getUnsigned(offset + 4);
                int minor = getUnsigned(offset + 5);
                int patch = getUnsigned(offset + 6);
                char revision = (char) getByte(offset + 7);
                softwareVersion =  major + "." + minor + "." + patch + "." + revision;
                offset += 8;
                continue;
            }

            BigInteger value;
            switch (identifier.getDataLength()) {
                case 4: value = get4ByteUnsignedInt(offset + 4); break;
                case 8: value = get8ByteUnsignedInt(offset + 4); break;
                default: throw new InvalidTelegramException("invalid identifier (unknown type): " + identifier);
            }
            measuredData.put(identifier, value);
            offset += 4 + identifier.getDataLength();
        }

        for (MeasuringChannel<?> channel : ALL) {
            if (!measuredData.containsKey(channel.getIdentifier())) {
                //log all missing channels
                System.err.println("telegram missing channel: " + channel.getIdentifier());
            }
        }
    }

    public int getSUSyID() {
        return SUSyID;
    }

    public BigInteger getSerNo() {
        return serNo;
    }

    public Quantity<Time> getMeasuringTime() {
        return measuringTime;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public <Q extends Quantity<Q>> Quantity<Q> getData(MeasuringChannel<Q> channel) throws IllegalArgumentException {
        BigInteger value = measuredData.get(channel.getIdentifier());
        if (value == null) throw new IllegalArgumentException("channel '" + channel + "' is not defined");
        return Quantities.getQuantity(value, channel.getUnit());
    }


}
