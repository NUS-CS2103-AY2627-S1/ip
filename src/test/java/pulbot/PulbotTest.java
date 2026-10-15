package pulbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pulbot.storage.Storage;
import pulbot.task.TaskList;

/** Tests date conversion and complete command sessions with isolated storage. */
public class PulbotTest {
    @TempDir
    private Path tempDir;

    @Test
    public void parseDateTime_validInput_returnsDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, Pulbot.parseDateTime("2/12/2019 1800"));
        assertEquals(expected, Pulbot.parseDateTime(" 2/12/2019 1800 "));
    }

    @Test
    public void parseDateTime_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Pulbot.parseDateTime("2019-12-02 18:00"));
        assertThrows(IllegalArgumentException.class, () ->
                Pulbot.parseDateTime("31/2/2019 1800"));
    }

    @Test
    public void formatDateTime_validDateTime_returnsDisplayFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("Dec 02 2019 6:00 PM", Pulbot.formatDateTime(dateTime));
        assertEquals("Jan 01 2020 12:05 AM",
                Pulbot.formatDateTime(LocalDateTime.of(2020, 1, 1, 0, 5)));
    }

    @Test
    public void main_byeCommand_startsAndExitsApplication() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Pulbot.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String displayed = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayed.contains("Hello! I'm Pulbot."));
        assertTrue(displayed.contains("Bye."));
    }

    @Test
    public void constructor_storageLoadFailure_retainsStartupWarning() {
        Pulbot pulbot = new Pulbot(new FailingStorage());

        assertTrue(pulbot.getStartupWarning().contains("Unable to load saved tasks"));
        assertTrue(pulbot.getStartupWarning().contains("test failure"));
    }

    @Test
    public void getResponse_commandSequence_updatesAndPersistsTasks() throws PulbotException {
        Path dataFile = tempDir.resolve("data/pulbot.txt");
        Storage storage = new Storage(dataFile.toString());
        Pulbot pulbot = new Pulbot(storage);

        String addResponse = pulbot.getResponse("todo read book");
        String duplicateResponse = pulbot.getResponse("todo READ BOOK");
        String markResponse = pulbot.getResponse("mark 1");
        String listResponse = pulbot.getResponse("list");

        assertTrue(addResponse.contains("I have added this task"));
        assertTrue(duplicateResponse.contains("This task already exists"));
        assertTrue(markResponse.contains("marked this task as done"));
        assertTrue(listResponse.contains("1.[T][✓] read book"));
        assertTrue(storage.load().get(0).isDone());
    }

    @Test
    public void getResponse_invalidCommands_doNotCreateTasks() throws PulbotException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Pulbot pulbot = new Pulbot(storage);

        assertTrue(pulbot.getResponse("todo").contains("Please include a description"));
        assertTrue(pulbot.getResponse("mark 1").contains("There is no task with that number"));
        assertTrue(pulbot.getResponse("event class /from 2/12/2019 1600 /to 2/12/2019 1400")
                .contains("end time must be after"));
        assertTrue(storage.load().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo read\tbook", "todo read\nbook", "todo read\rbook",
        "deadline read\tbook /by 18/9/2026 1200",
        "event read\tbook /from 18/9/2026 1200 /to 18/9/2026 1300"})
    public void getResponse_storageDelimiters_rejectsWithoutCorruptingData(String input)
            throws PulbotException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Pulbot pulbot = new Pulbot(storage);
        pulbot.getResponse("todo original");

        assertTrue(pulbot.getResponse(input).contains("ERROR"));
        assertEquals(1, storage.load().size());
        assertEquals("original", storage.load().get(0).getDescription());
    }

    @Test
    public void getResponse_damagedStorage_preservesOriginalFile() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String original = "T\t0\tvaluable task\ninvalid record\n";
        Files.writeString(file, original);
        Pulbot pulbot = new Pulbot(new Storage(file.toString()));

        assertTrue(pulbot.getStartupWarning().contains("Unable to load saved tasks"));
        String response = pulbot.getResponse("todo replacement");

        assertTrue(response.contains("original file has been preserved"));
        assertFalse(response.contains("I have added"));
        assertEquals(original, Files.readString(file));
        assertTrue(pulbot.getResponse("list").contains("empty"));
    }

    /** Simulates a storage failure without depending on the host file system. */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super("unused");
        }

        @Override
        public TaskList load() throws PulbotException {
            throw new PulbotException("test failure");
        }
    }
}
