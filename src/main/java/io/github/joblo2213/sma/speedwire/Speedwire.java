package io.github.joblo2213.sma.speedwire;

import io.github.joblo2213.sma.speedwire.protocol.telegrams.DiscoveryResponse;
import io.github.joblo2213.sma.speedwire.protocol.telegrams.Telegram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 * Main class for interacting with speedwire devices.
 * </p><p>
 * All devices that use the speedwire protocol send data periodically via
 * <a href="https://en.wikipedia.org/wiki/User_Datagram_Protocol">UDP</a>
 * <a href="https://en.wikipedia.org/wiki/Multicast">Multicast</a>, by default to group {@code 239.12.255.254}
 * on port {@code 9522}.
 * </p><p>
 * To join the multicast group and listen for incoming data call the {@code start()} method.<br>
 * The thread will read all incoming data and parse the telegrams.<br>
 * Register callbacks that receive the parsed telegrams from the thread by using {@code onData()}.<br>
 * Callbacks that handle occurring errors or timeouts can also be registered but aren't mandatory.<br>
 * </p><p>
 * <b>Example:</b>
 * </p><pre>{@code
 * Speedwire sw = new Speedwire();
 * sw.onData(telegram -> {
 *     if (telegram instanceof EnergyMeterTelegram) {
 *         EnergyMeterTelegram em = (EnergyMeterTelegram) telegram;
 *
 *         //log current ingress power of energy meter
 *         System.out.println(em.getData(EnergyMeterChannels.TOTAL_P_IN));
 *     }
 * });
 * //print stacktrace for all occurring exceptions
 * sw.onError(e -> e.printStackTrace());
 * sw.start;
 * }</pre>
 * <p>
 * To find all devices that support the speedwire protocol in your local network
 * send a discovery request using {@code sendDiscoveryRequest()}.<br>
 * Each device will answer with a {@link DiscoveryResponse} that you can listen for using the {@code onData()} callback.
 * </p>
 */
public class Speedwire extends Thread {

    private static final String DEFAULT_GROUP = "239.12.255.254";
    private static final int DEFAULT_PORT = 9522;
    private static final int TIMEOUT = 5000;

    private final InetAddress hostAddress;
    private final InetAddress multicastGroup;
    private final int port;

    private final CopyOnWriteArraySet<SpeedwireCallback> callbacks = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<SpeedwireErrorHandler> errorHandlers = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Runnable> timeoutHandlers = new CopyOnWriteArraySet<>();

    private MulticastSocket socket;

    /**
     * Construct a new thread for exchanging data with speedwire devices in your local network.<br>
     * In most cases the default constructor ({@link #Speedwire()}) will better suit your needs.
     *
     * @param hostAddress    the ip address of the host (the device you are running this program on)
     *                       in your local network
     * @param multicastGroup the ip address of the multicast group to which all speedwire data is send,
     *                       by default this is {@code 239.12.255.254}
     * @param port           the iana registered udp port over which all speedwire data is send and received.
     *                       Default port is {@code 9522}
     * @throws IOException if one of the given addresses isn't a valid inet address or the {@code multicastGroup}
     *                     is not a multicast address.
     */
    public Speedwire(String hostAddress, String multicastGroup, int port) throws IOException {
        this.hostAddress = InetAddress.getByName(hostAddress);
        this.multicastGroup = InetAddress.getByName(multicastGroup);
        this.port = port;
        if (!this.multicastGroup.isMulticastAddress())
            throw new IOException("multicastGroup is not a multicast address");
    }

    /**
     * Construct a new thread for exchanging data with speedwire devices in your local network.<br>
     * Automatically determines your host address and uses the default port ({@code 9522}) and the
     * default multicast group ({@code 239.12.255.254}).
     *
     * @throws IOException if your host address couldn't be determined.
     *                     Then use {@link #Speedwire(String, String, int)} instead.
     */
    public Speedwire() throws IOException {
        this(getLocalAddress().getHostAddress(), DEFAULT_GROUP, DEFAULT_PORT);
    }

    /**
     * Try to determine your host address by opening a socket as {@link InetAddress#getLocalHost()} often returns the
     * wrong address if a device has multiple ethernet adapters.
     *
     * @return your host address
     * @throws IOException if the socket could not be opened and therefore the host address could not be determined
     */
    private static InetAddress getLocalAddress() throws IOException {
        if (System.getProperty("os.name").contains("mac")) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("google.com", 80));
                return socket.getLocalAddress();
            } catch (IOException e) {
                throw new IOException("Could not automatically determine your host address. Please specify it.", e);
            }
        } else {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                return socket.getLocalAddress();
            } catch (IOException e) {
                throw new IOException("Could not automatically determine your host address. Please specify it.", e);
            }
        }
    }

    /**
     * <p>
     * Register a callback that is run when a new telegram is received.<br>
     * Multiple callbacks may be registered.
     * Callbacks are allowed to be registered while the speedwire thread is running, although it is advised to register
     * them before calling {@code start()}.<br>
     * The callback will be run on the speedwire thread so avoid blocking or very slow operations.
     * </p><p>
     * <b>Note:</b><br>
     * Telegrams send from your host address will be redirected back by the multicast group and are therefore
     * filtered out. They will not trigger this callback.
     * </p>
     *
     * @param callback callback that listens for all incoming telegrams
     */
    public void onData(SpeedwireCallback callback) {
        callbacks.add(callback);
    }

    /**
     * Register a callback that is run whenever an error occurs while reading or parsing incoming data<br>
     * Multiple error handlers may be registered, registering new error handlers while the thread is running is possible.
     *
     * @param errorHandler errorHandler that listens for all occurring exceptions while receiving or parsing incoming data
     */
    public void onError(SpeedwireErrorHandler errorHandler) {
        errorHandlers.add(errorHandler);
    }

    /**
     * Register a callback that is run when no data is received for over 5 seconds.<br>
     * As most known speedwire devices send data every second this indicates a network error.
     *
     * @param timeoutHandler runnable that is run on timeout
     */
    public void onTimeout(Runnable timeoutHandler) {
        timeoutHandlers.add(timeoutHandler);
    }


    /**
     * Send any packet of bytes to the multicast group.<br>
     * Please make sure the send data does not violate the speedwire protocol as this isn't checked.
     *
     * @param packet an array of bytes that will be send to all devices that joined the multicast group.
     */
    public void send(byte[] packet) {
        try {
            DatagramPacket pkt = new DatagramPacket(packet, packet.length, multicastGroup, port);
            socket.send(pkt);
        } catch (IOException e) {
            errorHandlers.forEach(h -> h.onError(e));
        }
    }

    /**
     * <p>
     * Sends a discovery request to all speedwire devices in the multicast group.<br>
     * Every speedwire device will answer this request with a {@link DiscoveryResponse} for which you can listen
     * using {@code onData()}.<br>
     * This allows detecting all unknown devices and determining their ip addresses by using {@link Telegram#getOrigin()}.
     * </p><p>
     * More information on speedwire device discovery can be found in
     * <a href="https://www.sma.de/fileadmin/content/global/Partner/Documents/sma_developer/SpeedwireDD-TI-en-10.pdf">
     * SpeedwireDD-TI-en-10.pdf
     * </a>.
     * </p>
     */
    public void sendDiscoveryRequest() {
        byte[] discoveryRequest = new byte[]{
                (byte) 0x53, (byte) 0x4d, (byte) 0x41, (byte) 0x00,
                (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0xa0,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        send(discoveryRequest);
    }

    /**
     * Start the speedwire thread.<br>
     * This will cause your device to open a socket, join the multicast group and listen for incoming data.<br>
     * If for some reason, like network issues, the socket can't be opened the io exception will be logged.
     */
    @Override
    public synchronized void start() {
        try {
            socket = new MulticastSocket(port);
            socket.setInterface(getLocalAddress());
            socket.setReuseAddress(true);
            socket.joinGroup(multicastGroup);
            socket.setSoTimeout(TIMEOUT);
            super.start();
        } catch (IOException e) {
            System.err.println("Could not start Speedwire: " + e.getMessage());
            e.printStackTrace();
            if (socket != null) socket.close();
        }
    }

    /**
     * <a href="https://www.youtube.com/watch?v=otCpCn0l4Wo"><b><i>Don't touch this!</i></b></a>
     */
    @Override
    public void run() {
        if (socket == null) {
            throw new RuntimeException("Multicast socket isn't started");
        }

        while (!interrupted()) {
            try {
                //receive and decode incoming packets
                DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
                socket.receive(packet);
                final Telegram telegram = Telegram.from(packet);

                //Ignore own packets as multicast will also redirect them back to the sender
                if (telegram.getOrigin().equals(hostAddress)) continue;

                callbacks.forEach(h -> h.onDataReceived(telegram));
            } catch (SocketTimeoutException e) {
                timeoutHandlers.forEach(Runnable::run);
            } catch (Exception e) {
                errorHandlers.forEach(h -> h.onError(e));
            }
        }
        socket.close();
    }

    /**
     * Call this method to gracefully shut down the speedwire thread to stop listening for incoming data
     * and close the socket.<br>
     * This method does not await the termination of the thread.
     */
    public void shutdown() {
        interrupt();
    }

    /**
     * Returns the host address used for joining the multicast group (your local ip address)
     */
    public InetAddress getHostAddress() {
        return hostAddress;
    }

    /**
     * Returns the ip address of the multicast group (by default {@code 239.12.255.254})
     */
    public InetAddress getMulticastGroup() {
        return multicastGroup;
    }

    /**
     * Returns the udp port used for communication (by default {@code 9522})
     */
    public int getPort() {
        return port;
    }
}
