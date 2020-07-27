package com.sumeyyeakay.clientServer;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyServer implements Closeable
{
    //paketi kapatma sintali terminal_signal "Serveri kapatmak icin bir sinyal"

    private static final String TERMINATE_SIGNAL = String.valueOf('\0');

    private final ExecutorService executorService = Executors.newCachedThreadPool();


    //veriyi alan taraf olmasini sagliyor
    private final ServerSocket serverSocket;

    private static final int PORT = 2020;

    private final Object runningLock = new Object();
    private boolean running = false;
    //baglanti sayisi icin gelen

    //yeni olay olusturup firlatmak icin
    private PacketReceivedEventHandler onPacketReceived= null;
    private int count = 1;


    public MyServer ()
    {
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    private void listenForNewConnection(){
        while(isRunning())
        {
            //TODO istemciden yeni paket al
        }
    }
    private void startNewThread(){
        Thread thread = new Thread(this::listenForNewConnection);
        thread.setName("my-server");
        thread.setDaemon(true);
        thread.start();
    }
    public void setOnPacketReceived(PacketReceivedEventHandler onPacketReceived)
    {
        this.onPacketReceived = onPacketReceived;
    }

    private String reverse(String text)
    {
        return new StringBuilder(text)
                .reverse()
                .toString();
    }

    private void handleReceivedSocket(Socket socket){

        final String ipAddress = socket
                .getInetAddress()
                .toString();

        //alinan degeri tersine cevirip metin yazicaz
        try (DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            final String data = dis.readUTF();

            if (!TERMINATE_SIGNAL.equals(data))
            {
                final String reversed = reverse(data);

                if (onPacketReceived != null)
                {
                    onPacketReceived.received(ipAddress, data, reversed);
                }
            }

        }catch (IOException ex){

        }
    }
    private void listenForNewConnections()
    {
        while (isRunning())
        {
            receive();
        }
    }
    private void startInNewThread()
    {
        Thread thread = new Thread(this::listenForNewConnections);
        thread.setName("my-server");
        thread.setDaemon(true);
        thread.start();
    }

    private void receive(){
        //yeni paketler
        try {


            /**
             * Thread icin 2 ayri yapi kullanabiliriz.
             * Ilki ->
             *             // ayyni anda birden fazla istek gelebilir bu yuzden thread yaziyoruz
             *             Thread thread = new Thread(() -> handleReceivedSocket(receivedSocket));
             *             thread.setName("My-server-received"  + count++);
             *             thread.setDaemon(true);
             *             thread.start();
             *
             * Bu yapi genel olarak buyuk projelerde kullanildigi zaman threade yuk biniyor ve programin daha yavas calismasina neden
             * olabilir.
             *      Programda cok fazla thread kullanmak istiyorum ama context switche daha fazla yuklenmemek icin THREAD POOL
             *      (thread havuzu) olusturmam gerekir.
             * Mesela bu yapi: Basta 100 thread olusturuyorum ve bekliyor. Sunucudan bir islem geldiginde uygun olan threade gidiyor.
             * Her istegi kullaniyoruz. Boylelikle sistemimizin performansi daha saglikli oluyor.
             * Bu yapinin kullanildigi yere ornek vericek olursak -> Spring boot da database baglandigimiz zaman kullanilir.
             *
             * Bunun icinde EXECUTER SERVICE kullaniriz.
             *       executorService: Yukardaki kullandigimiz 4 satirlik kodu tek satir ile yapmamizi saglar. Istek geldiginde thread
             * acilir.
             */

            //1. YOL
            // ayyni anda birden fazla istek gelebilir bu yuzden thread yaziyoruz
            //Thread thread = new Thread(() -> handleReceivedSocket(receivedSocket));
            // thread.setName("My-server-received"  + count++);
            // thread.setDaemon(true);
            // thread.start();

            //2.YOL
            //yeni baglanti geldiginde calisacak o zamana akadar bekletiyo
            Socket receivedSocket = serverSocket.accept();
            executorService.submit(() -> handleReceivedSocket(receivedSocket));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    public void start(){
        if (isRunning())
        {
            return;
        }

        setRunning(true);
        startInNewThread();
    }

    private void sendTerminateSignal()
    {
        new MyClient().sendMessage(TERMINATE_SIGNAL);
    }

    public void stop(){
        if (!isRunning())
        {
            return;
        }

        setRunning(false);
        sendTerminateSignal();
    }

    @Override
    public void close() throws IOException {
        //
        /**
         * executorService -> kullandigin zaman mutlaka kapatmak zorundasin.
         * shutdownNow() -> havuzu kapatiyor ve calismayan threadleri  bize liste olarak donuyor.
         */
        executorService.shutdownNow();
        serverSocket.close();
    }

    public PacketReceivedEventHandler getOnPacketReceived()
    {
        return onPacketReceived;
    }

    public boolean isRunning() {
        synchronized (runningLock){
            return running;
        }
    }

    private void setRunning(boolean running) {
        synchronized (runningLock)
        {
            this.running = running;
    }

    }

}
