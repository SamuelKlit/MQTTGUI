package com.samuelklit;

import javax.swing.*;
import java.awt.event.*;
import java.util.Random;

public class BrokerSettings extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField usernameTXT;
    private JTextField passwordTXT;
    private JTextField clientIDTXT;
    private JButton randomizeBTN;
    private Application app;
    private Random rnd;


    public BrokerSettings(Application app) {
        rnd = new Random();
        this.app = app;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        randomizeBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientIDTXT.setText("MQTTGUI-" + rnd.nextInt(100000));
            }
        });
    }

    private void onOK() {
        app.Connect(clientIDTXT.getText(), usernameTXT.getText(),passwordTXT.getText());
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
