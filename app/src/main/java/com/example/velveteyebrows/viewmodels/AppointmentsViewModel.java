package com.example.velveteyebrows.viewmodels;

import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cf.feuerkrieg.web_api_helper.Requester;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.services.HttpHelper;
import com.example.velveteyebrows.threads.HttpRequestThread;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.velveteyebrows.dto.AppointmentDTO;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsViewModel extends ViewModel {

    public final MutableLiveData<List<AppointmentDTO>> appointments
            = new MutableLiveData<>();

    public AppointmentsViewModel(int clientId){

        GetClientServicesThread thread = new GetClientServicesThread(
                Looper.getMainLooper(), this::onGetAppointments, clientId
        );

        thread.start();
    }


    private void onGetAppointments(int responseCode, ArrayList<AppointmentDTO> result){
        if(HttpHelper.isRequestSuccesful(responseCode)){
           appointments.setValue(result);
        }
    }


    public static class GetClientServicesThread extends HttpRequestThread<ArrayList<AppointmentDTO>>{

        private final int _clientId;

        public GetClientServicesThread(Looper looper,
                                       @Nullable HttpRequestThread.OnHttpRequestCompleteCallback<ArrayList<AppointmentDTO>> callback,
                                       int clientId) {
            super(looper, callback);
            _clientId = clientId;
        }

        @Override
        public void run() {

            String params = String.format("clientId=%d", _clientId);
            String url = AppData.API_ADDRESS_SLASH + "appointments?" + params;

            Requester.Response response = Requester.makeGetRequest(url);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString());
                }
            });


            Type resultType = new TypeToken<ArrayList<AppointmentDTO>>(){}.getType();

            ArrayList<AppointmentDTO> result = gsonBuilder.create().fromJson(response.getJson(), resultType);

            sendResult(response.getResponseCode(), result);
        }
    }
}
