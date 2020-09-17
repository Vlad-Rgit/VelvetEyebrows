package com.example.velveteyebrows.activities;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.activities.abstracts.NavigatableActivity;
import com.example.velveteyebrows.fragments.LoginFragment;
import com.example.velveteyebrows.fragments.RegisterFragment;
import com.velveteyebrows.dto.ClientDTO;

public class LoginActivtiy extends NavigatableActivity {

    public static final String LOGGED_USER = "LoggedUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activtiy);

        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        goToReg();
    }

    public void goToReg(){
        navigateToFragment(R.id.loginFragmentContainer, new RegisterFragment(), false);
        getSupportActionBar().setTitle("Register");
    }

    public void goToLogin(){
        navigateToFragment(R.id.loginFragmentContainer, new LoginFragment(), true);
        getSupportActionBar().setTitle("Login");
    }

    public void loginComplete(ClientDTO clientDTO){
        Intent intent = new Intent();
        intent.putExtra(LOGGED_USER, clientDTO);

        setResult(RESULT_OK, intent);
        finish();
    }
}