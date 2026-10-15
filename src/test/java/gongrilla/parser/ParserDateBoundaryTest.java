package gongrilla.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.storage.Storage;
import gongrilla.task.Deadline;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/** Checks calendar boundaries and equivalent date formats through the parser's public API. */
class ParserDateBoundaryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void parse_invalidCalendarDatesAndTimes_rejectsRatherThanNormalizing() {
        for (String date : List.of("29/2/2023", "31/4/2026", "0/1/2026", "1/13/2026",
                "2026-02-30", "1/1/2026 2400", "1/1/2026 1260", "1/1/2026 12:00",
                "1/1/2026 1200 trailing")) {
            assertThrows(DateTimeParseException.class, () -> Parser.parse("deadline task /by " + date), date);
        }
    }

    @Test
    void parse_supportedDateFormats_preservesLeapDayAndMidnight() throws Exception {
        List<String> dates = List.of("29/2/2024", "2024-02-29", "29/2/2024 0000", "2024-02-29 0000");
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(new ByteArrayOutputStream()));
        for (String date : dates) {
            Parser.parse("deadline task " + tasks.size() + " /by " + date).execute(tasks, ui, storage);
            Deadline task = (Deadline) tasks.get(tasks.size() - 1);
            assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), task.getBy(), date);
        }
        assertEquals(dates.size(), storage.load().size());
    }

    @Test
    void parse_lastMinuteOfDay_preservesTime() throws Exception {
        TaskList tasks = new TaskList();
        Parser.parse("deadline task /by 31/12/2026 2359").execute(tasks,
                new Ui(InputStream.nullInputStream(), new PrintStream(new ByteArrayOutputStream())),
                new Storage(temporaryDirectory.resolve("tasks.txt")));
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59), ((Deadline) tasks.get(0)).getBy());
    }
}
