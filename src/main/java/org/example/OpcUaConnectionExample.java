package org.example;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OpcUaConnectionExample {

    private static JLabel lamp1Label;
    private static JLabel lamp2Label;
    private static JLabel lamp3Label;
    private static JLabel lamp4Label;
    private static JLabel lamp5Label;

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        //Start de GUI
        initGUI();

        // Definieer de connectie-URL voor de OPC UA-server
        String url = "opcua:tcp://192.168.0.1:4840";
        PlcConnection connection;

        // Maak gebruik van een try-with-resources statement om automatisch te zorgen voor het sluiten van de verbinding
        try {
            connection = PlcDriverManager.getDefault().getConnectionManager().getConnection(url);
        } catch (
                PlcConnectionException e) {
            throw new RuntimeException(e);
        }

        PlcReadRequest.Builder builder = connection.readRequestBuilder();
        builder.addTagAddress("lamp1", "ns=4;i=2");
        builder.addTagAddress("lamp2", "ns=4;i=3");
        builder.addTagAddress("lamp3", "ns=4;i=4");
        builder.addTagAddress("lamp4", "ns=4;i=5");
        builder.addTagAddress("lamp5", "ns=4;i=6");
        PlcReadRequest request = builder.build();

        while (connection.isConnected()) {
            try {
                PlcReadResponse response = request.execute().get(5000, TimeUnit.MILLISECONDS);
                boolean lamp1Value = response.getBoolean("lamp1");
                boolean lamp2Value = response.getBoolean("lamp2");
                boolean lamp3Value = response.getBoolean("lamp3");
                boolean lamp4Value = response.getBoolean("lamp4");
                boolean lamp5Value = response.getBoolean("lamp5");

                //update GUI met nieuwe waarden
                updateGUI(lamp1Value, lamp2Value, lamp3Value, lamp4Value, lamp5Value);

                Thread.sleep(100);// Update elke seconde
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initGUI() {
        JFrame frame = new JFrame("PLC Status");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        lamp1Label = new JLabel("Lamp 1: Waiting...");
        lamp2Label = new JLabel("Lamp 2: Waiting...");
        lamp3Label = new JLabel("Lamp 3: Waiting...");
        lamp4Label = new JLabel("Lamp 4: Waiting...");
        lamp5Label = new JLabel("Lamp 5: Waiting...");

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(lamp1Label);
        frame.add(lamp2Label);
        frame.add(lamp3Label);
        frame.add(lamp4Label);
        frame.add(lamp5Label);

        frame.setVisible(true);
    }

    private static void updateGUI(boolean lampValue1, boolean lampValue2, boolean lampValue3, boolean lampValue4, boolean lampValue5) {
        SwingUtilities.invokeLater(() -> {
            lamp1Label.setText("Lamp 1: " + (lampValue1 ? "ON" : "OFF"));
            lamp2Label.setText("Lamp 2: " + (lampValue2 ? "ON" : "OFF"));
            lamp3Label.setText("Lamp 3: " + (lampValue3 ? "ON" : "OFF"));
            lamp4Label.setText("Lamp 4: " + (lampValue4 ? "ON" : "OFF"));
            lamp5Label.setText("Lamp 5: " + (lampValue5 ? "ON" : "OFF"));
        });
    }
}