package com.lei.service;

import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class groupChat {
    private JPanel groupPanel;
    private JTextArea readMessage;
    private JTextField writeMessage;
    private JPanel friendsInGroup;


    private  String groupName;
    private Set<String> friends;//好友列表
    private String currentName;//当前客户端的名字
    private ServerConnection serverConnection;
    private JFrame frame;
    public groupChat(final String groupName, Set<String> friends, final String currentName, final ServerConnection serverConnection) {
       this.groupName=groupName;
       this.friends=friends;
       this.currentName=currentName;
       this.serverConnection=serverConnection;

         frame = new JFrame(groupName);
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//隐藏掉
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //加载群中的好友列表
        friendsInGroup.setLayout(new BoxLayout(friendsInGroup,BoxLayout.Y_AXIS));//设置为纵向布局
        Iterator<String> iterator=friends.iterator();
        while(iterator.hasNext()){

            String friend=iterator.next();
            JLabel jLabel=new JLabel(friend);
            friendsInGroup.add(jLabel);
        }

        writeMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb=new StringBuilder();
                sb.append(writeMessage.getText());
                //按下回车键
                if(e.getKeyCode()==KeyEvent.VK_ENTER){

                    String sendMessage=sb.toString();
                    /*
                    * type:4
                    * message:currentName-message
                    * toPerson:groupName
                    * */

                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.setType("4");
                    chatMessage.setMessage(currentName+"-"+sendMessage);
                    chatMessage.setToPerson(groupName);

                    try {
                        PrintStream out=new PrintStream(serverConnection.getOut(),true,"UTF-8");
                        out.println(ComonUtils.objectTOjson(chatMessage));
                        writeMessage.setText("");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });
    }

    public void readFromServer(String message){

        readMessage.append(message+"\n");
    }

    public JFrame getFrame(){

        return frame;
    }
}
