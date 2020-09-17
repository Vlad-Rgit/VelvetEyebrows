package com.example.velveteyebrows.comparators;

import android.content.Context;
import com.example.velveteyebrows.database.DbAdapter;
import com.example.velveteyebrows.database.DbHelper;
import com.velveteyebrows.dto.ServiceDTO;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;

public class ServiceAlphComparator implements Comparator<ServiceDTO> {



    @Override
    public int compare(ServiceDTO o1, ServiceDTO o2) {

        if (o1 == null || o2 == null)
            return 0;
        else if(o1.getTitle() == null || o2.getTitle() == null)
            return 0;
        else
            return Integer.compare(o1.getId(), o2.getId());
    }


}
