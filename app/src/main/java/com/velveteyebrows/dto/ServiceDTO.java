package com.velveteyebrows.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class ServiceDTO implements Serializable {

    @SerializedName("ID")
    private int id;
    @SerializedName("Title")
    private String title;
    @SerializedName("Cost")
    private BigDecimal cost;
    @SerializedName("Duration")
    private int duration;
    @SerializedName("Description")
    private String description;
    @SerializedName("Discount")
    private float discount;

    private transient byte[] image = null;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    private boolean favourited;

    public boolean getFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getFinalCost() {
        if(discount == 0){
            return cost.floatValue();
        }
        else {
            return cost.floatValue() - cost.floatValue() * discount;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ServiceDTO) {
            ServiceDTO service = (ServiceDTO)obj;
            return this.id == service.id;
        }
        else {
            return false;
        }
    }
}
