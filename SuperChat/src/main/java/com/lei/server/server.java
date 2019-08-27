package com.lei.server;

import com.lei.util.ComonUtils;
import com.lei.vo.ChatMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

    private static  final String host;
    private static  final int port;

    private  static Map<String,Socket>  clientMap=new ConcurrentHashMap<>();//存储所有用户
    //缓存当前服务器注册的所有群名称以及群好友
    private static Map<String,Set<String>>  groupMap=new ConcurrentHashMap<>();

    /*加载文件*/
    static {
        Properties properties=ComonUtils.loadProperties("socket.properties");
        host=properties.getProperty("host");
        port=Integer.parseInt(properties.getProperty("port"));

    }

    /*处理客户端*/
    static class HandleClient implements Runnable{


        private Socket client;//服务器端处理的用户
        private Scanner in; //服务器端输入流
        private PrintStream out;//服务器端输出流

        public HandleClient(Socket client) {
            this.client = client;

            try {
                this.in = new Scanner(client.getInputStream());
                this.out = new PrintStream(client.getOutputStream(), true, "UTF-8");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true){

                /*客户端输入信息，服务器端获取*/
                if(in.hasNextLine()){

                    String message=in.nextLine();//得到的是json字符串
                    /*将字符串反序列化为对象*/
                    ChatMessage chatMessage= (ChatMessage) ComonUtils.isonTOobject(message,ChatMessage.class);

                    /*得到信息之后，服务器端可以根据得到的信息进行处理*/
                    if(chatMessage.getType().equals("1")){//登陆
                        /*登陆到服务器端*/
                        String username=chatMessage.getMessage();//获取当前登录聊天室的用户的名字

                        /*将当前在线的所有用户名发回客户端*/
                        ChatMessage ToClient=new ChatMessage();
                        ToClient.setType("1");
                        ToClient.setMessage(ComonUtils.objectTOjson(clientMap.keySet()));
                        /*将该信息发送给客户端*/
                         out.println(ComonUtils.objectTOjson(ToClient));

                         /*将新上线的用户信息发回给所有在线用户*/
                        SendUserLoginMessage("newLogin:"+username);

                        /*将新用户注册到服务器端缓存*/
                        clientMap.put(username,client);
                        System.out.println(username+"上线了");
                        System.out.println("当前聊天室共"+clientMap.size()+"个用户");

                    }else  if(chatMessage.getType().equals("2")){//私聊

                        /*
                        * Type：2
                        * meassage:myname-msg
                        * toPerson:privatename
                        * */
                        String currentName=chatMessage.getMessage().split("-")[0];//发消息的那个用户
                        String privatemessge=chatMessage.getMessage().split("-")[1];
                        String privateName=chatMessage.getToPerson();//私聊对象
                        Socket client=clientMap.get(currentName);
                        try {
                            /*服务器将消息转给该私聊对象*/
                            PrintStream out=new PrintStream(client.getOutputStream(),true,"UTF-8");
                           ChatMessage chatMessageToClient=new ChatMessage();
                            chatMessageToClient.setType("2");
                            chatMessageToClient.setMessage(chatMessage.getMessage());
                            //System.out.println("收到私聊信息为："+chatMessage.getMessage());
                            out.println(ComonUtils.objectTOjson(chatMessageToClient));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else if(chatMessage.getType().equals("3")){
                        //群注册
                        String groupName=chatMessage.getMessage();
                        /*该群的所有成员*/
                        Set<String> friends= (Set<String>) ComonUtils.isonTOobject(chatMessage.getToPerson(),Set.class);


                        groupMap.put(groupName,friends);
                        System.out.println("群【"+groupName+"】注册成功，目前共"+groupMap.size()+"个群");
                    }else if(chatMessage.getType().equals("4")){


                        /*
                         * type:4
                         * message:currentName-message
                         * toPerson:groupName
                         * */
                        String groupName=chatMessage.getToPerson();//????????????????
                        Set<String>  friends=groupMap.get(groupName);//获取这个组中所有的成员
                        Iterator<String> iterator=friends.iterator();
                        while (iterator.hasNext()){

                            String friendName=iterator.next();
                            Socket clientSocket=clientMap.get(friendName);//群中每个成员的Socket
                            try {
                                PrintStream out=new PrintStream(clientSocket.getOutputStream(),true,"UTF-8");

                                ChatMessage groupMessage=new ChatMessage();
                                groupMessage.setType("4");
                                groupMessage.setMessage(chatMessage.getMessage());
                                /*群名-[好友]*/
                                groupMessage.setToPerson(groupName+"-"+ComonUtils.objectTOjson(friends));//添加上群中所有的好友
                                out.println(ComonUtils.objectTOjson(groupMessage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }



        private void SendUserLoginMessage(String message){

            for(Map.Entry<String,Socket>  entry:clientMap.entrySet()){

                Socket client=entry.getValue();
                try {
                    PrintStream out=new PrintStream(client.getOutputStream(),true,"UTF-8");
                    /*服务器端将信息发送给客户端*/
                    out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket=new ServerSocket(port);

            //创建线程池
            ExecutorService executorService=Executors.newFixedThreadPool(50);
            for(int i=0;i<50;i++){
                System.out.println("服务端启动，等待客户端链接");
                Socket client=serverSocket.accept();
                System.out.println("客户端"+client.getLocalAddress()+"连接成功");

                //线程池执行任务
                executorService.execute(new HandleClient(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


