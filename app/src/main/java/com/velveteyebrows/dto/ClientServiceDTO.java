package com.velveteyebrows.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClientServiceDTO implements Serializable {

    private int clientId;
    private int serviceId;
    private LocalDateTime startTime;
    private String comment;
    private ServiceDTO serviceDTO;
    private ClientDTO clientDTO;
    private int isNotificate;

    public int isNotificate() {
        return isNotificate;
    }

    public void setNotificate(int notificate) {
        isNotificate = notificate;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ServiceDTO getServiceDTO() {
        return serviceDTO;
    }

    public void setServiceDTO(ServiceDTO serviceDTO) {
        this.serviceDTO = serviceDTO;
    }

    public ClientDTO getClientDTO() {
        return clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }
}
