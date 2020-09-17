package com.example.velveteyebrows.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import org.jetbrains.annotations.NotNull;

public class AppointmentsViewModelFactory implements ViewModelProvider.Factory {

    private final int _clientId;

    public AppointmentsViewModelFactory(int clientId) {
        _clientId = clientId;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AppointmentsViewModel(_clientId);
    }
}
