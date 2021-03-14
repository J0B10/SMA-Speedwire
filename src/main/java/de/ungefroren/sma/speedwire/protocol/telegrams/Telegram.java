package de.ungefroren.sma.speedwire.protocol.telegrams;

import de.ungefroren.sma.speedwire.protocol.InvalidTelegramException;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Telegram {

    private final InetAddress origin;
    private final byte[] data;

    Telegram(InetAddress origin, byte[] data) throws InvalidTelegramException {
        this.origin = origin;
        this.data = data;
        validate();
    }

    protected void validate() throws InvalidTelegramException {
        try {
            //check identification string
            if (!new String(getUnsigned(0, 3), 0, 3).equals("SMA") || getUnsigned(3) != 0)
                throw new InvalidTelegramException("telegram doesn't start with id String \"S\", \"M\", \"A\", 0");

            //check end
            if (get2ByteUnsignedInt(length() - 4) != 0 || get2ByteUnsignedInt(length() - 2) != 0)
                throw new InvalidTelegramException("telegram seems to be incomplete (must end with 0x00, 0x00, 0x00, 0x00)");

        /*TODO
           Check the integrity of the given fields:

           All telegrams seem to consist of multiple fields.
           Each field starts with a 2 Byte unsigned int that specifies the length of the payload followed by a 2 Byte tag.
           The tags and their functions remain unclear, known are "SMA Net 2" (0x0010) which indicates a data packet and
           "end" (0x0000) which indicates the end of the telegram.
           The tag is followed by the payload with the given length.

           More incomplete information can be found under:
           https://www.sma.de/fileadmin/content/global/Partner/Documents/SMA_Labs/EMETER-Protokoll-TI-en-10.pdf
         */
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidTelegramException(e);
        }
    }

    public static Telegram from(DatagramPacket packet) throws InvalidTelegramException {
        byte[] data = Arrays.copyOfRange(
                packet.getData(),
                packet.getOffset(),
                packet.getOffset() + packet.getLength()
        );
        //TODO Improve handling of invalid telegram (distinguish between wrong type and defective packets)
        try {
            return new DiscoveryResponse(packet.getAddress(), data);
        } catch (InvalidTelegramException ignored) {
        }
        try {
            return new EnergyMeterTelegram(packet.getAddress(), data);
        } catch (InvalidTelegramException ignored) {
        }
        return new Telegram(packet.getAddress(), data);
    }

    public InetAddress getOrigin() {
        return origin;
    }

    public int length() {
        return data.length;
    }

    public byte getByte(int index) {
        return data[index];
    }

    public int getUnsigned(int index) {
        return Byte.toUnsignedInt(getByte(index));
    }

    public byte[] getBytes(int index, int length) {
        return Arrays.copyOfRange(data, index, index + length);
    }

    public int[] getUnsigned(int index, int length) {
        return IntStream.range(index, index + length).map(this::getUnsigned).toArray();
    }

    public int get2ByteUnsignedInt(int index) {
        return ((getByte(index) << 8) & 0x0000ff00) | (getByte(index + 1) & 0x000000ff);
    }

    public BigInteger get4ByteUnsignedInt(int index) {
        return new BigInteger(1, getBytes(index, 4));
    }

    public BigInteger get8ByteUnsignedInt(int index) {
        return new BigInteger(1, getBytes(index, 8));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            switch (i % 16) {
                case 0:
                    if (i != 0) builder.append(System.lineSeparator());
                    break;
                case 8:
                    builder.append("  ");
                    break;
                default:
                    builder.append(' ');
            }
            builder.append(String.format("%02x", getUnsigned(i)));
        }
        return builder.append(System.lineSeparator()).toString();
    }
}
