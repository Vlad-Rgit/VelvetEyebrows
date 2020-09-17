package com.example.velveteyebrows.callbacks;

import com.velveteyebrows.dto.ClientDTO;

@FunctionalInterface
public interface OnGetClientCompletedCallback {
    void onGetClientCompleted(ClientDTO clientDTO);
}
