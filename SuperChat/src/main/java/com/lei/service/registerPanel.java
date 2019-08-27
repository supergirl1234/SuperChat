package com.lei.service;

import com.lei.po.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.lei.dao.RealDAO.register;

public class registerPanel {
    private JTextField Name;
    private JPasswordField Pwd;
    private JPanel all;
    private JLabel username;
    private JLabel password;
    private JTextField per;
    private JLabel perSign;
    private JButton button;


    /*弹出注册的图标 for main*/
    public registerPanel() {
        final JFrame frame = new JFrame("注册界面");
        frame.setContentPane(all);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400,300);

        frame.setVisible(true);

        /*单击注册按钮，将信息持久化到db中，成功则弹出提示框*/
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = Name.getText();
                String pwd = new String(Pwd.getPassword());
                String message = per.getText();
                User user = new User();
                user.setUsername(name);
                user.setPassword(pwd);
                user.setPerSign(message);
                boolean result=register(user);
                if(result){
                    /*注册成功*/

                    JOptionPane.showMessageDialog(frame,"注册成功");
                    //成功之后
                    frame.setVisible(false);//让注册页面不可见
                }else{

                    /*注册失败*/
                    JOptionPane.showMessageDialog(frame,"注册失败");
                }

            }
        });
    }
}
