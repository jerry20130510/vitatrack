package web.blog.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UtcTimestampAdapter extends TypeAdapter<Timestamp> {
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter;
    });

    @Override
    public void write(JsonWriter out, Timestamp value) throws IOException {
        out.value(value == null ? null : sdf.get().format(value));
    }

    @Override
    public Timestamp read(JsonReader in) throws IOException {
        String s = in.nextString();
        if (s == null) {
            return null;
        }
        try {
            long millis = sdf.get().parse(s).getTime();
            return new Timestamp(millis);
        } catch (Exception e) {
            throw new IOException("Invalid timestamp format: " + s, e);
        }
    }
}
