package com.example.velveteyebrows.activities;

import android.app.Application;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.android.volley.toolbox.ImageLoader;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.databinding.ActivityClientServiceBinding;
import com.example.velveteyebrows.network.ServiceImageRequester;
import com.example.velveteyebrows.services.HttpHelper;
import com.example.velveteyebrows.viewmodels.ClientServiceViewModel;
import com.example.velveteyebrows.viewmodels.ClientServiceViewModelFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.velveteyebrows.dto.ServiceDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.LinkedList;

public class ClientServiceActivity extends AppCompatActivity {

    public static final String SERVICE_ARG = "ServiceArg";

    private ClientServiceViewModel _viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServiceDTO serviceDTO = (ServiceDTO)
                getIntent().getExtras().getSerializable(SERVICE_ARG);

        Application application = getApplication();

        _viewModel = new ViewModelProvider(this,
                new ClientServiceViewModelFactory(application, serviceDTO))
                .get(ClientServiceViewModel.class);


        ActivityClientServiceBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_client_service);


        String url = AppData.API_ADDRESS_SLASH
                + "images/service?serviceId="+ serviceDTO.getId();

        ImageLoader imageLoader = ServiceImageRequester.getInstance(this)
                .getImageLoader();


        binding.clservImage.setImageUrl(url, imageLoader);


        binding.clservBtnPickDate.setOnClickListener((View v)->{

            CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();

            LocalDateTime localDateTime = LocalDateTime.now();
            calendarConstraints.setStart(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
            localDateTime = localDateTime.plusMonths(2);
            calendarConstraints.setEnd(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());

           MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
           MaterialDatePicker<Long> datePicker = builder
                   .setTheme(R.style.Theme_VelvetEyebrows_Calendar)
                   .setCalendarConstraints(calendarConstraints.build())
                   .build();

           datePicker.addOnPositiveButtonClickListener((Long epochMilli) ->
                   _viewModel.updateDate(epochMilli));

           datePicker.show(getSupportFragmentManager(), null);

        });

        _viewModel.scheduleRows.observe(this, scheduleRows -> {

            binding.clservChipGroupTime.removeAllViews();
            binding.clservChipGroupTime.setSingleSelection(true);
            for(ClientServiceViewModel.ScheduleRow scheduleRow : scheduleRows){
                addChipToGroup(binding.clservChipGroupTime, scheduleRow);
            }
        });

        binding.clservChipGroupTime.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                int index = 0;
                for(int i = 0; i < group.getChildCount(); i++){
                    if(group.getChildAt(i).getId() == checkedId){
                        index = i;
                        break;
                    }
                }

                int counter = 0;

                for(ClientServiceViewModel.ScheduleRow scheduleRow : _viewModel.scheduleRows.getValue()){
                    //Set checked only the row with checked chip index
                    scheduleRow.setChecked(counter++ == index);
                }
            }
        });

        binding.clservBtnAccept.setOnClickListener(v->{

            int chipId = binding.clservChipGroupTime.getCheckedChipId();

            if(chipId == View.NO_ID){
                Toast.makeText(this, "Choose time", Toast.LENGTH_SHORT)
                        .show();
            }
            else{
                _viewModel.accept((responseCode -> {

                    String message;

                    if(HttpHelper.isRequestSuccesful(responseCode)){
                        message = "Sign up completed";
                    }
                    else{
                        message = "Erorr. Response code " + responseCode;
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    finish();
                }));
            }
        });

        binding.setViewModel(_viewModel);
    }


    private void addChipToGroup(ChipGroup chipGroup, ClientServiceViewModel.ScheduleRow scheduleRow){

        Chip chip = (Chip)
                getLayoutInflater().inflate(R.layout.chip_time, chipGroup, false);

        chip.setText(scheduleRow.getStartTime().toString());
        chipGroup.addView(chip);
    }
}