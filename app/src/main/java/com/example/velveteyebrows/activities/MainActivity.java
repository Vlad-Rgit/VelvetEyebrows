package com.example.velveteyebrows.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;

import com.example.velveteyebrows.callbacks.OnGetClientCompletedCallback;
import com.example.velveteyebrows.fragments.*;
import com.example.velveteyebrows.services.HttpHelper;
import com.example.velveteyebrows.services.LoginResponseUiHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.velveteyebrows.dto.ClientDTO;
import com.velveteyebrows.dto.ServiceDTO;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_FOR_APPOINTS_REQUEST = 1;
    public static final int LOGIN_FOR_SCHEDULE_REQUETS = 2;


    private BottomNavigationView _btm_nav_main;
    private FrameLayout _backLayerContainer;

    private final HashMap<String, Fragment.SavedState> _savedStates
            = new HashMap<>();

    private FragmentManager _fragmentManager;
    private View _backLayer;
    private Fragment _backLayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        _fragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);

        /*toolbar.setNavigationOnClickListener(new BackdropRevealClickListener(
                this, findViewById(R.id.front_layer)
        ));*/

        navigateFragment(new CatalogFragment());

        _btm_nav_main = findViewById(R.id.btm_nav_main);

        _btm_nav_main.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_catalog:
                        navigateFragment(new CatalogFragment());
                        break;
                    case R.id.navigation_apoints:
                        if(AppData.isUserLogged(MainActivity.this)){
                            getClientFromServer((clientDTO)->{
                                navigateFragment(AppointmentsFragment.newInstance(clientDTO.getId()));
                            });
                        }
                        else{
                            goToRegisterActvity(LOGIN_FOR_APPOINTS_REQUEST);
                        }
                        break;
                    case R.id.navigation_settings:
                        navigateFragment(new SettingsFragment());
                        break;
                }

                return true;
            }
        });


        _backLayerContainer = findViewById(R.id.back_layer_container);
    }

    public void goToRegisterActvity(int requestCode){
        Intent intent = new Intent(MainActivity.this, LoginActivtiy.class);
        startActivityForResult(intent, requestCode);
    }

    private ServiceDTO _waitedService;
    public void goToSignUp(ServiceDTO serviceDTO){
        if(!AppData.isUserLogged(this)){
            _waitedService = serviceDTO;
            goToRegisterActvity(LOGIN_FOR_SCHEDULE_REQUETS);
        }
        else if(AppData.getClientInstance() == null){
            getClientFromServer(clientDTO -> startSignUpActivity(serviceDTO));
        }
        else{
            startSignUpActivity(serviceDTO);
        }
    }

    private void startSignUpActivity(ServiceDTO serviceDTO){
        Intent intent = new Intent(this, ClientServiceActivity.class);
        intent.putExtra(ClientServiceActivity.SERVICE_ARG, serviceDTO);
        startActivity(intent);
    }

    private void getClientFromServer(@Nullable OnGetClientCompletedCallback callback){

        ClientDTO clientDTO = AppData.getUser(MainActivity.this);

        LoginFragment.LoginClientThread thread =
                new LoginFragment.LoginClientThread(clientDTO, new LoginResponseUiHandler.OnLoginResponseCallback() {
                    @Override
                    public void onLoginResponse(int responseCode, @org.jetbrains.annotations.Nullable ClientDTO clientDTO) {
                        if(HttpHelper.isRequestSuccesful(responseCode)){
                            AppData.setClientInstance(clientDTO);
                            if(callback != null) {
                                callback.onGetClientCompleted(clientDTO);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this,
                                    "Error request\nResponse code " + responseCode,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        thread.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode, data);

        if(resultCode == RESULT_OK) {

            ClientDTO clientDTO = (ClientDTO)
                    data.getSerializableExtra(LoginActivtiy.LOGGED_USER);
            AppData.saveUser(this, clientDTO);

            switch (requestCode) {
                case LOGIN_FOR_APPOINTS_REQUEST:
                    _btm_nav_main.setSelectedItemId(R.id.navigation_apoints);
                    break;
                case LOGIN_FOR_SCHEDULE_REQUETS:
                    _btm_nav_main.setSelectedItemId(R.id.navigation_catalog);
                    goToSignUp(_waitedService);
                    break;
            }
        }
        else {
            _btm_nav_main.setSelectedItemId(R.id.navigation_catalog);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void navigateFragment(Fragment fragment){

        List<Fragment> fragmentList = _fragmentManager.getFragments();

        if(fragmentList.size()>0) {
            Fragment currentFragment = _fragmentManager.getFragments().get(0);
            Fragment.SavedState savedState = _fragmentManager.saveFragmentInstanceState(currentFragment);

            _savedStates.put(currentFragment.getClass().getName(),
                    savedState);
        }

        String key = fragment.getClass().getName();

        if(_savedStates.containsKey(key)){
            fragment.setInitialSavedState(_savedStates.get(key));
        }

        FragmentTransaction transaction
                = _fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_main, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    public void setBacklayer(int resourceId){

       _backLayer = getLayoutInflater().inflate(resourceId, _backLayerContainer, false);
       _backLayerContainer.addView(_backLayer);
    }

    public void setBacklayer(View view)
    {
        _backLayer = view;
        _backLayerContainer.addView(_backLayer);
    }

    public void setBacklayer(Fragment fragment){

        _backLayerFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.back_layer_container, _backLayerFragment)
                .commit();
    }

    public View getBackLayer(){
        return _backLayer;
    }

    public Fragment getBackLayerFragment(){
        return _backLayerFragment;
    }

}
