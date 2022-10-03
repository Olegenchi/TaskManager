package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(dateTimeFormatter));
        } else {
            jsonWriter.value("");
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String localDateTime = jsonReader.nextString();
        if (!localDateTime.isBlank()) {
            return LocalDateTime.parse(localDateTime, dateTimeFormatter);
        } else {
            return null;
        }
    }
}
