package com.lei.service;

import com.lei.po.User;
import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

import static com.lei.dao.RealDAO.login;

public class start {
    private JLabel picture;
    private JPanel back;
    private JTextField Name;
    private JPasswordField Pwd;
    private JLabel username;
    private JLabel password;
    private JButton register;
    private JButton login;
    private JPanel start;
    private  JFrame frame;



    public start() {

        /*用户登录注册总界面*/
        frame= new JFrame("start");
        frame.setContentPane(start);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 300);
        frame.setVisible(true);



        /*监听注册按钮*/
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /*一点击注册就弹出完整的注册界面*/
                new registerPanel();
            }
        });

        /*监听登录按钮*/
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //填写登陆信息
                String name = Name.getText();
                String pwd = new String(Pwd.getPassword());

                //用户登录
                User user=login(name,pwd);

                if(user!=null){
                    //登录成功
                    JOptionPane.showMessageDialog(frame,"登录成功");
                    //成功之后
                    frame.setVisible(false);//让登录页面不可见

                    /*然后与服务器端进行连接，将当前用户的用户名与密码发送到服务器端*/
                    /*服务器端发回当前所有在线信息*/
                    ServerConnection serverConnection=new ServerConnection();//客户端与服务器端建立连接

                    /*业务*///将当前用户的用户名发送到服务器端
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.setType("1");
                    chatMessage.setMessage(name);//传递给服务器端当前登陆该聊天室的用户的名字
                    String jsonstr=ComonUtils.objectTOjson(chatMessage);
                    try {
                        /*客户端输出流*/
                        PrintStream out=new PrintStream(serverConnection.getOut(),true,"UTF-8");
                        out.println(jsonstr);//将客户端信息发送给服务器端


                        /*客户端输入流，接收服务端信息*/
                        /*客户端获取服务器端发回的所有的用户在线信息*/
                        Scanner in=new Scanner(serverConnection.getIn());
                        if(in.hasNextLine()) {
                            String message = in.nextLine();
                            ChatMessage FromServer= (ChatMessage) ComonUtils.isonTOobject(message,ChatMessage.class);
                            Set<String>   allUsers= (Set<String>) ComonUtils.isonTOobject(FromServer.getMessage(),Set.class);
                            System.out.println("所有在线用户为："+allUsers);

                            /*加载用户列表界面*/
                            /*
                            * name：登录用户
                            * allUsers：当前在线所有用户
                            * serverConnection：客户端与服务器端链接
                            * */
                            new friendList(name,allUsers,serverConnection);
                        }

                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }



                    /*加载用户列表页面：监听服务器端发来的用户上线信息更新用户列表*/
                }else{

                    //登录失败
                    JOptionPane.showMessageDialog(frame,"登陆失败");
                }
            }
        });
    }

    /*运行弹出注册登录的图标*/
    public static void main(String[] args) {
       start start=new start();

    }
}
