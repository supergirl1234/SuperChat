package com.lei.vo;


/*服务器与客户端传递信息载体*/
public class ChatMessage {

    private String type;//选择聊天类型;私聊、群聊； 1代表登录，2代表私聊，3代表群注册，4代表群聊
    private String message;//聊天信息
    private  String toPerson;//聊天对象

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToPerson() {
        return toPerson;
    }

    public void setToPerson(String toPerson) {
        this.toPerson = toPerson;
    }
}
