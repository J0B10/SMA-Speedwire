import de.ungefroren.sma.speedwire.Speedwire;
import de.ungefroren.sma.speedwire.protocol.measuringChannels.EnergyMeterChannels;
import de.ungefroren.sma.speedwire.protocol.telegrams.EnergyMeterTelegram;
import tech.units.indriya.unit.Units;

import javax.measure.MetricPrefix;
import javax.measure.quantity.Energy;
import java.io.IOException;

public class EnergyMeterSample {
    public static void main(String[] args) throws IOException {
        Speedwire speedwire = new Speedwire();
        speedwire.onError(Exception::printStackTrace);
        speedwire.onTimeout(() -> System.out.println("Speedwire timed out"));
        speedwire.onData(data -> {
            if (data instanceof EnergyMeterTelegram) {
                EnergyMeterTelegram energyMeterData = (EnergyMeterTelegram) data;
                System.out.println("IP: " + energyMeterData.getOrigin().getHostAddress());
                System.out.println("SUSy-ID: " + energyMeterData.getSUSyID());
                System.out.println("Ser-No: " + energyMeterData.getSerNo());
                System.out.println("Software: " + energyMeterData.getSoftwareVersion());
                System.out.println("Time: " + energyMeterData.getMeasuringTime());
                System.out.println(EnergyMeterChannels.TOTAL_P_IN + " : "
                        + energyMeterData.getData(EnergyMeterChannels.TOTAL_P_IN).to(Units.WATT));
                System.out.println(EnergyMeterChannels.TOTAL_P_IN_SUM + " : "
                        + energyMeterData.getData(EnergyMeterChannels.TOTAL_P_IN_SUM)
                        .to(MetricPrefix.KILO(Units.WATT).multiply(Units.HOUR).asType(Energy.class)));
            }
        });
        speedwire.start();
    }
}
