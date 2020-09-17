package com.velveteyebrows.dto;

import android.app.Service;

import java.io.Serializable;

public class AppointmentDTO implements Serializable {
    private ClientServiceDTO clientServiceDTO;
    private ServiceDTO serviceDTO;

    public ClientServiceDTO getClientServiceDTO() {
        return clientServiceDTO;
    }

    public void setClientServiceDTO(ClientServiceDTO clientServiceDTO) {
        this.clientServiceDTO = clientServiceDTO;
    }

    public ServiceDTO getServiceDTO() {
        return serviceDTO;
    }

    public void setServiceDTO(ServiceDTO serviceDTO) {
        this.serviceDTO = serviceDTO;
    }
}
