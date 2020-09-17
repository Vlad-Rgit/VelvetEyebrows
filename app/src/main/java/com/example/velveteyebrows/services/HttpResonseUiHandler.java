package com.example.velveteyebrows.services;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class HttpResonseUiHandler extends Handler {

    public static final String RESPONSE_CODE_KEY = "ResponseCodeKey";

    private final OnCompletedRequestCallback _onCompletedRequestCallback;

    public HttpResonseUiHandler(Looper looper, OnCompletedRequestCallback callback){
        super(looper);
        _onCompletedRequestCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int responseCode = msg.getData().getInt(RESPONSE_CODE_KEY);
        _onCompletedRequestCallback.onCompletedRequest(responseCode);
    }
}
