package com.example.cardiohealth.DTO;
import android.os.Build;
import androidx.annotation.RequiresApi;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocalDateTimeConverter implements Converter<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public LocalDateTime read(InputNode node) throws Exception {
        String value = node.getValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.parse(value, FORMATTER);
        }
        return null;
    }

    @Override
    public void write(OutputNode node, LocalDateTime value) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            node.setValue(value.format(FORMATTER));
        }
    }
}
