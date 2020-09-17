package com.example.velveteyebrows.threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public abstract class HttpRequestThread<T extends Serializable> extends Thread {

    private final HttpRequestUiHandler<T> _uiHandler;

    public HttpRequestThread(Looper looper, @Nullable OnHttpRequestCompleteCallback<T> callback){
        _uiHandler = new HttpRequestUiHandler<T>(looper, callback);
    }

    protected void sendResult(int responseCode){
        Bundle messageData = buildMessageData(responseCode);
        Message message = new Message();
        message.setData(messageData);
        _uiHandler.sendMessage(message);
    }

    protected void sendResult(int responseCode, T result){
         Bundle messageData = buildMessageData(responseCode, result);
         Message message = new Message();
         message.setData(messageData);
        _uiHandler.sendMessage(message);
    }

    private Bundle buildMessageData(int responseCode){

        Bundle messageData = new Bundle();
        messageData.putInt(HttpRequestUiHandler.RESPONSE_CODE, responseCode);

        return messageData;
    }

    private Bundle buildMessageData(int responseCode, T result){

        Bundle messageData = buildMessageData(responseCode);
        messageData.putSerializable(HttpRequestUiHandler.RESULT_ARG, result);
        return messageData;
    }

    public interface OnHttpRequestCompleteCallback<T extends Serializable>{
        void onHttpRequestCompelete(int responseCode, @Nullable T result);
    }

    public static class HttpRequestUiHandler<T extends Serializable> extends Handler {

        public static final String RESPONSE_CODE = "ResponseCode";
        public static final String RESULT_ARG = "ResultArg";

        private final HttpRequestThread.OnHttpRequestCompleteCallback<T> _callback;

        public HttpRequestUiHandler(Looper looper, @Nullable HttpRequestThread.OnHttpRequestCompleteCallback<T> callback){
            super(looper);
            _callback = callback;
        }

        @Override
        public void handleMessage(@NonNull Message message) {

            Bundle messageData = message.getData();

            int resonseCode = messageData.getInt(RESPONSE_CODE);
            T result =(T) messageData.getSerializable(RESULT_ARG);

            if(_callback != null) {
                _callback.onHttpRequestCompelete(resonseCode, result);
            }
        }
    }
}
