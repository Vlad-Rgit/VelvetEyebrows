package com.example.velveteyebrows.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import cf.feuerkrieg.web_api_helper.Requester;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.activities.LoginActivtiy;
import com.example.velveteyebrows.databinding.FragmentRegisterBinding;
import com.example.velveteyebrows.services.HttpHelper;
import com.example.velveteyebrows.services.LoginResponseUiHandler;
import com.example.velveteyebrows.services.RemoveEmptyErrorWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.velveteyebrows.dto.ClientDTO;


public class RegisterFragment extends Fragment {

    private ClientDTO _client;

    public static final String CLIENT_STATE_ARG = "ClientState";
    private LoginActivtiy _activity;
    private FragmentRegisterBinding _binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _activity = (LoginActivtiy) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RefisterFragment", "OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("RefisterFragment", "OnCreateView");
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register,
                container, false);

        if(savedInstanceState == null) {
            _client = new ClientDTO();
        }
        else{
            _client = (ClientDTO)
                    savedInstanceState.getSerializable(CLIENT_STATE_ARG);
        }

        _binding.setClient(_client);

        _binding.btnRegContinue.setOnClickListener(this::onContinueClick);
        _binding.btnReqToLogin.setOnClickListener(this::goToLoginClick);

        setCheckInputCallbacks();

        return _binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("RefisterFragment", "OnPause");
    }


    /**
     * Add text watchers to TextInputEditTexts to remove errors when user enters correct input
     */
    private void setCheckInputCallbacks(){

        //Add watchers to remove empty error
        _binding.edName.addTextChangedListener(new RemoveEmptyErrorWatcher(
                _binding.layEdName, _binding.edName));
        _binding.edLastName.addTextChangedListener(new RemoveEmptyErrorWatcher(
                _binding.layEdLastName, _binding.edLastName));
        _binding.edPatronymic.addTextChangedListener(new RemoveEmptyErrorWatcher(
                _binding.layEdPatronymic, _binding.edPatronymic));
        _binding.edPhone.addTextChangedListener(new RemoveEmptyErrorWatcher(
                _binding.layEdPhone, _binding.edPhone));

        //Add watchers to remove pattern mathcers errors
        _binding.edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Patterns.EMAIL_ADDRESS.matcher(_binding.edEmail.getText()).matches()){
                    _binding.layEdEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        _binding.edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(_binding.edPassword.getText().length() >= 8){
                    _binding.layEdPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkEmptyEditTexts(){

        boolean hasError = false;

        for(int i = 0; i<_binding.regRoot.getChildCount(); i++){

            View v = _binding.regRoot.getChildAt(i);

            if(v instanceof TextInputLayout){
                TextInputLayout textLayout = (TextInputLayout)v;

                if(textLayout.getEditText().getText().toString().trim().isEmpty()){
                    textLayout.setError("The field cannot be empty");
                    hasError = true;
                }
            }
        }

        return hasError;
    }

    private void onContinueClick(View view){

        boolean hasError = checkEmptyEditTexts();

        if(_client.getEmail() == null ||
                !Patterns.EMAIL_ADDRESS.matcher(_client.getEmail()).matches()){
            hasError = true;
            _binding.layEdEmail.setError("Email is not valid");
        }

        if(_binding.edPassword.getText() == null ||
                _binding.edPassword.getText().length() < 8){
            hasError = true;
            _binding.layEdPassword.setError("Password must be at least 8 length");
        }

        if(!hasError) {
            RegisterClientThread thread = new RegisterClientThread(_client, this::onRegisterComplete);
            thread.start();
        }
    }

    private void onRegisterComplete(int responseCode, ClientDTO clientDTO){
        if(HttpHelper.isRequestSuccesful(responseCode)){
            Toast.makeText(_activity, "Register is completed " + clientDTO.getEmail(),
                    Toast.LENGTH_SHORT).show();

            _activity.loginComplete(clientDTO);
        }
        else if (responseCode == 403){
            Toast.makeText(_activity, "User with this email already exsists", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(_activity, "Resgister is failed.. We can not do anything",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginClick(View view){
        _activity.goToLogin();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("RefisterFragment", "OnStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(CLIENT_STATE_ARG, _client);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("RefisterFragment", "OnDestroyView");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RefisterFragment", "OnDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("RefisterFragment", "OnDetach");
    }

    public static class RegisterClientThread extends Thread {

        private final ClientDTO _clientDTO;
        private final LoginResponseUiHandler _uiHandler;

        public RegisterClientThread (ClientDTO clientDTO, LoginResponseUiHandler.OnLoginResponseCallback callback){
            _uiHandler = new LoginResponseUiHandler(Looper.getMainLooper(), callback);
            _clientDTO = clientDTO;
        }

        @Override
        public void run() {

            String url = AppData.API_ADDRESS_SLASH + "clients";

            Gson gson = new Gson();
            String jsonBody = gson.toJson(_clientDTO);

            Requester.Response response =
                    Requester.makeQueryRequest(url, jsonBody, "POST");

            ClientDTO clientDTO = gson.fromJson(response.getJson(), ClientDTO.class);

            Message message = LoginResponseUiHandler.buildMessage(response.getResponseCode(), clientDTO);

            _uiHandler.sendMessage(message);
        }
    }
}
