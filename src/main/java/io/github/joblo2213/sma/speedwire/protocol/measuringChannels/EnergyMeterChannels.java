package io.github.joblo2213.sma.speedwire.protocol.measuringChannels;


import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.Units;

import javax.measure.MetricPrefix;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import java.util.List;

/**
 * This interface provides a collection of all measuring channels supported by SMA Energy Meters and
 * SMA Sunny Home Manager (2.0), as well as the units of the measurements.
 */
public interface EnergyMeterChannels {

    /**
     * The Energy Meter provides power values in [0.1W]
     */
    Unit<Power> UNIT_POWER = Units.WATT.multiply(0.1);

    /**
     * The Energy Meter provides energy values in [Ws]
     */
    Unit<Energy> UNIT_ENERGY = Units.WATT.multiply(Units.SECOND).asType(Energy.class);

    /**
     * The Energy Meter provides electric current values in [mA]
     */
    Unit<ElectricCurrent> UNIT_CURRENT = MetricPrefix.MILLI(Units.AMPERE);

    /**
     * The Energy Meter provides voltage values in [mV]
     */
    Unit<ElectricPotential> UNIT_VOLTAGE = MetricPrefix.MILLI(Units.VOLT);

    /**
     * The Energy Meter provides power factor values [cos(φ)] without unit, multiplied by 0.001
     */
    Unit<Dimensionless> UNIT_FACTOR = AbstractUnit.ONE.multiply(0.001);

    /**
     * The Energy Meter provides time measurements in [ms]
     */
    Unit<Time> UNIT_TIME = MetricPrefix.MILLI(Units.SECOND);

    /**
     * The Energy Meter provides frequency measurements in [0.001 Hz]
     */
    Unit<Frequency> UNIT_FREQUENCY = Units.HERTZ.multiply(0.001);

    /**
     * <b>OBIS 0:1.4.0</b> - current total ingress power in [0.1W]
     */
    MeasuringChannel<Power> TOTAL_P_IN = new MeasuringChannel<>(1, 4, 0, "current total ingress power", UNIT_POWER);

    /**
     * <b>OBIS 0:1.8.0</b> - total ingress energy sum in [Ws]
     */
    MeasuringChannel<Energy> TOTAL_P_IN_SUM = new MeasuringChannel<>(1, 8, 0, "total ingress energy sum", UNIT_ENERGY);

    /**
     * <b>OBIS 0:2.4.0</b> - current total egress power in [0.1W]
     */
    MeasuringChannel<Power> TOTAL_P_OUT = new MeasuringChannel<>(2, 4, 0, "current total egress power", UNIT_POWER);

    /**
     * <b>OBIS 0:2.8.0</b> - total egress energy sum in [Ws]
     */
    MeasuringChannel<Energy> TOTAL_P_OUT_SUM = new MeasuringChannel<>(2, 8, 0, "total egress energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> TOTAL_Q_IN = new MeasuringChannel<>(3, 4, 0, "current total ingress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> TOTAL_Q_IN_SUM = new MeasuringChannel<>(3, 8, 0, "total ingress reactive energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> TOTAL_Q_OUT = new MeasuringChannel<>(4, 4, 0, "current total egress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> TOTAL_Q_OUT_SUM = new MeasuringChannel<>(4, 8, 0, "total egress reactive energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> TOTAL_S_IN = new MeasuringChannel<>(9, 4, 0, "current total ingress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> TOTAL_S_IN_SUM = new MeasuringChannel<>(9, 8, 0, "total ingress apparent energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> TOTAL_S_OUT = new MeasuringChannel<>(10, 4, 0, "current total egress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> TOTAL_S_OUT_SUM = new MeasuringChannel<>(10, 8, 0, "total egress apparent energy sum", UNIT_ENERGY);

    MeasuringChannel<Dimensionless> TOTAL_POWER_FACTOR = new MeasuringChannel<>(13, 4, 0, "current power factor", UNIT_FACTOR);
    MeasuringChannel<Frequency> NET_FREQUENCY = new MeasuringChannel<>(14, 4, 0, "current net frequency", UNIT_FREQUENCY);

    MeasuringChannel<Power> L1_P_IN = new MeasuringChannel<>(21, 4, 0, "current phase 1 ingress power", UNIT_POWER);
    MeasuringChannel<Energy> L1_P_IN_SUM = new MeasuringChannel<>(21, 8, 0, "phase 1 ingress energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L1_P_OUT = new MeasuringChannel<>(22, 4, 0, "current phase 1 egress power", UNIT_POWER);
    MeasuringChannel<Energy> L1_P_OUT_SUM = new MeasuringChannel<>(22, 8, 0, "phase 1 egress energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L1_Q_IN = new MeasuringChannel<>(23, 4, 0, "current phase 1 ingress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L1_Q_IN_SUM = new MeasuringChannel<>(23, 8, 0, "phase 1 ingress reactive energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L1_Q_OUT = new MeasuringChannel<>(24, 4, 0, "current phase 1 egress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L1_Q_OUT_SUM = new MeasuringChannel<>(24, 8, 0, "phase 1 egress reactive energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L1_S_IN = new MeasuringChannel<>(29, 4, 0, "current phase 1 ingress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L1_S_IN_SUM = new MeasuringChannel<>(29, 8, 0, "phase 1 ingress apparent energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L1_S_OUT = new MeasuringChannel<>(30, 4, 0, "current phase 1 egress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L1_S_OUT_SUM = new MeasuringChannel<>(30, 8, 0, "phase 1 egress apparent energy sum", UNIT_ENERGY);

    MeasuringChannel<ElectricCurrent> L1_CURRENT = new MeasuringChannel<>(31, 4, 0, "current phase 1 electric current", UNIT_CURRENT);
    MeasuringChannel<ElectricPotential> L1_VOLTAGE = new MeasuringChannel<>(32, 4, 0, "current phase 1 electric voltage", UNIT_VOLTAGE);
    MeasuringChannel<Dimensionless> L1_POWER_FACTOR = new MeasuringChannel<>(33, 4, 0, "current phase 1 power factor", UNIT_FACTOR);

    MeasuringChannel<Power> L2_P_IN = new MeasuringChannel<>(41, 4, 0, "current phase 2 ingress power", UNIT_POWER);
    MeasuringChannel<Energy> L2_P_IN_SUM = new MeasuringChannel<>(41, 8, 0, "phase 2 ingress energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L2_P_OUT = new MeasuringChannel<>(42, 4, 0, "current phase 2 egress power", UNIT_POWER);
    MeasuringChannel<Energy> L2_P_OUT_SUM = new MeasuringChannel<>(42, 8, 0, "phase 2 egress energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L2_Q_IN = new MeasuringChannel<>(43, 4, 0, "current phase 2 ingress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L2_Q_IN_SUM = new MeasuringChannel<>(43, 8, 0, "phase 2 ingress reactive energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L2_Q_OUT = new MeasuringChannel<>(44, 4, 0, "current phase 2 egress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L2_Q_OUT_SUM = new MeasuringChannel<>(44, 8, 0, "phase 2 egress reactive energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L2_S_IN = new MeasuringChannel<>(49, 4, 0, "current phase 2 ingress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L2_S_IN_SUM = new MeasuringChannel<>(49, 8, 0, "phase 2 ingress apparent energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L2_S_OUT = new MeasuringChannel<>(50, 4, 0, "current phase 2 egress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L2_S_OUT_SUM = new MeasuringChannel<>(50, 8, 0, "phase 2 egress apparent energy sum", UNIT_ENERGY);

    MeasuringChannel<ElectricCurrent> L2_CURRENT = new MeasuringChannel<>(51, 4, 0, "current phase 2 electric current", UNIT_CURRENT);
    MeasuringChannel<ElectricPotential> L2_VOLTAGE = new MeasuringChannel<>(52, 4, 0, "current phase 2 electric voltage", UNIT_VOLTAGE);
    MeasuringChannel<Dimensionless> L2_POWER_FACTOR = new MeasuringChannel<>(53, 4, 0, "current phase 2 power factor", UNIT_FACTOR);

    MeasuringChannel<Power> L3_P_IN = new MeasuringChannel<>(61, 4, 0, "current phase 3 ingress power", UNIT_POWER);
    MeasuringChannel<Energy> L3_P_IN_SUM = new MeasuringChannel<>(61, 8, 0, "phase 3 ingress energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L3_P_OUT = new MeasuringChannel<>(62, 4, 0, "current phase 3 egress power", UNIT_POWER);
    MeasuringChannel<Energy> L3_P_OUT_SUM = new MeasuringChannel<>(62, 8, 0, "phase 3 egress energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L3_Q_IN = new MeasuringChannel<>(63, 4, 0, "current phase 3 ingress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L3_Q_IN_SUM = new MeasuringChannel<>(63, 8, 0, "phase 3 ingress reactive energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L3_Q_OUT = new MeasuringChannel<>(64, 4, 0, "current phase 3 egress reactive power", UNIT_POWER);
    MeasuringChannel<Energy> L3_Q_OUT_SUM = new MeasuringChannel<>(64, 8, 0, "phase 3 egress reactive energy sum", UNIT_ENERGY);

    MeasuringChannel<Power> L3_S_IN = new MeasuringChannel<>(69, 4, 0, "current phase 3 ingress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L3_S_IN_SUM = new MeasuringChannel<>(69, 8, 0, "phase 3 ingress apparent energy sum", UNIT_ENERGY);
    MeasuringChannel<Power> L3_S_OUT = new MeasuringChannel<>(70, 4, 0, "current phase 3 egress apparent power", UNIT_POWER);
    MeasuringChannel<Energy> L3_S_OUT_SUM = new MeasuringChannel<>(70, 8, 0, "phase 3 egress apparent energy sum", UNIT_ENERGY);

    MeasuringChannel<ElectricCurrent> L3_CURRENT = new MeasuringChannel<>(71, 4, 0, "current phase 3 electric current", UNIT_CURRENT);
    MeasuringChannel<ElectricPotential> L3_VOLTAGE = new MeasuringChannel<>(72, 4, 0, "current phase 3 electric voltage", UNIT_VOLTAGE);
    MeasuringChannel<Dimensionless> L3_POWER_FACTOR = new MeasuringChannel<>(73, 4, 0, "current phase 3 power factor", UNIT_FACTOR);

    /**
     * This list contains all channels provided by the energy meter telegram
     */
    List<MeasuringChannel<?>> ALL = List.of(
            TOTAL_P_IN, TOTAL_P_OUT, TOTAL_Q_IN, TOTAL_Q_OUT, TOTAL_S_IN, TOTAL_S_OUT, TOTAL_P_IN_SUM, TOTAL_P_OUT_SUM,
            TOTAL_Q_IN_SUM, TOTAL_Q_OUT_SUM, TOTAL_S_IN_SUM, TOTAL_S_OUT_SUM, TOTAL_POWER_FACTOR, NET_FREQUENCY,
            L1_P_IN, L1_P_OUT, L1_Q_IN, L1_Q_OUT, L1_S_IN, L1_S_OUT, L1_P_IN_SUM, L1_P_OUT_SUM,
            L1_Q_IN_SUM, L1_Q_OUT_SUM, L1_S_IN_SUM, L1_S_OUT_SUM, L1_CURRENT, L1_VOLTAGE, L1_POWER_FACTOR,
            L2_P_IN, L2_P_OUT, L2_Q_IN, L2_Q_OUT, L2_S_IN, L2_S_OUT, L2_P_IN_SUM, L2_P_OUT_SUM,
            L2_Q_IN_SUM, L2_Q_OUT_SUM, L2_S_IN_SUM, L2_S_OUT_SUM, L2_CURRENT, L2_VOLTAGE, L2_POWER_FACTOR,
            L3_P_IN, L3_P_OUT, L3_Q_IN, L3_Q_OUT, L3_S_IN, L3_S_OUT, L3_P_IN_SUM, L3_P_OUT_SUM,
            L3_Q_IN_SUM, L3_Q_OUT_SUM, L3_S_IN_SUM, L3_S_OUT_SUM, L3_CURRENT, L3_VOLTAGE, L3_POWER_FACTOR
    );
}
