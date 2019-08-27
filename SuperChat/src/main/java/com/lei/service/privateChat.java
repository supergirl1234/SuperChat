package com.lei.service;

import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class privateChat {
    private JPanel privatePanel;
    private JTextArea read;
    private JTextField write;


    private String chatFriendName;
    private String myName;
    private ServerConnection serverConnection;
    private PrintStream out;

    private JFrame frame;

    public privateChat(final String chatFriendName, final String myName, ServerConnection serverConnection) {
        //私聊界面
        this.chatFriendName = chatFriendName;//私聊好友
        this.myName = myName;//当前用户
        this.serverConnection = serverConnection;

        try {
            this.out = new PrintStream(serverConnection.getOut(), true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        frame = new JFrame("与" + chatFriendName + "私聊中");
        frame.setContentPane(privatePanel);
        /*设置窗口的关闭操作为隐藏*/
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);

        /*捕捉输入框的键盘输入*/
        write.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                /*点击回车才发送消息*/
                StringBuilder sb = new StringBuilder();
                sb.append(write.getText());
                /*当捕捉到按下enter,将当前信息发送到服务端，并且将自己发送的信息展示到当前页面*/
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    String message = sb.toString();//发送的消息
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setType("2");//私聊
                    chatMessage.setMessage(myName + "-" + message);
                    chatMessage.setToPerson(chatFriendName);

                    /*将信息发送给服务器端*/
                    privateChat.this.out.println(ComonUtils.objectTOjson(chatMessage));
                    //将自己发送的信息展示到自己当前私聊界面
                    readFromServer(myName + "说" + message);
                    /*将输入框还原*/
                    write.setText("");
                }
            }
        });
    }

    public void readFromServer(String message) {
        read.append(message + "\n");

    }

    public Component getFrame() {
        return frame;
    }
}
