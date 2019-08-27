package com.lei.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/*json字符串{"1":"he","2":"he'"}*/
public class ComonUtils {

    private static  final Gson gson=new GsonBuilder().create();//?

    /*加载配置文件datasource.properties文件*/
    public static Properties loadProperties(String filename){
        Properties properties=new Properties();
        InputStream in=ComonUtils.class.getClassLoader().getResourceAsStream(filename);
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  properties;
    }

    /*凡是序列化出来的都是深拷贝*/
    /*将任意对象序列化为json字符串*/

    public static String objectTOjson(Object object){
        return  gson.toJson(object);

    }

    /*将任意字符串反序列化为对象*/
    /*str:json字符串
    * classNmae：反序列化的类反射对象
    * */
    public static  Object isonTOobject(String str,Class classNmae){
        return  gson.fromJson(str,classNmae);

    }

}
