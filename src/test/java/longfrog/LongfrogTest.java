package longfrog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LongfrogTest {
    private static final String TASK_FILE_NAME = "tasks.txt";

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoCommand_returnsConfirmationAndUpdatesList() {
        Longfrog longfrog = createLongfrog();

        String response = longfrog.getResponse("todo read book");

        assertEquals("Task secured on the lily pad: [T][ ] read book", response);
        assertEquals("Tasks currently on the lily pads:" + System.lineSeparator() + "1: [T][ ] read book",
                longfrog.getResponse("list"));
        assertFalse(longfrog.isExitRequested());
    }

    @Test
    void getResponse_invalidCommand_returnsParserErrorWithoutAddingTask() {
        Longfrog longfrog = createLongfrog();

        assertEquals("Unknown command token. My parser cannot compute that, ribbit.",
                longfrog.getResponse("dance"));
        assertEquals("Tasks currently on the lily pads:" + System.lineSeparator()
                + "The pond is clear—no tasks waiting.", longfrog.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_returnsFarewellAndRequestsExit() {
        Longfrog longfrog = createLongfrog();

        assertEquals("Pond secured. Rest well—ribbit.", longfrog.getResponse("bye"));
        assertTrue(longfrog.isExitRequested());

        longfrog.getResponse("list");

        assertFalse(longfrog.isExitRequested());
    }

    @Test
    void run_listThenBye_writesFramedConsoleResponses() {
        String input = "list" + System.lineSeparator() + "bye" + System.lineSeparator();
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Longfrog(temporaryDirectory.resolve(TASK_FILE_NAME).toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String separator = "─".repeat(50);
        String expectedOutput = String.join(System.lineSeparator(),
                separator,
                "Tasks currently on the lily pads:",
                "The pond is clear—no tasks waiting.",
                "",
                separator,
                "",
                separator,
                "Pond secured. Rest well—ribbit.",
                "",
                separator,
                "",
                "");
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void run_duplicateSavedTasks_displaysStartupWarningBeforeFirstResponse() throws IOException {
        Path saveFile = temporaryDirectory.resolve(TASK_FILE_NAME);
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | Read Book",
                "T | 1 | read book"));
        String input = "bye" + System.lineSeparator();
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Longfrog(saveFile.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String separator = "─".repeat(50);
        String expectedOutput = String.join(System.lineSeparator(),
                "Warning: 1 duplicate task entry detected in saved data. Existing entries were preserved.",
                separator,
                "Pond secured. Rest well—ribbit.",
                "",
                separator,
                "",
                "");
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void constructorAndGetResponse_storagePathIsDirectory_reportsLoadAndSaveErrors() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("not-a-file"));
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Longfrog longfrog;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            longfrog = new Longfrog(directory.toString());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("Warning: Save-file decoding failed. Booting with an empty task set."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
        assertEquals("Task secured on the lily pad: [T][ ] swim" + System.lineSeparator()
                + "I/O error: Task data could not be persisted to the pond archive.",
                longfrog.getResponse("todo swim"));
    }

    @Test
    void getResponse_duplicateTodo_rejectsTaskAndPreservesList() {
        Longfrog longfrog = createLongfrog();
        longfrog.getResponse("todo Read Book");

        assertEquals("Duplicate detected; task already exists at position 1: [T][ ] Read Book",
                longfrog.getResponse("todo read   book"));
        assertEquals("Tasks currently on the lily pads:" + System.lineSeparator() + "1: [T][ ] Read Book",
                longfrog.getResponse("list"));
    }

    @Test
    void getStartupWarning_duplicateSavedTasks_reportsCountWithoutChangingCommandResponse() throws IOException {
        Path saveFile = temporaryDirectory.resolve(TASK_FILE_NAME);
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | Read Book",
                "T | 1 | read   book",
                "T | 0 | READ BOOK"));

        Longfrog longfrog = new Longfrog(saveFile.toString());

        assertEquals("Warning: 2 duplicate task entries detected in saved data. Existing entries were preserved.",
                longfrog.getStartupWarning());
        assertEquals("Tasks currently on the lily pads:" + System.lineSeparator()
                + "1: [T][ ] Read Book" + System.lineSeparator()
                + "2: [T][X] read   book" + System.lineSeparator()
                + "3: [T][ ] READ BOOK", longfrog.getResponse("list"));
    }

    @Test
    void getStartupWarning_oneOrNoDuplicate_usesSingularOrNoWarning() throws IOException {
        Path saveFile = temporaryDirectory.resolve(TASK_FILE_NAME);
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | task",
                "T | 1 | TASK"));
        Longfrog longfrogWithDuplicate = new Longfrog(saveFile.toString());

        assertEquals("Warning: 1 duplicate task entry detected in saved data. Existing entries were preserved.",
                longfrogWithDuplicate.getStartupWarning());

        Files.writeString(saveFile, "T | 0 | unique");
        Longfrog longfrogWithoutDuplicate = new Longfrog(saveFile.toString());
        assertEquals("", longfrogWithoutDuplicate.getStartupWarning());
    }

    private Longfrog createLongfrog() {
        return new Longfrog(temporaryDirectory.resolve(TASK_FILE_NAME).toString());
    }
}
