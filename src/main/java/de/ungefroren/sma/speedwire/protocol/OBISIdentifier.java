package de.ungefroren.sma.speedwire.protocol;

import java.util.Objects;

/**
 * <p>
 * OBIS identifiers are used to identify measurement values in a telegram.<br>
 * They are defined in the <b>ICE 62056-61</b> standard.
 * </p><br><p>
 * Each OBIS identifier consists of the following groups:<br>
 * <i><b>Group A</b> - specifies the medium (1 = electricity)</i> <b>omitted</b><br>
 * <b>Group B</b> - specifies the channel (default 0)<br>
 * <b>Group C</b> - specifies the index of the physical value<br>
 * <b>Group D</b> - specifies the type of measurement<br>
 * <b>Group E</b> - specifies the tariff (default 0)<br>
 * </p>
 */
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
        if (channel > 256 || channel < 0 || index > 256 || index < 0
                || type > 256 || type < 0 || tariff > 256 || tariff < 0)
            throw new IllegalArgumentException("Only unsigned byte values (>=0, <256) are allowed");
    }

    public OBISIdentifier(byte[] data) {
        if (data.length != 4) throw new IllegalArgumentException("obis identifiers consist of 4 bytes");
        this.channel = Byte.toUnsignedInt(data[0]);
        this.index = Byte.toUnsignedInt(data[1]);
        this.type = Byte.toUnsignedInt(data[2]);
        this.tariff = Byte.toUnsignedInt(data[3]);
    }

    /**
     * <p>
     * <b>OBIS group B</b><br>
     * Some devices have multiple measurement channels that generate multiple measurement results for the same index.<br>
     * These devices can separate the results into different channels.
     * </p><br><p>
     * SMA Devices initially use channel 0 for internal measurements and can use additional channels for additional
     * connected circuits.
     * </p>
     *
     * @return value between 0 and 255 specifying the identifiers channel (default 0)
     */
    public int getChannel() {
        return channel;
    }

    /**
     * <b>OBIS group C</b><br>
     * Each physical value that is measured (current, voltage, power, ...) has a specific index
     *
     * @return value between 0 and 255 specifying the identifiers value index
     */
    public int getIndex() {
        return index;
    }

    /**
     * <b>OBIS group D</b><br>
     * Specifies the type of measurement.<br>
     * Known types are 4 (indicates the last measured average value) and 8 (indicates a cumulative meter reading).
     *
     * @return value between 0 and 255 specifying identifiers measurement type
     */
    public int getType() {
        return type;
    }

    /**
     * <b>OBIS group E</b><br>
     * Specifies the tariff.<br>
     * Tariff can be ignored for most applications and is 0 by default.
     *
     * @return value between 0 and 255 specifying the tariff (default 0)
     */
    public int getTariff() {
        return tariff;
    }

    /**
     * Returns the length of the data that belongs to this identifier in bytes
     */
    public int getDataLength() {
        return getType();
    }

    /**
     * <p>
     * Returns the identifier as String with the syntax B:C.D.E
     * </p><br><p>
     * <b>Example:</b> {@code 0:14.4.0} (net frequency)
     * </p>
     */
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
