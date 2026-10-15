package squirtlebot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests supported and unsupported date and date-time formats.
 */
public class DateParserTest {
    @Test
    public void parseTemporal_correctDateFormat_optionalDateReturned() {
        DateParser parser = new DateParser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse("01-01-1970", formatter);
        assertEquals(Optional.of(localDate), parser.parseTemporal("01-01-1970"));
    }

    @Test
    public void parseTemporal_wrongDateFormat_optionalEmptyReturned() {
        DateParser parser = new DateParser();
        assertEquals(Optional.empty(), parser.parseTemporal("12 12 1970"));
    }

    @Test
    public void parseTemporal_correctDateTimeFormat_optionalDateTimeReturned() {
        DateParser parser = new DateParser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDate = LocalDateTime.parse("26-08-2026 23:59:01", formatter);
        assertEquals(Optional.of(localDate), parser.parseTemporal("26-08-2026 23:59:01"));

    }

    @Test
    public void parseTemporal_wrongDateTimeFormat_optionalEmptyReturned() {
        DateParser parser = new DateParser();
        assertEquals(Optional.empty(), parser.parseTemporal("01-01-1970-23:59"));
    }
}
