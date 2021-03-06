package com.youli.companybigwenjuan.util;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liutao on 2017/9/21.
 */

public class MyOkHttpUtils {
    //mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public static final String BaseUrl="http://183.194.4.58:89";
    public static OkHttpClient okHttpClient=null;


    static String cookies;

    //懒汉
    private static synchronized OkHttpClient getInstance(){

          if(okHttpClient==null){

              okHttpClient=new OkHttpClient();

          }
        cookies=SharedPreferencesUtils.getString("cookies");



          return  okHttpClient;
    }


    /**
     * OKHttp 同步 Get
     *
     * @param url 请求网址
     * @return 获取到数据返回Response，若未获取到数据返回null
     */

    public static Response okHttpGet(String url){
        getInstance();

       // String cookies = SharedPreferencesUtils.getString("cookies");

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).build();



        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    }

    /**
     * OKHttp 异步 Post
     *
     * @param url 请求网址
     * @return 获取到数据返回Response，若未获取到数据返回null
     */
    public static Response okHttpPost(String url, HashMap<String,String> paramsMap){

        getInstance();

        FormBody.Builder builder = new FormBody.Builder();

        for(String key:paramsMap.keySet()){
            builder.add(key,paramsMap.get(key));
        }

        RequestBody requestBody=builder.build();

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).post(requestBody).build();

        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    };

    //失业无业的调查提交
    public static Response okHttpPost(String url, String ID, String DQYX, String MQZK, String DATE){
        getInstance();
        RequestBody requestBody=new FormBody.Builder()
                .add("MDID",ID).add("DCBZ",DQYX).add("MQZK_NEW",MQZK)
                .add("DQYX_NEW",DATE)
                .build();
        Request request=new Request.Builder().url(url)
                .post(requestBody).addHeader("cookie",cookies).build();
        Response response;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return  response;
    }



    public static Response okHttpPost(String url, String userName){

        getInstance();

        RequestBody requestBody=new FormBody.Builder().add("sfz", userName)
                .build();

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).post(requestBody).build();

        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    };



    //提交图片
    public static Response okHttpPostTuPian(String url, String ID){
        getInstance();
        RequestBody requestBody=new FormBody.Builder()
                .add(",Img_No1",ID).build();
        Request request=new Request.Builder().url(url)
                .post(requestBody).addHeader("cookie",cookies).build();
        Response response;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return  response;
    }

}
