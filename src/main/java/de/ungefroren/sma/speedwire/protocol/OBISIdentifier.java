package de.ungefroren.sma.speedwire.protocol;

import java.util.Objects;

public class OBISIdentifier {
    private final int channel;
    private final int index;
    private final int type;
    private final int tariff;

    public OBISIdentifier(int channel, int index, int type, int tariff) {
        this.channel = channel;
        this.index = index;
        this.type = type;
        this.tariff = tariff;
    }

    public OBISIdentifier(byte[] data) {
        if (data.length != 4) throw new IllegalArgumentException("obis identifiers consist of 4 bytes");
        this.channel = Byte.toUnsignedInt(data[0]);
        this.index = Byte.toUnsignedInt(data[1]);
        this.type = Byte.toUnsignedInt(data[2]);
        this.tariff = Byte.toUnsignedInt(data[3]);
    }

    public int getChannel() {
        return channel;
    }

    public int getIndex() {
        return index;
    }

    public int getType() {
        return type;
    }

    public int getTariff() {
        return tariff;
    }

    public int getDataLength() {
        return getType();
    }

    @Override
    public String toString() {
        return channel + ":" + index + "." + type + "." + tariff;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OBISIdentifier that = (OBISIdentifier) o;
        return channel == that.channel && index == that.index && type == that.type && tariff == that.tariff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, index, type, tariff);
    }
}
