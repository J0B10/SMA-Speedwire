import de.ungefroren.sma.speedwire.Speedwire;
import de.ungefroren.sma.speedwire.protocol.measuringChannels.EnergyMeterChannels;
import de.ungefroren.sma.speedwire.protocol.telegrams.EnergyMeterTelegram;
import tech.units.indriya.unit.Units;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import java.io.IOException;

/**
 * This sample demonstrates how to log the current power draw and the total energy reading of a SMA Energy Meter.<br>
 * Data from other channels of the Energy Meter can be retrieved the same way.
 */
public class EnergyMeterSample {

    public static void main(String[] args) throws IOException {
        Speedwire speedwire = new Speedwire();
        speedwire.onError(Exception::printStackTrace);
        speedwire.onTimeout(() -> System.err.println("speedwire timeout"));
        speedwire.onData(data -> {
            if (data instanceof EnergyMeterTelegram) {
                EnergyMeterTelegram em = (EnergyMeterTelegram) data;

                //device information
                int SUSyID = em.getSUSyID();
                long SerNo = em.getSerNo().longValueExact();
                String ip = em.getOrigin().getHostAddress();
                System.out.printf("Device %d %d on port %s%n", SUSyID, SerNo, ip);

                //current power draw (in W)
                Quantity<Power> w = em.getData(EnergyMeterChannels.TOTAL_P_IN).to(Units.WATT);
                System.out.printf("Ingress Power: %s%n", w);

                //energy meter total power reading (in kWh)
                Quantity<Energy> powerReading = em.getData(EnergyMeterChannels.TOTAL_P_IN_SUM)
                        .to(MetricPrefix.KILO(Units.WATT).multiply(Units.HOUR).asType(Energy.class));
                System.out.printf("Total power reading: %s%n", powerReading);
            }
        });
        speedwire.start();
    }
}
