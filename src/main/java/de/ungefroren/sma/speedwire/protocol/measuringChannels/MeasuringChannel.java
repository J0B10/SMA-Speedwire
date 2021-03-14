package de.ungefroren.sma.speedwire.protocol.measuringChannels;

import de.ungefroren.sma.speedwire.protocol.OBISIdentifier;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Objects;

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

    public OBISIdentifier getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public Unit<Q> getUnit() {
        return unit;
    }

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
