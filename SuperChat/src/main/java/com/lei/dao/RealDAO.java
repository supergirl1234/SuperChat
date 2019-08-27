package com.lei.dao;

import com.lei.po.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/*连接数据库后进行操作，用户注册是往数据库中插入数据；用户登录是查询数据库中是否有该操作；*/
public class RealDAO extends BasicDAO {

    /*用户注册 */
    public  static  boolean register(User user){

        Connection connection=null;
        PreparedStatement statement=null;

        connection=getConnection();
        String result1=user.getUsername();
        String result2=user.getPassword();
        String result3=user.getPerSign();


        String sql="insert into user(username,password,perSign)value ('"+result1+"','"+result2+"','"+result3+"')";
        try {
            statement=connection.prepareStatement(sql);



           /* statement.setString(1,result1);
            statement.setString(2,result2);
            statement.setString(3,result3);*/


            int value=statement.executeUpdate(sql);
            if(value!=0){
                System.out.println("注册成功");
                return  true;
            }else{
                System.out.println("注册失败");
                return  false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            close(connection,statement);
        }
        return false;
    }
    /*用户登录*/
    public  static  User login(String username,String password){

        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        connection=getConnection();
        String sql="select * from user where username='"+username+"' and  password='"+password+"'";

        try {
            statement=connection.prepareStatement(sql);

          /*  statement.setString(1,username);
            statement.setString(2,password);
*/
            resultSet=statement.executeQuery(sql);
            User user=new User();
            if (resultSet.next()){
                Integer id=resultSet.getInt("id");
                String name=resultSet.getString("username");
                String pwd=resultSet.getString("password");
                String per=resultSet.getString("perSign");

                user.setId(id);
                user.setUsername(name);
                user.setPassword(pwd);
                user.setPerSign(per);
            }
            return  user;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
        return null;
    }
}
