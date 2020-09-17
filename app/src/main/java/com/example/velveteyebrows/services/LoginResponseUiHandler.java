package com.example.velveteyebrows.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import com.example.velveteyebrows.fragments.LoginFragment;
import com.velveteyebrows.dto.ClientDTO;

import javax.annotation.Nullable;


public class LoginResponseUiHandler extends Handler {

    @FunctionalInterface
    public interface OnLoginResponseCallback {
        /**
         * Exposes then http response is gotten
         * @param responseCode The response code
         * @param clientDTO The whole informaion about the client.
         *                  Can be null if the response code is FORBIDDEN
         */
        void onLoginResponse(int responseCode, @Nullable ClientDTO clientDTO);
    }

    public static final String CLIENT_DATA = "ClientData";
    public static final String RESPONSE_CODE = "ResponseCode";

    private final OnLoginResponseCallback _onLoginResponseCallback;

    public LoginResponseUiHandler(Looper looper, OnLoginResponseCallback callback){
        super(looper);
        _onLoginResponseCallback = callback;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);

        Bundle msgData = msg.getData();

        ClientDTO clientDTO = (ClientDTO)
                msgData.getSerializable(CLIENT_DATA);
        int responseCode = msgData.getInt(RESPONSE_CODE);

        _onLoginResponseCallback.onLoginResponse(responseCode, clientDTO);
    }



    public static Message buildMessage(int responseCode, ClientDTO clientDTO){

        Bundle messageData = new Bundle();
        messageData.putInt(RESPONSE_CODE, responseCode);
        messageData.putSerializable(CLIENT_DATA, clientDTO);

        Message message = new Message();
        message.setData(messageData);

        return message;
    }
}