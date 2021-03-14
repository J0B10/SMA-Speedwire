package de.ungefroren.sma.speedwire;

import de.ungefroren.sma.speedwire.protocol.telegrams.Telegram;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArraySet;

public class Speedwire extends Thread {

    private static final String DEFAULT_GROUP = "239.12.255.254";
    private static final int DEFAULT_PORT = 9522;
    private static final int TIMEOUT = 5000;
    private static final byte[] DISCOVERY_REQUEST = DatatypeConverter.parseHexBinary("534d4100000402a0ffffffff0000002000000000");

    private final InetAddress hostAddress;
    private final InetAddress multicastGroup;
    private final int port;

    private final CopyOnWriteArraySet<SpeedwireCallback> callbacks = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<SpeedwireErrorHandler> errorHandlers = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Runnable> timeoutHandlers = new CopyOnWriteArraySet<>();

    private MulticastSocket socket;

    public Speedwire(String hostAddress, String multicastGroup, int port) throws IOException {
        this.hostAddress = InetAddress.getByName(hostAddress);
        this.multicastGroup = InetAddress.getByName(multicastGroup);
        this.port = port;
        if (!this.multicastGroup.isMulticastAddress())
            throw new IOException("multicastGroup is not a multicast address");
    }

    public Speedwire() throws IOException {
        this(getLocalAddress().getHostAddress(), DEFAULT_GROUP, DEFAULT_PORT);
    }

    private static InetAddress getLocalAddress() throws IOException {
        if (System.getProperty("os.name").contains("mac")) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("google.com", 80));
                return socket.getLocalAddress();
            }
        } else {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                return socket.getLocalAddress();
            }
        }
    }

    public void onData(SpeedwireCallback callback) {
        callbacks.add(callback);
    }

    public void onError(SpeedwireErrorHandler errorHandler) {
        errorHandlers.add(errorHandler);
    }

    public void onTimeout(Runnable timeoutHandler) {
        timeoutHandlers.add(timeoutHandler);
    }

    public void send(byte[] packet) {
        try {
            DatagramPacket pkt = new DatagramPacket(packet, packet.length, multicastGroup, port);
            socket.send(pkt);
        } catch (IOException e) {
            errorHandlers.forEach(h -> h.onError(e));
        }
    }

    public void sendDiscoveryRequest() {
        send(DISCOVERY_REQUEST);
    }

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
            System.err.println("Could not star Speedwire: " + e.getMessage());
            e.printStackTrace();
            if (socket != null) socket.close();
        }
    }

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

    public void shutdown() {
        interrupt();
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public InetAddress getMulticastGroup() {
        return multicastGroup;
    }

    public int getPort() {
        return port;
    }
}
