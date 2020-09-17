package com.example.velveteyebrows.gson.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.beginObject();
        out.name("startTime");
        out.value(value.toString());
        out.endObject();
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        in.beginObject();
        String dateTime = in.nextString();
        return LocalDateTime.parse(dateTime);
    }
}
