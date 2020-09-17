package com.example.velveteyebrows.viewmodels;

import android.app.Application;
import android.os.Looper;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;
import cf.feuerkrieg.web_api_helper.Requester;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.gson.adapters.LocalDateTimeAdapter;
import com.example.velveteyebrows.threads.HttpRequestThread;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.velveteyebrows.dto.ClientDTO;
import com.velveteyebrows.dto.ClientServiceDTO;
import com.velveteyebrows.dto.ServiceDTO;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ClientServiceViewModel extends AndroidViewModel {


    public static class CurrentDateTime{

        public final ObservableField<LocalDate> date = new ObservableField<>();
        public final ObservableField<LocalTime> time = new ObservableField<>();

        public CurrentDateTime(){
            date.set(LocalDate.now());
            time.set(LocalTime.now().plus(Duration.ofHours(1)));
        }
    }

    public static class ScheduleRow {
        private boolean isChecked;
        private LocalTime startTime;

        public ScheduleRow(LocalTime startTime){
            this.startTime = startTime;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public String getStartTimeString(){

            SimpleDateFormat formatter = new SimpleDateFormat("H:m", Locale.getDefault());

            return formatter.format(startTime);
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }
    }

    @FunctionalInterface
    public interface OnAcceptCompleteCallback{
        void onAcceptComplete(int responseCode);
    }

    private final ClientDTO _clientDTO;
    private final ServiceDTO _serviceDTO;
    private final ClientServiceDTO _clientServiceDTO;
    public final MutableLiveData<LinkedList<ScheduleRow>> scheduleRows = new MutableLiveData<>();

    private final CurrentDateTime _currentDateTime = new CurrentDateTime();

    public ClientServiceViewModel(Application application, ServiceDTO serviceDTO){

        super(application);

        _clientDTO = AppData.getClientInstance();

        if(_clientDTO == null){
            throw new RuntimeException("User is not logged");
        }

        _serviceDTO = serviceDTO;

        _clientServiceDTO = new ClientServiceDTO();
        _clientServiceDTO.setServiceId(_serviceDTO.getId());
        _clientServiceDTO.setClientId(_clientDTO.getId());
        _clientServiceDTO.setStartTime(LocalDateTime.now());


        GetClientServicesThread thread = new GetClientServicesThread(Looper.getMainLooper(),
                this::onGetClientServices, _serviceDTO.getId());

        thread.start();
    }

    public ServiceDTO getServiceDTO() {
        return _serviceDTO;
    }

    public ClientServiceDTO getClientServiceDTO(){
        return _clientServiceDTO;
    }

    public ClientDTO getClientDTO(){
        return _clientDTO;
    }

    public CurrentDateTime getCurrentDateTime() {
        return _currentDateTime;
    }


    public void accept(OnAcceptCompleteCallback listener){


        for(ScheduleRow scheduleRow : scheduleRows.getValue()){
            if(scheduleRow.isChecked){
                LocalDateTime dateTime = LocalDateTime.of(
                        _currentDateTime.date.get(), scheduleRow.getStartTime()
                );
                _clientServiceDTO.setStartTime(dateTime);
            }
        }

        boolean isNotificate = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext())
                .getBoolean("notificate", true);


        PostClientServiceThread thread = new PostClientServiceThread(
                Looper.getMainLooper(),
                ((responseCode, result) -> listener.onAcceptComplete(responseCode)),
                _clientServiceDTO,
                isNotificate
        );

        thread.start();
    }

    private void onGetClientServices(int responseCode, ArrayList<ClientServiceDTO> clientServices) {

        LocalTime startTime = LocalTime.of(10, 0);
        Duration step = Duration.ofMinutes(_serviceDTO.getDuration() + 10);

        LocalTime currentTime = LocalTime.of(10,0);

        LinkedList<ScheduleRow> times = new LinkedList<>();

        do{

            boolean isTimeOccupied = false;

            for(ClientServiceDTO clientServiceDTO : clientServices){
                LocalTime timeOccupied = clientServiceDTO.getStartTime().toLocalTime();

                if(currentTime.equals(timeOccupied)){
                    currentTime = currentTime.plus(step);
                    isTimeOccupied = true;
                    break;
                }
            }


            if(!isTimeOccupied){
                ScheduleRow row = new ScheduleRow(currentTime);
                times.add(row);
            }

            currentTime = currentTime.plus(step);

        } while(currentTime.getHour() <= 20);

        scheduleRows.setValue(times);
    }

    public void updateDate(long epochMilli){

        LocalDate date = Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        _currentDateTime.date.set(date);

        System.out.println(_currentDateTime.date.get().toString());
    }


    public static class GetClientServicesThread extends HttpRequestThread<ArrayList<ClientServiceDTO>> {

        private final int serviceId;

        public GetClientServicesThread(Looper looper,
                                       OnHttpRequestCompleteCallback<ArrayList<ClientServiceDTO>> callback,
                                       int serviceId) {
            super(looper, callback);
            this.serviceId = serviceId;
        }

        @Override
        public void run() {

            String params = String.format("serviceId=%d", serviceId);

            String url = AppData.API_ADDRESS_SLASH + "clientServices?" + params;

            Requester.Response response = Requester.makeGetRequest(url);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString());
                }
            });

            Type resultType = new TypeToken<List<ClientServiceDTO>>(){}.getType();

            ArrayList<ClientServiceDTO> result = gsonBuilder.create()
                    .fromJson(response.getJson(), resultType);

            sendResult(response.getResponseCode(), result);
        }
    }

    public static class PostClientServiceThread extends HttpRequestThread<ClientServiceDTO>{

        private final ClientServiceDTO _clientServiceDTO;
        private final boolean _isNotificate;

        public PostClientServiceThread(Looper looper,
                                       OnHttpRequestCompleteCallback<ClientServiceDTO> callback,
                                       ClientServiceDTO clientServiceDTO,
                                       boolean isNotificate) {
            super(looper, callback);
            _clientServiceDTO = clientServiceDTO;
            _isNotificate = isNotificate;
        }

        @Override
        public void run() {


            String url = AppData.API_ADDRESS_SLASH + "clientServices";

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

            _clientServiceDTO.setNotificate(_isNotificate ? 1 : 0);
            String jsonBody = gsonBuilder.create().toJson(_clientServiceDTO);

            Requester.Response response
                    = Requester.makeQueryRequest(url, jsonBody, "POST");

            sendResult(response.getResponseCode());
        }
    }
}
