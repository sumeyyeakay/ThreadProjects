package com.sumeyyeakay.clientServer;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.stage.WindowEvent;

public class MyServerWindow extends MyWindow implements EventHandler<WindowEvent>, PacketReceivedEventHandler
{
    private MyServer server;

    public static void main(String[] args) {
        /**
         * Fx Thread
         */

        ///uygulmayi calistirmamiz icin launch yaziyoruz.


        launch(args);
    }
    private void startServer()
    {
        server = new MyServer();
        server.setOnPacketReceived(this);
        server.start();

        appendInfoLine("Sunucu çalışıyor...");
    }

    @Override
    public void received(String ipAddress, String original, String reversed)
    {
        appendInfoLine(String.format("%s adresinden veri alındı: \"%s\" Tersine çevrildi: \"%s\"", ipAddress, original, reversed));
    }

    @Override
    public void handle(WindowEvent event)
    {
        if (server != null)
        {
            appendInfoLine("Sunucu kapatılıyor...");

            try
            {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                server.stop();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new javafx.scene.control.Label("SERVER");
        label.setFont(new Font(22d));


        //1den fazla nesneyi controlu ayni pencerede gostermek icin (Node verebiliriz)
        //Border Pane ozelligi pencereyi 5 parcaya ayiriyor. istenilen kontrolu bu alanlara yerlestirmeye yarar
        BorderPane root = new BorderPane();
        root.setTop(label);
        root.setCenter(txtInfo);

        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setMargin(label,new Insets(10d,0d,10d,0d));

        Scene scene = new Scene(root,750d,500d);


        primaryStage.setScene(scene);
        //yeniden boyutlandirilmasin demek
        primaryStage.setResizable(false);
        primaryStage.setTitle("SERVER");
        primaryStage.setOnCloseRequest(this);
        primaryStage.show();

        //Serveri baslat
        startServer();
    }
}
