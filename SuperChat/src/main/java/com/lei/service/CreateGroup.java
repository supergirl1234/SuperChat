package com.lei.service;

import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CreateGroup {
    private JPanel createGroupPanel;
    private JPanel friendPanel;
    private JTextField groupname;
    private JButton Button;

    private String currentName;
    private Set<String> friends;
    private ServerConnection serverConnection;
    private friendList friendList;
    private  final JFrame frame;

    public CreateGroup(final String currentName, Set<String> friends, final ServerConnection serverConnection, final friendList friendList) {
        this.currentName = currentName;
        this.friends = friends;
        this.serverConnection = serverConnection;
        this.friendList=friendList;

         frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);//居中显示
        frame.setVisible(true);


        friendPanel.setLayout(new BoxLayout(friendPanel,BoxLayout.Y_AXIS));//纵向展示
        /*将在线好友以checkBox展示到界面中*/
        JCheckBox[] jCheckBoxes=new JCheckBox[friends.size()];
        Iterator<String> iterator=friends.iterator();
        int i=0;
        while(iterator.hasNext()){
            String friend=iterator.next();
            jCheckBoxes[i]=new JCheckBox(friend);
            friendPanel.add(jCheckBoxes[i]);
            i++;
        }





        /*点击提交，将信息发送到服务器端*/
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               /*1.判断哪些好友选中加入群聊
               * 2.获取群名：获取输入框中输入的群名称
               * 3.将群名和选中的好友发送到服务端
               * type:3
               * message:group
               * toPerson:{user1,user2....}
               * */

               Set<String> selectFriends=new HashSet<>();//存放所有被选中的好友
               Component[] components=  friendPanel.getComponents();//获取friendPanel的所有组件
                for(Component item:components){

                      JCheckBox checkBox= (JCheckBox) item;
                      if(checkBox.isSelected()){
                          String selectFriend=checkBox.getText();
                          selectFriends.add(selectFriend);
                      }

                }
                selectFriends.add(currentName);/*将自己也添加进去*/

                /*获取群名*/
                String groupName=groupname.getText();

                /*将上面的信息发送给服务器*/

                ChatMessage chatMessage=new ChatMessage();
                chatMessage.setType("3");
                chatMessage.setMessage(groupName);
                chatMessage.setToPerson(ComonUtils.objectTOjson(selectFriends));


                try {
                    PrintStream out=new PrintStream(serverConnection.getOut(),true,"UTF-8");
                    out.println(ComonUtils.objectTOjson(chatMessage));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                /*将当前创建群聊的界面隐去*/
                frame.setVisible(false);
                /*刷新好友列表界面的群列表*/

            friendList.addGroup(groupName,selectFriends);//添加
            friendList.loadGroup();//刷新
            }
        });
    }


}
