package de.ungefroren.sma.speedwire.protocol.measuringChannels;

import de.ungefroren.sma.speedwire.protocol.OBISIdentifier;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Objects;

/**
 * All measured data is divided into channels.<br>
 * This class defines the identifier, the description, the unit and the physical quantity of a channel.
 *
 * @param <Q> the physical quantity measured by this channel (e.g. Power, ElectricalCurrent, Frequency, ...)
 */
public class MeasuringChannel<Q extends Quantity<Q>> {
    private final OBISIdentifier identifier;
    private final String description;
    private final Unit<Q> unit;

    public MeasuringChannel(OBISIdentifier identifier, String description, Unit<Q> unit) {
        this.identifier = identifier;
        this.description = description;
        this.unit = unit;
    }

    public MeasuringChannel(int channel, int index, int type, int tariff, String description, Unit<Q> unit) {
        this(new OBISIdentifier(channel, index, type, tariff), description, unit);
    }

    public MeasuringChannel(int index, int type, int tariff, String description, Unit<Q> unit) {
        this(0, index, type, tariff, description, unit);
    }

    /**
     * Returns the OBIS identifier of this channel
     */
    public OBISIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Returns a short description of this channel
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unit of the data in this channel
     */
    public Unit<Q> getUnit() {
        return unit;
    }

    /**
     * Returns the amount of bytes a value of this channel has
     */
    public int getDataLength() {
        return getIdentifier().getDataLength();
    }

    @Override
    public String toString() {
        return identifier + " - " + description + " [" + unit.toString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MeasuringChannel<?> that = (MeasuringChannel<?>) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
