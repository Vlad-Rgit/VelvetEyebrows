package com.example.velveteyebrows.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cf.feuerkrieg.web_api_helper.Requester;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.activities.LoginActivtiy;
import com.example.velveteyebrows.databinding.FragmentLoginBinding;
import com.example.velveteyebrows.services.HttpHelper;
import com.example.velveteyebrows.services.LoginResponseUiHandler;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.velveteyebrows.dto.ClientDTO;

import javax.annotation.Nullable;


public class LoginFragment extends Fragment {

    public static final String CLIENT_STATE = "ClientState";

    private ClientDTO _client;
    private LoginActivtiy _activtiy;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        _activtiy = (LoginActivtiy) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentLoginBinding binding
                = FragmentLoginBinding.inflate(inflater, container, false);

        //Set data binding
        if(savedInstanceState == null){
            _client = new ClientDTO();
        }
        else{
            _client = (ClientDTO)
                    savedInstanceState.getSerializable(CLIENT_STATE);
        }

        binding.setClient(_client);

        //Set callbacks
        binding.loginBtnAccept.setOnClickListener(this::acceptClick);
        binding.loginBtnBackToReg.setOnClickListener(this::backToRegClick);

        return binding.getRoot();
    }

    private void acceptClick(View view){

        String hashedPassword = Hashing.sha256()
                .hashString(_client.getPassword(), Charsets.UTF_16)
                .toString();

        _client.setPassword(hashedPassword);

        LoginClientThread thread = new LoginClientThread(_client, this::onLoginResponse);
        thread.start();
    }

    private void backToRegClick(View view){
        _activtiy.goToReg();
    }

    private void onLoginResponse(int responseCode, @Nullable ClientDTO clientDTO){

        String message;

        if(HttpHelper.isRequestSuccesful(responseCode)){
            message = "Login succesful " + clientDTO.getEmail();
            _activtiy.loginComplete(clientDTO);
        }
        else if (responseCode == 403){
            message = "Uncorrect";
        }
        else {
            message = "Some problems with internet";
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(CLIENT_STATE, _client);
    }


    public static class LoginClientThread extends Thread {

        private final LoginResponseUiHandler _uiHandler;
        private final ClientDTO _clientDTO;

        public LoginClientThread(ClientDTO clientDTO, LoginResponseUiHandler.OnLoginResponseCallback callback){
            _uiHandler = new LoginResponseUiHandler(Looper.getMainLooper(), callback);
            _clientDTO = clientDTO;
        }

        @Override
        public void run() {


            String params = String.format("login=%s&password=%s",
                    _clientDTO.getEmail(), _clientDTO.getPassword());

            String url = AppData.API_ADDRESS_SLASH + "clients?" + params;

            Requester.Response response  = Requester.makeGetRequest(url);

            ClientDTO clientDTO = null;

            if(HttpHelper.isRequestSuccesful(response.getResponseCode())){
                Gson gson = new Gson();
                clientDTO = gson.fromJson(response.getJson(), ClientDTO.class);
            }

            Message msg = LoginResponseUiHandler.buildMessage(response.getResponseCode(), clientDTO);

            _uiHandler.sendMessage(msg);
        }
    }
}