package com.samuelklit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private static JFrame myFrame;

    private ArrayList<String> subscribedTopics;
    private MqttClient client;

    public static void main(String[] args) {
        myFrame = new JFrame("MQTT Transceiver");
        myFrame.setContentPane(new Application().panel);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setSize(400,350);

    }

    void Connect(String clientId, String username, String password){
        try{
            //Unsubscribe from all topics from old broker.
            if(subscribedTopics != null){
                if(subscribedTopics.size() > 0){
                    client.unsubscribe((subscribedTopics.toArray(new String[subscribedTopics.size()])));
                    subscribedTopics = new ArrayList<String>();
                }
            }

            //Clear received box.
            topicReceivedTXT.setText("");

            //Connect to broker.
            MqttConnectOptions connOpts = new MqttConnectOptions();
            MemoryPersistence persistence = new MemoryPersistence();
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
            status("Connected");
            JOptionPane.showMessageDialog(null, "Connected");
        }
        catch(Exception e) {
            if(e instanceof MqttException){
                printMQTTError((MqttException)e);
            }else{
                System.out.println(e.getMessage());
            }

            JOptionPane.showMessageDialog(null, "Can't connect to server.");
        }
    }

    private void printMQTTError(MqttException me){
        status("Error!");
        System.out.println("reason "+me.getReasonCode());
        System.out.println("msg "+me.getMessage());
        System.out.println("loc "+me.getLocalizedMessage());
        System.out.println("cause "+me.getCause());
        System.out.println("exception "+me);
        me.printStackTrace();
    }

    private Application() {
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
                //Check if action is valid.
                if(client == null || !client.isConnected()){
                    JOptionPane.showMessageDialog(null, "Please connect to a broker first.");
                    return;
                }else if(topicTXT.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Topic cannot be empty.");
                    return;
                }

                try{
                    client.subscribe(topicTXT.getText());

                    if(subscribedTopics == null){
                        subscribedTopics = new ArrayList<String>();
                    }
                    subscribedTopics.add(topicTXT.getText());

                    JOptionPane.showMessageDialog(null, "Subscribed");
                    status("Subscribed");
                }catch(MqttException me) {
                    status("Error");
                    JOptionPane.showMessageDialog(null, "Couldn't subscribe to topic.");
                    printMQTTError(me);
                }
            }
        });

        publishBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check if action is valid.
                if(client == null || !client.isConnected()){
                    JOptionPane.showMessageDialog(null, "Please connect to a broker first.");
                    return;
                }else if(topicTXT.getText() == ""){
                    JOptionPane.showMessageDialog(null, "Please subscribe to a topic first.");
                    return;
                }

                //Publish on topic.
                try{
                    MqttMessage message = new MqttMessage(publishTXT.getText().getBytes());
                    message.setQos(2);
                    client.publish(topicTXT.getText(), message);
                    status("Published message.");
                }catch(MqttException me) {
                    printMQTTError(me);
                }
            }
        });
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        topicReceivedTXT.setText(s + ": "+  mqttMessage.toString() + "\n" + topicReceivedTXT.getText());
        status("Message received.");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        status("Delivery complete.");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        status("Connection lost.");
    }

    private void status(String message){
        myFrame.setTitle("MQTTGUI. Status: " + message);
    }
}
