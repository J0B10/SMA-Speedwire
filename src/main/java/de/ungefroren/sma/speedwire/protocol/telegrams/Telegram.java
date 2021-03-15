package de.ungefroren.sma.speedwire.protocol.telegrams;

import de.ungefroren.sma.speedwire.protocol.InvalidTelegramException;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Data received via speedwire is called telegram.<br>
 * On construction of a telegram the data is validated and parsed.<br>
 * The class provides additional methods for
 */
public class Telegram {

    private final InetAddress origin;
    private final byte[] data;

    Telegram(InetAddress origin, byte[] data) throws InvalidTelegramException {
        this.origin = origin;
        this.data = data;
        validate();
    }

    /**
     * <p>
     * Parses the given datagram packet into a telegram<br>
     * The returned telegram will be of a specific subclass if the data from the packet matches the subclasses
     * requirements.
     * </p><br><p>
     * Currently implemented subclasses:<br>
     * {@link DiscoveryResponse}, {@link EnergyMeterTelegram}
     * </p>
     *
     * @param packet packet that should be parsed as speedwire telegram
     * @return parsed telegram
     * @throws InvalidTelegramException if the packet is not a valid telegram
     */
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

    /**
     * Validates the integrity of the telegram and throws an exception otherwise
     *
     * @throws InvalidTelegramException if the telegram data does violate the required data format
     */
    protected void validate() throws InvalidTelegramException {
        try {
            //check identification string
            if (!new String(getUnsigned(0, 3), 0, 3).equals("SMA") || getUnsigned(3) != 0)
                throw new InvalidTelegramException("telegram doesn't start with id String \"S\", \"M\", \"A\", 0");

            //check end
            if (get2ByteUnsignedInt(length() - 4) != 0 || get2ByteUnsignedInt(length() - 2) != 0)
                throw new InvalidTelegramException("telegram seems to be incomplete (must end with 0x00, 0x00, 0x00, 0x00)");

        /* TODO
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

    /**
     * Returns the address of the device that sent this telegram
     */
    public InetAddress getOrigin() {
        return origin;
    }

    /**
     * Returns the total length of this telegram in bytes
     */
    public int length() {
        return data.length;
    }

    /**
     * <p>
     * Returns the raw value from the specified position of the telegrams data
     * </p><br><p>
     * <b>Telegrams contain unsigned bytes while java byte is signed.<br>
     * It's in most cases better to use {@link #getUnsigned(int)}!</b>
     * </p>
     *
     * @param index index of the byte to return
     * @return the value at the given position as signed byte
     */
    public byte getByte(int index) {
        return data[index];
    }

    /**
     * Returns the value from the specified position of the telegrams data as unsigned byte<br>
     *
     * @param index index of the byte to return
     * @return the value at the given position, converted to an unsigned integer
     */
    public int getUnsigned(int index) {
        return Byte.toUnsignedInt(getByte(index));
    }

    /**
     * <p>
     * Returns an array of multiple bytes from the telegrams data
     * </p><br><p>
     * <b>Telegrams contain unsigned bytes while java byte is signed.<br>
     * It's in most cases better to use {@link #getUnsigned(int, int)}!</b>
     * </p>
     *
     * @param index  index of the first byte to return
     * @param length size of the array to return
     * @return an array containing a copy of the telegram data with the given range as signed bytes
     */
    public byte[] getBytes(int index, int length) {
        return Arrays.copyOfRange(data, index, index + length);
    }

    /**
     * Returns an array of multiple unsigned bytes from the telegrams data
     *
     * @param index  index of the first byte to return
     * @param length size of the array to return
     * @return an array containing a copy of the telegram data with the given range converted to unsigned integers
     */
    public int[] getUnsigned(int index, int length) {
        return IntStream.range(index, index + length).map(this::getUnsigned).toArray();
    }

    /**
     * Returns a two byte sized, unsigned integer from a specified position of the telegrams data
     *
     * @param index starting index of the integer to return
     * @return the unsigned 2 byte integer at the given index
     */
    public int get2ByteUnsignedInt(int index) {
        return ((getByte(index) << 8) & 0x0000ff00) | (getByte(index + 1) & 0x000000ff);
    }

    /**
     * Returns a four byte sized, unsigned integer from a specified position of the telegrams data
     *
     * @param index starting index of the integer to return
     * @return the unsigned 4 byte integer at the given index
     */
    public BigInteger get4ByteUnsignedInt(int index) {
        return new BigInteger(1, getBytes(index, 4));
    }

    /**
     * Returns a eight byte sized, unsigned integer from a specified position of the telegrams data
     *
     * @param index starting index of the integer to return
     * @return the unsigned 8 byte integer at the given index
     */
    public BigInteger get8ByteUnsignedInt(int index) {
        return new BigInteger(1, getBytes(index, 8));
    }

    /**
     * Returns the telegrams raw data converted to a string of hexadecimal values for debugging purposes
     */
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
