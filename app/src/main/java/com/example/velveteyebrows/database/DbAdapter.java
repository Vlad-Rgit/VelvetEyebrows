package com.example.velveteyebrows.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.Service;
import com.velveteyebrows.dto.ServiceDTO;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class DbAdapter implements AutoCloseable {

    private final DbHelper _dbHelper;
    private final SQLiteDatabase _database;

    public DbAdapter(Context context){
        _dbHelper = new DbHelper(context.getApplicationContext());
        _database = _dbHelper.getWritableDatabase();
    }


    public void addService(ServiceDTO service){

        ContentValues values = new ContentValues();

        values.put("ServiceId", service.getId());
        values.put("Title", service.getTitle());
        values.put("Cost", service.getCost().floatValue());
        values.put("Duration", service.getDuration());
        values.put("Description", service.getDescription());
        values.put("Discount", service.getDiscount());

        _database.insert("Service", null, values);
    }

    public void addServices(Iterable<ServiceDTO> services) {
        for(ServiceDTO service: services) {
            addService(service);
        }
    }

    public void setImage(ServiceDTO service, byte[] image) {

        service.setImage(image);

        ContentValues values = new ContentValues();

        values.put("Image", image);

        _database.update("Service", values,
                "ServiceId = " + service.getId(),
                null);
    }

    public boolean isServiceExists(ServiceDTO service){

        String query = "Select * from Service" +
                " where ServiceId = " + service.getId();

        Cursor cursor = _database.rawQuery(query, null);

        return cursor.moveToFirst();
    }

    public boolean isServiceFav(ServiceDTO service) {

        String query = "Select * from Service" +
                " where ServiceId = " + service.getId();

        Cursor cursor = _database.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            service.setFavourited(false);
            return service.getFavourited();
        }

        service.setFavourited(cursor.getInt(2) == 1);
        return service.getFavourited();
    }

    public void setServiceFav(ServiceDTO service, boolean isFav){

        service.setFavourited(isFav);

        int fav = isFav ? 1 : 0;

        ContentValues values = new ContentValues();

        values.put("IsFav", fav);

        _database.update("Service", values,
                "ServiceId = " + service.getId(), null);
    }

    public List<ServiceDTO> getServices() {

        Cursor cursor = _database.rawQuery("Select * from Service",
                null, null);

        LinkedList<ServiceDTO> serviceDTOS = new LinkedList<>();

        while(cursor.moveToNext()){
            serviceDTOS.add(createService(cursor));
        }

        return serviceDTOS;
    }

    public ServiceDTO createService(Cursor cursor) {

        ServiceDTO serviceDTO = new ServiceDTO();

        serviceDTO.setId(cursor.getInt(0));
        serviceDTO.setTitle(cursor.getString(1));
        serviceDTO.setCost(new BigDecimal(cursor.getFloat(2)));
        serviceDTO.setDuration(cursor.getInt(3));
        serviceDTO.setDescription(cursor.getString(4));
        serviceDTO.setDiscount(cursor.getFloat(5));
        serviceDTO.setImage(cursor.getBlob(6));
        serviceDTO.setFavourited(cursor.getInt(7) == 1);

        return serviceDTO;
    }


    @Override
    public void close()  {
        _database.close();
        _dbHelper.close();
    }
}
