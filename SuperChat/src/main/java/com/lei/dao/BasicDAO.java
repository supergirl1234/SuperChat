package com.lei.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.lei.util.ComonUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/*加载数据库资源，连接数据库*/
public class BasicDAO {

    private static DruidDataSource druidDataSource;

    /*获取数据源，类一加载，就执行了该静态代码*/
    static {

        Properties properties = ComonUtils.loadProperties("datasource.properties");
        try {
            druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*获取连接*/
    public static DruidPooledConnection getConnection() {

        try {
            return (DruidPooledConnection) druidDataSource.getPooledConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*关闭资源*/
    public static void close(Connection connection,
                             Statement statement,
                             ResultSet resultSet) {

        try {

            if(resultSet!=null){
                resultSet.close();

            }
            if(statement!=null){

                statement.close();
            }
            if (connection != null) {

                connection.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void close(Connection connection,
                             Statement statement) {

        try {

            if(statement!=null){

                statement.close();
            }
            if (connection != null) {

                connection.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
