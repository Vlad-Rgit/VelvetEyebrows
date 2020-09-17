package com.example.velveteyebrows.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.velveteyebrows.dto.ServiceDTO;
import org.jetbrains.annotations.NotNull;

public class ClientServiceViewModelFactory implements ViewModelProvider.Factory {

    private final ServiceDTO _serviceDTO;
    private final Application _application;

    public ClientServiceViewModelFactory(Application application, ServiceDTO serviceDTO){
        _application = application;
        _serviceDTO = serviceDTO;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ClientServiceViewModel.class)){
            return (T) new ClientServiceViewModel(_application, _serviceDTO);
        }
        else{
            throw new RuntimeException("Invalid view model type " + modelClass.getTypeName());
        }
    }
}
