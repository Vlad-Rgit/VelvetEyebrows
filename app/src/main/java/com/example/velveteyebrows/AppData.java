package com.example.velveteyebrows;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.velveteyebrows.dto.ClientDTO;

import static android.content.Context.MODE_PRIVATE;

public class AppData {

    public final static String SERVER_SLASH = "http://192.168.88.234:2500/";
    public final static String API_ADDRESS_SLASH = SERVER_SLASH + "api/";

    public static final String PREF_CLIENT_EMAIL = "PrefClientEmail";
    public static final String PREF_CLIENT_PASS = "PrefClientPass";

    private static ClientDTO clientInstance;

    public static boolean isLocal = false;

    public static ClientDTO getUser(Context context){
        SharedPreferences preferences = getClientPreferences(context);

        ClientDTO clientDTO = new ClientDTO();

        clientDTO.setEmail(preferences.getString(PREF_CLIENT_EMAIL, null));
        clientDTO.setPassword(preferences.getString(PREF_CLIENT_PASS, null));

        return clientDTO;
    }

    public static void saveUser(Context context, ClientDTO clientDTO){
        SharedPreferences preferences = getClientPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PREF_CLIENT_EMAIL, clientDTO.getEmail());
        editor.putString(PREF_CLIENT_PASS, clientDTO.getPassword());

        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void clearUser(Context context){

        SharedPreferences preferences = getClientPreferences(context);

        preferences.edit()
                .remove(PREF_CLIENT_PASS)
                .remove(PREF_CLIENT_EMAIL)
                .commit();
    }


    public static  boolean isUserLogged(Context context) {
        SharedPreferences preferences = getClientPreferences(context);
        return preferences.contains(PREF_CLIENT_EMAIL);
    }

    public static SharedPreferences getClientPreferences(Context context){
        return context.getApplicationContext()
                .getSharedPreferences("ClientData", MODE_PRIVATE);
    }

    public static ClientDTO getClientInstance() {
        return clientInstance;
    }

    public static void setClientInstance(ClientDTO clientInstance) {
        AppData.clientInstance = clientInstance;
    }
}
