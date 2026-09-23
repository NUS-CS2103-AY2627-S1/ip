package vermithor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** End-to-end tests for command processing: persistence across a simulated relaunch and parsing edge cases. */
class VermithorTest {
    @TempDir
    Path temporaryDirectory;

    private final Parser parser = new Parser();
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectOutput() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void addTasksSaveExitRelaunchListShowsTasks() throws VermithorException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/vermithor.txt"));
        List<Task> beforeExit = new ArrayList<>();
        Vermithor.processCommand(parser, "todo read a book", beforeExit);
        Vermithor.processCommand(parser, "deadline submit report /by 2026-09-20", beforeExit);
        Vermithor.processCommand(parser, "event trip /from Mon /to Tue", beforeExit);
        storage.save(beforeExit);

        // Simulate a relaunch: a fresh, unrelated in-memory list loaded straight from disk.
        List<Task> afterRelaunch = new ArrayList<>(storage.load());
        capturedOut.reset();
        Vermithor.processCommand(parser, "list", afterRelaunch);

        String listOutput = capturedOut.toString();
        assertEquals(3, afterRelaunch.size());
        assertTrue(listOutput.contains("read a book"));
        assertTrue(listOutput.contains("submit report"));
        assertTrue(listOutput.contains("trip"));
    }

    @Test
    void deadlineToleratesExtraWhitespaceAroundMarker() throws VermithorException {
        List<Task> tasks = new ArrayList<>();
        Vermithor.processCommand(parser, "deadline finish task  /by  2026-09-20", tasks);

        assertEquals(1, tasks.size());
        assertEquals("finish task", tasks.get(0).getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), ((Deadline) tasks.get(0)).getBy());
    }

    @Test
    void deadlineRejectsImpossibleDateWithSpecificMessage() {
        List<Task> tasks = new ArrayList<>();

        VermithorException exception = assertThrows(VermithorException.class,
                () -> Vermithor.processCommand(parser,
                        "deadline submit report /by 2026-09-31", tasks));

        assertEquals("The deadline date is not valid.", exception.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void deadlineReportsFormatErrorForWrongDateFormat() {
        List<Task> tasks = new ArrayList<>();

        VermithorException exception = assertThrows(VermithorException.class,
                () -> Vermithor.processCommand(parser,
                        "deadline submit report /by 31/09/2026", tasks));

        assertEquals("Use a deadline date in yyyy-MM-dd format.", exception.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void eventToleratesExtraWhitespaceAroundMarkers() throws VermithorException {
        List<Task> tasks = new ArrayList<>();
        Vermithor.processCommand(parser, "event  trip   /from  Mon  /to  Tue", tasks);

        assertEquals(1, tasks.size());
        Event event = (Event) tasks.get(0);
        assertEquals("trip", event.getDescription());
        assertEquals("Mon", event.getFrom());
        assertEquals("Tue", event.getTo());
    }

    @Test
    void eventRejectsImpossibleDateWithoutAddingTask() {
        List<Task> tasks = new ArrayList<>();

        VermithorException exception = assertThrows(VermithorException.class,
                () -> Vermithor.processCommand(parser,
                        "event meeting /from 2026-02-30 /to 2026-03-01", tasks));

        assertEquals("Use valid event dates in yyyy-MM-dd format.", exception.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void eventRejectsEndDateThatIsNotAfterStartDate() {
        List<Task> tasks = new ArrayList<>();

        assertThrows(VermithorException.class, () -> Vermithor.processCommand(parser,
                "event meeting /from 2026-03-02 /to 2026-03-01", tasks));

        assertTrue(tasks.isEmpty());
    }

    @Test
    void markAfterSortTargetsCurrentDisplayOrderNotOriginalInsertionOrder() throws VermithorException {
        List<Task> tasks = new ArrayList<>();
        Vermithor.processCommand(parser, "todo zebra task", tasks);
        Vermithor.processCommand(parser, "todo apple task", tasks);
        Vermithor.processCommand(parser, "sort", tasks);

        // After sorting, "apple task" is displayed first, so "mark 1" targets it, not the
        // originally-first "zebra task". This is the documented, intended behaviour.
        assertEquals("apple task", tasks.get(0).getDescription());
        Vermithor.processCommand(parser, "mark 1", tasks);
        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void invalidTaskNumbersAreRejectedWithoutCrashing() {
        List<Task> tasks = new ArrayList<>();
        assertThrows(VermithorException.class, () -> Vermithor.processCommand(parser, "mark 1", tasks));
        assertThrows(VermithorException.class, () -> Vermithor.processCommand(parser, "delete 0", tasks));
        assertThrows(VermithorException.class, () -> Vermithor.processCommand(parser, "unmark abc", tasks));
    }

    @Test
    void deleteOnEmptyListGivesAClearMessageInsteadOfARangeFromOneToZero() {
        List<Task> tasks = new ArrayList<>();

        VermithorException exception = assertThrows(VermithorException.class,
                () -> Vermithor.processCommand(parser, "delete 1", tasks));

        assertEquals("Your task list is empty, so there is no task to choose.", exception.getMessage());
    }
}
