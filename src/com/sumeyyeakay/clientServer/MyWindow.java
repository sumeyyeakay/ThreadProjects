package com.sumeyyeakay.clientServer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class MyWindow extends Application {
    protected final TextArea txtInfo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    protected synchronized void appendInfoLine(String line){
        final String text = String.format("%s - %s\n", dateFormat.format(new Date()), line);
        Platform.runLater(() -> txtInfo.appendText(text));

    }
    protected MyWindow() {
        txtInfo = new TextArea();
        txtInfo.setFont(new Font(16d));
        txtInfo.setEditable(false);
        txtInfo.setWrapText(false);

    }
}
