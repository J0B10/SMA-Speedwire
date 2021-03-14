package de.ungefroren.sma.speedwire;

import de.ungefroren.sma.speedwire.protocol.telegrams.Telegram;

public interface SpeedwireCallback {
    void onDataReceived(Telegram data);
}
