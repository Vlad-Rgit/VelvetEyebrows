package com.example.velveteyebrows.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.velveteyebrows.R;
import com.example.velveteyebrows.databinding.FragmentProfileBinding;
import com.velveteyebrows.dto.ClientDTO;

public class ProfileFragment extends Fragment {

    public static final String CLIENT_ARG = "ClientArg";

    private ClientDTO _clientDTO;
    private FragmentProfileBinding _binding;

    public ProfileFragment(){

    }

    public static ProfileFragment newInstance(ClientDTO clientDTO){

        ProfileFragment fragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putSerializable(CLIENT_ARG, clientDTO);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if(args != null){
            _clientDTO = (ClientDTO)
                    args.getSerializable(CLIENT_ARG);
        }
        else {
            _clientDTO = new ClientDTO();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _binding = FragmentProfileBinding.inflate(inflater, container, false);

        _binding.setClient(_clientDTO);

        return _binding.getRoot();
    }
}
