package com.sumeyyeakay.clientServer;

public interface PacketReceivedEventHandler {
    //dinlemek icin

    void received(String ipAddress, String original, String reversed);
}
