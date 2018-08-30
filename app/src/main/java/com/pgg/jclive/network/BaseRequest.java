package com.pgg.jclive.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pengganggui on 2018/8/30.
 * 封装请求基类
 */

public abstract class BaseRequest {

    protected final int WHAT_FAIL=0;
    protected final int WHAT_SUCC=1;

    private final static OkHttpClient okClient=new OkHttpClient();
    protected final static Gson gson=new Gson();

    public interface OnResultListener<T>{
        void onFail(int code,String msg);

        void onSuccess(T object);
    }

    private OnResultListener listener;

    private Handler uiHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if (what==WHAT_FAIL){
                if (listener!=null){
                    listener.onFail(msg.arg1,(String)msg.obj);
                }
            }else if (what==WHAT_SUCC){
                if (listener!=null){
                    listener.onSuccess(msg.obj);
                }
            }
        }
    };

    /**
     * 发送请求失败消息
     * @param code
     * @param reason
     */
    protected void sendFailMsg(int code,String reason){
        Message msg=uiHandler.obtainMessage(WHAT_FAIL);
        msg.arg1=code;
        msg.obj=reason;
        uiHandler.sendMessage(msg);
    }

    protected <T> void sendSuccMsg(T data){
        Message msg=uiHandler.obtainMessage(WHAT_SUCC);
        msg.obj=data;
        uiHandler.sendMessage(msg);
    }

    public void setOnResultListener(OnResultListener l){
        listener=l;
    }

    public void request(String url){
        final Request request=new Request.Builder()
                .url(url).build();
        okClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //不是UI线程
                if (response.isSuccessful()) {
                    onResponseSuccess(response.body().string());
                } else {
                    onResponseFail(response.code());
                }
            }
        });
    }

    /**
     * 请求失败
     * @param code
     */
    public abstract void onResponseFail(int code) ;

    /**
     * 请求成功回调
     * @param string
     */
    public abstract void onResponseSuccess(String string);

    /**
     * 网络失败
     * @param e
     */
    public abstract void onFail(IOException e);

}
