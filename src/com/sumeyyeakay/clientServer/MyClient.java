package com.sumeyyeakay.clientServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *      internetten gelen istekleri dinlemesi gerek bunun icin port lazim.
 *      2byte dan olusur.
 *
 * 127.0.0.1
 *
 *    //veri gondermek icin
 *         socket.getOutputStream();
 */
public class MyClient
{
    //IP number
    private static final String HOST = "localhost";

    private static final int PORT = 2020;

    public void sendMessage(String message){

        //try-with-resources
        try
        (
                Socket socket = new Socket(HOST, PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dos.writeUTF(message);
            dos.flush();
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }


    }
}
