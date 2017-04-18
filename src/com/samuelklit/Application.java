package com.samuelklit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Application {

    private JPanel panel;
    private JButton LoadBTN;
    private JTextArea zzzzzTextArea;
    private JTextField publishTXT;
    private JButton publishBTN;
    private JTextField topicTXT;
    private JTextArea topicReceivedTXT;
    private JTextField brokerIPTXT;
    private JButton connectionBTN;


    private MqttClient client;
    private MqttConnectOptions connOpts;
    private MemoryPersistence persistence;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MQTT Transceiver");
        frame.setContentPane(new Application().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600,400);
    }

    public void Connect(String clientId, String username, String password){
        try{
            System.out.println("Starting connection");
            connOpts = new MqttConnectOptions();
            persistence = new MemoryPersistence();
            client = new MqttClient("tcp://" + brokerIPTXT.getText(), clientId, persistence);
            System.out.println("Setting clean session.");
            connOpts.setCleanSession(true);

            System.out.println("Username:" + username + ": Password:" + password+":");
            if(username != null && !username.trim().isEmpty()){
                System.out.println("Connecting using credentials.");
                connOpts.setUserName(username);
                connOpts.setPassword(password.toCharArray());
            }else{
                System.out.println("Connecting without credentials.");
                connOpts.setUserName("usernameCannotBeEmpty");
                connOpts.setPassword("passwordCannotBeEmpty".toCharArray());
            }

            client.connect(connOpts);
            System.out.println("Connected");
            //MqttMessage message = new MqttMessage(publishTXT.getText().getBytes());
            //message.setQos(2);
            //client.publish(topic, message);
            //client.disconnect();

        }catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    public Application() {

        connectionBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrokerSettings dialog = new BrokerSettings(Application.this);
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.pack();
                dialog.setSize(250,160);
                dialog.show();
            }
        });

        LoadBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        publishBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }
}
