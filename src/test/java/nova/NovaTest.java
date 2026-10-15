package nova;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.parser.Parser;
import nova.storage.Storage;
import nova.ui.Ui;

class NovaTest {
    private static final String LOAD_ERROR = " Oops! I could not load your tasks from the data file.";
    private static final String COMMAND_UNAVAILABLE = " Oops! I could not load your tasks. "
            + "Please fix the data file and restart Nova. Only help and bye are available this session.";

    @TempDir
    private Path temporaryDirectory;

    private Nova nova;

    @BeforeEach
    void setUp() {
        Storage storage = new Storage(temporaryDirectory.resolve("nova.txt"));
        Ui unusedConsoleUi = new Ui(InputStream.nullInputStream(),
                new PrintStream(OutputStream.nullOutputStream()));
        nova = new Nova(storage, unusedConsoleUi, new Parser());
    }

    @Test
    void startGui_newSession_returnsGreeting() {
        assertEquals("Hello! I'm Nova.\nLet's take it one task at a time.", normalizeLineEndings(nova.startGui()));
    }

    @Test
    void getResponse_addThenList_usesSharedTaskListAndStorage() {
        String addResponse = normalizeLineEndings(nova.getResponse("todo read book"));
        String listResponse = normalizeLineEndings(nova.getResponse("list"));

        assertEquals(" Got it! Added to your list:\n  [T][ ] read book\n"
                + " Now you have 1 tasks in the list.", addResponse);
        assertEquals(" Here are the tasks in your list:\n 1.[T][ ] read book", listResponse);
        assertTrue(temporaryDirectory.resolve("nova.txt").toFile().isFile());
    }

    @Test
    void getResponse_invalidThenBye_reportsErrorAndEndsSessionOnlyAfterBye() {
        String errorResponse = nova.getResponse("unknown");

        assertTrue(errorResponse.contains("I don't know what that means"));
        assertFalse(nova.isExitRequested());

        assertEquals(" See you soon. Take care!", nova.getResponse("bye"));
        assertTrue(nova.isExitRequested());
    }

    @Test
    void getResponse_help_returnsExactHelpWithoutSavingOrExiting() {
        String expectedHelp = " Here are Nova's commands:\n"
                + " list\n"
                + " todo DESCRIPTION\n"
                + " deadline DESCRIPTION /by yyyy-MM-dd\n"
                + " event DESCRIPTION /from START /to END\n"
                + " mark TASK_NUMBER\n"
                + " unmark TASK_NUMBER\n"
                + " delete TASK_NUMBER\n"
                + " find KEYWORD\n"
                + " on yyyy-MM-dd\n"
                + " help\n"
                + " bye";

        assertEquals(expectedHelp, normalizeLineEndings(nova.getResponse("help")));
        assertFalse(nova.isExitRequested());
        assertFalse(temporaryDirectory.resolve("nova.txt").toFile().exists());
    }

    @Test
    void getResponse_loadFails_allowsOnlyHelpAndByeAndPreservesFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        byte[] originalBytes = writeUnreadableTaskFile(dataFile);
        assertEquals("Hello! I'm Nova.\nLet's take it one task at a time.\n" + LOAD_ERROR,
                normalizeLineEndings(nova.startGui()));

        String[] commands = {
            "todo new task", "deadline report /by 2026-09-30", "event meeting /from 2pm /to 4pm",
            "mark 1", "unmark 1", "delete 1", "todo another task", "list", "find task", "on 2026-09-30"
        };
        for (String command : commands) {
            assertEquals(COMMAND_UNAVAILABLE, nova.getResponse(command));
            assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
            assertFalse(nova.isExitRequested());
        }

        assertTrue(nova.getResponse("help").startsWith(" Here are Nova's commands:"));
        assertFalse(nova.isExitRequested());
        assertEquals(" See you soon. Take care!", nova.getResponse("bye"));
        assertTrue(nova.isExitRequested());
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    @Test
    void run_loadFails_reportsBlockedChangeAndContinuesToBye() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        byte[] originalBytes = writeUnreadableTaskFile(dataFile);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui consoleUi = new Ui(new ByteArrayInputStream("todo new task\nlist\nfind task\non 2026-09-30\nhelp\nbye\n"
                .getBytes(StandardCharsets.UTF_8)), new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        Nova consoleNova = new Nova(new Storage(dataFile), consoleUi, new Parser());

        consoleNova.run();

        String output = normalizeLineEndings(outputBytes.toString(StandardCharsets.UTF_8));
        assertTrue(output.contains(LOAD_ERROR + "\n"));
        assertEquals(4, output.lines().filter(COMMAND_UNAVAILABLE::equals).count());
        assertTrue(output.indexOf(" Here are Nova's commands:") > output.indexOf(COMMAND_UNAVAILABLE));
        assertFalse(output.contains(" Here are the tasks in your list:"));
        assertFalse(output.contains(" Here are the matching tasks in your list:"));
        assertFalse(output.contains(" Here are the deadlines on"));
        assertTrue(output.contains(" See you soon. Take care!\n"));
        assertFalse(output.contains("Added to your list"));
        assertTrue(consoleNova.isExitRequested());
        assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
    }

    private byte[] writeUnreadableTaskFile(Path dataFile) throws IOException {
        byte[] originalBytes = "T | 0 | important existing task\n?\n".getBytes(StandardCharsets.UTF_8);
        originalBytes[originalBytes.length - 2] = (byte) 0xff; // Invalid UTF-8 forces a real load failure.
        Files.write(dataFile, originalBytes);
        return originalBytes;
    }

    private String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n").replace('\r', '\n');
    }
}
