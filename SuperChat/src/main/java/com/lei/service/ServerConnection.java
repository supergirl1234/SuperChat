package com.lei.service;

import com.lei.util.ComonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/*客户端与服务器端建立连接*/
public class ServerConnection {

    private static  final String host;
    private static  final int port;

    /*加载文件*/
    static {

        Properties properties=ComonUtils.loadProperties("socket.properties");
        host=properties.getProperty("host");
        port=Integer.parseInt(properties.getProperty("port"));

    }

    private Socket client;//客户端
    private InputStream in;//客户端输入流
    private OutputStream out;//客户端输出流

    public ServerConnection() {
        try {
            client = new Socket(host,port);//创建客户端
            in=client.getInputStream();
            out=client.getOutputStream();
        } catch (IOException e) {
            System.out.println("与服务器创建链接失败");
            e.printStackTrace();
        }
    }

    /*获取输入流*/
    public InputStream getIn() {
        return in;
    }

    /*输出流*/
    public OutputStream getOut() {
        return out;
    }
}
