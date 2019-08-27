package com.lei.service;

import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class friendList {
    private JPanel friend;
    private JLabel friendOnline;
    private JScrollPane friendList;
    private JScrollPane group;
    private JButton createButton;
    private JFrame frame;//窗体

    private String username;//本人
    private Set<String> users;//所有的用户好友
    private ServerConnection serverConnection;

    /*存储所有的群名称以及相应成员*/
    private Map<String, Set<String>> groupMap = new ConcurrentHashMap<>();

    /*私聊好友名字，私聊界面*/
    private Map<String, privateChat> privateChatMap = new ConcurrentHashMap<>();/*存储所有的已经点开的私聊页面*/

    /*群名称，群聊界面*/
    private Map<String, groupChat> groupChatMap = new ConcurrentHashMap<>();/*存储所有群聊界面*/

    public friendList(final String username, final Set<String> users, final ServerConnection serverConnection) {
        this.username = username;
        this.users = users;
        this.serverConnection = serverConnection;

        /*好友列表界面*/
        frame = new JFrame(username);
        frame.setContentPane(friend);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 300);
        frame.setVisible(true);

        //加载所有的在线用户信息
        loadUsers();

        /*启动后台线程不断监听服务器发来的消息*/
        Thread thread = new Thread(new ListenTask());
        thread.setDaemon(true);
        thread.start();


        /*创建群组*/
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new CreateGroup(username, users, serverConnection, friendList.this);
            }
        });
    }

    /*加载所有的在线用户信息*/
    public void loadUsers() {
        JPanel friends = new JPanel();//面板
        JLabel[] jLabel = new JLabel[users.size()];//当前在线所有用户数量   //标签

        friends.setLayout(new BoxLayout(friends, BoxLayout.Y_AXIS));//设置面板格局
        /*set遍历*/
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String username = iterator.next();
            jLabel[i] = new JLabel(username);//将好友添加到标签数组中
            jLabel[i].addMouseListener(new PrivateAction(username));
            friends.add(jLabel[i]);//将标签添加到面板上
            i++;
        }


        friendList.setViewportView(friends);
        /*设置滚动条垂直滚动*/
        friendList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        /*刷新*/
        friends.revalidate();
        friendList.revalidate();

    }

    /*加载所有的群信息*/
    public void loadGroup() {

        /*存储所有群名称标签JPanel*/
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel, BoxLayout.Y_AXIS));

        JLabel[] labels = new JLabel[groupMap.size()];
        //遍历

        Set<Map.Entry<String, Set<String>>> entrySet = groupMap.entrySet();
        Iterator<Map.Entry<String, Set<String>>> iterator = entrySet.iterator();
        int i = 0;
        while (iterator.hasNext()) {

            Map.Entry<String, Set<String>> entry = iterator.next();
            labels[i] = new JLabel(entry.getKey());
            labels[i].addMouseListener(new GroupAction(entry.getKey()));//给每个群标签添加事件
            groupNamePanel.add(labels[i]);
            i++;
        }

        group.setViewportView(groupNamePanel);
        group.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        group.revalidate();
    }

    /*将群信息注册进来*/
    public void addGroup(String groupName, Set<String> friends) {

        groupMap.put(groupName, friends);

    }

    /*好友列表后台任务，不断监听服务器发来的信息*/
    /*好友上线信息、私聊、群聊*/
    private class ListenTask implements Runnable {


        private Scanner in = new Scanner(serverConnection.getIn());

        @Override
        public void run() {
            while (true) {

                /*收到服务器发来的信息*/
                if (in.hasNextLine()) {
                    String strFromSever = in.next();
                    /*判断服务器发来的是json字符串还是普通字符串*/

                    if (strFromSever.startsWith("{")) {
                        /*如果是json字符串，则解析为普通字符串*/
                        ChatMessage chatMessage = (ChatMessage) ComonUtils.isonTOobject(strFromSever, ChatMessage.class);

                        if (chatMessage.getType().equals("2")) {
                            /*服务器发来的私聊信息*/

                            String privateFriend = chatMessage.getMessage().split("-")[0];
                            String privateMessage = chatMessage.getMessage().split("-")[1];
                            //判断此私聊是否是第一次创建
                            if (privateChatMap.containsKey(privateFriend)) {
                                privateChat privateChat = privateChatMap.get(privateFriend);//获取私聊界面
                                privateChat.getFrame().setVisible(true);
                                privateChat.readFromServer(privateFriend + "说：" + privateMessage);

                            } else {

                                privateChat privateChat = new privateChat(privateFriend, username, serverConnection);
                                privateChatMap.put(privateFriend, privateChat);/*添加到map中*/
                                privateChat.readFromServer(privateFriend + "说：" + privateMessage);
                            }
                        } else if (chatMessage.getType().equals("4")) {


                            /*
                             * type:4
                             * message:currentName-message
                             * toPerson:groupName-[群中所有成员]
                             * */
                            /*收到服务器发来的群聊消息*/

                            String groupName = chatMessage.getToPerson().split("-")[0];

                            String sendName = chatMessage.getMessage().split("-")[0];//发送者
                            String groupMessage = chatMessage.getMessage().split("-")[1];//发送的信息
                            /*若此群聊在群聊列表*/
                            if (groupMap.containsKey(groupName)) {
                                /*判断是否已经打开群聊界面了*/
                                if (groupChatMap.containsKey(groupName)) {

                                    groupChat groupChat = groupChatMap.get(groupName);
                                    groupChat.getFrame().setVisible(true);  /*弹出群聊界面*/
                                    groupChat.readFromServer(sendName + "说：" + groupMessage);
                                } else {

                                    Set<String> groupFriends = groupMap.get(groupName);//获取该群的好友
                                    groupChat groupChat = new groupChat(groupName, groupFriends, username, serverConnection);
                                    groupChatMap.put(groupName, groupChat);
                                    groupChat.readFromServer(sendName + "说：" + groupMessage);
                                }

                            }else {

                                /*若群成员第一次收到群聊信息*/

                                //1.将群名称和群成员保存到当前客户端群聊列表 ??????????
                                Set<String> friends = (Set<String>) ComonUtils.isonTOobject(chatMessage.getToPerson().split("-")[1], Set.class);
                                groupMap.put(groupName, friends);//添加
                                loadGroup();
                                //2.弹出群聊界面
                                groupChat groupChat = new groupChat(groupName, friends, username, serverConnection);
                                groupChatMap.put(groupName, groupChat);//添加
                                groupChat.readFromServer(sendName + "说:" + groupMessage);
                            }
                        }
                    } else {

                        //newLogin:username
                        if (strFromSever.startsWith("newLogin:")) {
                            String newFriend = strFromSever.split(":")[1];

                            /*在用户好友中添加该用户*/
                            users.add(newFriend);
                            /*弹框提示用户上线*/
                            JOptionPane.showMessageDialog(frame, newFriend + "上线了", "上线提醒", JOptionPane.INFORMATION_MESSAGE);
                            //刷新列表好友
                            loadUsers();
                        }
                    }
                }
            }
        }
    }


    /*私聊*/
    /*与好友私聊的面板是否已经打开*/
    //鼠标操作
    private class PrivateAction implements MouseListener {

        private String privateName;//私聊的用户

        public PrivateAction(String privateName) {
            this.privateName = privateName;
        }

        /*鼠标点击执行的事件*/
        @Override
        public void mouseClicked(MouseEvent e) {
            /*判断是否已经点击开了私聊界面*/

            if (privateChatMap.containsKey(privateName)) {

                privateChat privateChat = privateChatMap.get(privateName);
                privateChat.getFrame().setVisible(true);

            } else {

                /*否则是第一次点击，创建私聊界面*/
                privateChat privateChat = new privateChat(privateName, username, serverConnection);
                /*并添加到缓存中*/
                privateChatMap.put(privateName, privateChat);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {


        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /*群聊点击事件*/
    private class GroupAction implements MouseListener {

        /*群名称*/
        private String groupName;

        public GroupAction(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (groupChatMap.containsKey(groupName)) {

                groupChat groupChat = groupChatMap.get(groupName);
                groupChat.getFrame().setVisible(true);//显示该群聊窗体

            } else {
                Set<String> groupFriends = groupMap.get(groupName);//获取群中好友
                groupChat groupChat = new groupChat(groupName, groupFriends, username, serverConnection);
                groupChatMap.put(groupName, groupChat);
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}
