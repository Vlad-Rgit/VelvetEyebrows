package com.example.velveteyebrows.comparators;

import com.velveteyebrows.dto.ServiceDTO;

import java.util.Comparator;

public class ServicePriceComparator implements Comparator<ServiceDTO> {

    @Override
    public int compare(ServiceDTO o1, ServiceDTO o2) {
        return Float.compare(o1.getFinalCost(),
                o2.getFinalCost());
    }
}
