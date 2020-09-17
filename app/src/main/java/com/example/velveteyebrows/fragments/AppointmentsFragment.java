package com.example.velveteyebrows.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.adapters.AppointmentsRecycleAdapter;
import com.example.velveteyebrows.viewmodels.AppointmentsViewModel;
import com.example.velveteyebrows.viewmodels.AppointmentsViewModelFactory;
import com.velveteyebrows.dto.AppointmentDTO;

import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentsFragment extends Fragment {


    private static final String ARG_CLIENT_ID = "ArgClientId";

    private int _clientId;
    private AppointmentsViewModel _viewModel;
    private AppointmentsRecycleAdapter _adapter;
    private RecyclerView _rvAppointments;


    public AppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param clientId The client for which show appointments
     * @return A new instance of fragment AppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentsFragment newInstance(int clientId) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _clientId = getArguments().getInt(ARG_CLIENT_ID);
        }
        else{
            throw new RuntimeException("Client id is required in arguments");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        _rvAppointments = view.findViewById(R.id.rv_appointments);
        _rvAppointments.setHasFixedSize(true);
        _rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        _viewModel = new ViewModelProvider(this, new AppointmentsViewModelFactory(_clientId))
                .get(AppointmentsViewModel.class);

        _viewModel.appointments.observe(this, rows->{

            Collections.sort(rows, new Comparator<AppointmentDTO>() {
                @Override
                public int compare(AppointmentDTO o1, AppointmentDTO o2) {
                    return o1.getClientServiceDTO().getStartTime()
                            .compareTo(o2.getClientServiceDTO().getStartTime());
                }
            });

            _adapter = new AppointmentsRecycleAdapter(getContext(), rows);
            _rvAppointments.setAdapter(_adapter);
        });


        return view;
    }
}