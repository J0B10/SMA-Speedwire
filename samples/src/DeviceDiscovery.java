import io.github.joblo2213.sma.speedwire.Speedwire;
import io.github.joblo2213.sma.speedwire.protocol.telegrams.DiscoveryResponse;

import java.io.IOException;

/**
 * This sample demonstrates a simple way to detect all speedwire devices in the local network.
 */
public class DeviceDiscovery {

    public static void main(String[] args) throws IOException, InterruptedException {
        Speedwire speedwire = new Speedwire();
        speedwire.onError(Exception::printStackTrace);
        speedwire.onTimeout(() -> System.err.println("speedwire timeout"));
        speedwire.onData(DiscoveryResponse.class, data ->
                System.out.println("Device detected with ip " + data.getOrigin().getHostAddress())
        );
        speedwire.start();
        speedwire.sendDiscoveryRequest();
        Thread.sleep(5_000);
        speedwire.shutdown();
    }
}
