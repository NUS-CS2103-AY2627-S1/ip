package luckynoslacky.luckystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests CSV-specific timestamp encoding and decoding. */
class DateTimeStorageCodecTest {
    /** Verifies that a date and time round-trips through CSV text. */
    @Test
    void formatAndParse_validDateTime_returnsOriginalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2030, 10, 15, 14, 15);

        String stored = DateTimeStorageCodec.format(dateTime);

        assertEquals("2030-10-15 14:15", stored);
        assertEquals(dateTime, DateTimeStorageCodec.parse(stored));
        assertEquals("", DateTimeStorageCodec.format(null));
        assertEquals(null, DateTimeStorageCodec.parse(""));
    }
}
