package gongrilla.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.storage.Storage;

/**
 * Tests typed dates, display formatting, and date persistence.
 */
class DateTaskTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void deadline_validDateTime_storesTypedValueAndFormatsDisplay() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", dateTime);

        assertEquals(dateTime, deadline.getBy());
        assertEquals("[D][ ] return book (by: 2 Dec 2019, 6:00PM)", deadline.toString());
    }

    @Test
    void event_validRange_storesTypedDatesAndFormatsDisplay() {
        Event event = new Event("conference", LocalDateTime.of(2019, 12, 2, 9, 30),
                LocalDateTime.of(2019, 12, 2, 17, 0));

        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 30), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 17, 0), event.getTo());
        assertEquals("[E][ ] conference (from: 2 Dec 2019, 9:30AM "
                        + "to: 2 Dec 2019, 5:00PM)",
                event.toString());
    }

    @Test
    void event_sameStartAndEndDateTime_isRejected() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 2, 29, 12, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("instant event", dateTime, dateTime));
    }

    @Test
    void event_endBeforeStart_isRejected() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Event("conference", LocalDateTime.of(2019, 12, 5, 18, 0),
                        LocalDateTime.of(2019, 12, 5, 9, 0)));

        assertTrue(exception.getMessage().contains("cannot be after"));
    }

    @Test
    void dateTasks_nullDates_areRejectedImmediately() {
        assertThrows(NullPointerException.class, () -> new Deadline("return book", null));
        assertThrows(NullPointerException.class, () ->
                new Event("event", null, LocalDateTime.now()));
        assertThrows(NullPointerException.class, () ->
                new Event("event", LocalDateTime.now(), null));
    }

    @Test
    void storage_journalRoundTrip_preservesDatesAndCompletion() throws Exception {
        Storage storage = storageFor("journal.txt");
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("conference", LocalDateTime.of(2019, 12, 3, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0));
        storage.appendAdd(deadline);
        storage.appendAdd(event);
        storage.appendMark(0);

        ArrayList<Task> tasks = storage.load();

        assertEquals("[D][X] return book (by: 2 Dec 2019, 6:00PM)",
                tasks.get(0).toString());
        assertEquals("[E][ ] conference (from: 3 Dec 2019, 9:00AM "
                        + "to: 3 Dec 2019, 5:00PM)",
                tasks.get(1).toString());
    }

    @Test
    void storage_specialCharactersAndDates_roundTrip() throws Exception {
        Storage storage = storageFor("encoded.txt");
        Deadline deadline = new Deadline("read | review % notes\nnext line",
                LocalDateTime.of(2020, 2, 29, 23, 59));

        storage.appendAdd(deadline);
        Deadline loadedDeadline = (Deadline) storage.load().getFirst();

        assertEquals(deadline.getName(), loadedDeadline.getName());
        assertEquals(deadline.getBy(), loadedDeadline.getBy());
    }

    @Test
    void storage_isoLegacySnapshot_loadsSuccessfully() throws Exception {
        Path dataFile = temporaryDirectory.resolve("legacy-iso.txt");
        Files.write(dataFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02",
                "E | 0 | conference | 2019-12-03 | 2019-12-05"));

        ArrayList<Task> tasks = new Storage(dataFile).load();

        assertEquals(3, tasks.size());
        assertEquals("[D][ ] return book (by: 2 Dec 2019, 12:00AM)",
                tasks.get(1).toString());
    }

    @Test
    void storage_informalLegacyDate_reportsMigrationGuidance() throws Exception {
        Path dataFile = temporaryDirectory.resolve("legacy-informal.txt");
        Files.writeString(dataFile, "D | 0 | return book | June 6th");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("must be changed to an ISO date or date-time"));
    }

    @Test
    void storage_reversedEventDate_reportsCorruptRecord() throws Exception {
        Path dataFile = temporaryDirectory.resolve("reversed-event.txt");
        Files.writeString(dataFile, "E2 | 0 | conference | 2019-12-05 | 2019-12-03");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("cannot be after"));
    }

    @Test
    void storage_missingFile_returnsEmptyList() throws Exception {
        assertTrue(storageFor("missing.txt").load().isEmpty());
    }

    private Storage storageFor(String fileName) {
        return new Storage(temporaryDirectory.resolve("data").resolve(fileName));
    }
}
