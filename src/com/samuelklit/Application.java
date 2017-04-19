package com.samuelklit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NoRouteToHostException;
import java.net.SocketException;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Application implements MqttCallback {

    private JPanel panel;
    private JButton subscribeBTN;
    private JTextField publishTXT;
    private JButton publishBTN;
    private JTextField topicTXT;
    private JTextField brokerIPTXT;
    private JButton connectionBTN;
    private JTextArea topicReceivedTXT;

    private MqttClient client;
    private MqttConnectOptions connOpts;
    private MemoryPersistence persistence;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MQTT Transceiver");
        frame.setContentPane(new Application().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,350);
    }

    public void Connect(String clientId, String username, String password){
        try{
            System.out.println("Starting connection");
            connOpts = new MqttConnectOptions();
            persistence = new MemoryPersistence();
            client = new MqttClient("tcp://" + brokerIPTXT.getText(), clientId, persistence);

            connOpts.setCleanSession(true);
            client.setCallback(this);

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
            JOptionPane.showMessageDialog(null, "Connected");
        }
        catch(Exception e) {
            printMQTTError((MqttException)e);
            JOptionPane.showMessageDialog(null, "Can't connect to server.");
        }
    }

    public void printMQTTError(MqttException me){
        System.out.println("reason "+me.getReasonCode());
        System.out.println("msg "+me.getMessage());
        System.out.println("loc "+me.getLocalizedMessage());
        System.out.println("cause "+me.getCause());
        System.out.println("excep "+me);
        me.printStackTrace();
    }

    public Application() {

        connectionBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(brokerIPTXT.getText() == null | brokerIPTXT.getText().trim().isEmpty() ){
                    JOptionPane.showMessageDialog(null, "Please enter broker IP");
                    return;
                }

                BrokerSettings dialog = new BrokerSettings(Application.this);
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.pack();
                dialog.setSize(250,160);
                dialog.show();
            }
        });

        subscribeBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client == null || !client.isConnected()){
                    JOptionPane.showMessageDialog(null, "Please connect to a broker first.");
                    return;
                }

                try{
                    client.subscribe(topicTXT.getText());
                    System.out.println("Subscribed!");
                    JOptionPane.showMessageDialog(null, "Subscribed");
                    topicReceivedTXT.setText("");
                }catch(MqttException me) {
                    printMQTTError(me);
                }
            }
        });

        publishBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client == null || !client.isConnected()){
                    JOptionPane.showMessageDialog(null, "Please connect to a broker first.");
                    return;
                }

                try{
                    MqttMessage message = new MqttMessage(publishTXT.getText().getBytes());
                    message.setQos(2);
                    client.publish(topicTXT.getText(), message);
                }catch(MqttException me) {
                    printMQTTError(me);
                }
            }
        });
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        topicReceivedTXT.setText(mqttMessage.toString() + "\n" + topicReceivedTXT.getText());
        System.out.println("Received: " + mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void connectionLost(Throwable throwable) {

    }
}
