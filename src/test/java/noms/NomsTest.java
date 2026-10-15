package noms;

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
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.storage.Storage;
import noms.task.ToDo;

/**
 * Tests {@link Noms#getResponse}, the integration seam the GUI relies on. It
 * must run the same parse-execute pipeline as the console loop, return clean
 * text with no console dividers, and report when the user asked to exit.
 * Standard output is redirected during each test so the captured prints do not
 * clutter the test log.
 */
public class NomsTest {
    @TempDir
    private Path tempDir;

    private final InputStream realIn = System.in;
    private final PrintStream realOut = System.out;
    private Noms noms;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        noms = new Noms(tempDir.toString(), "noms.txt");
    }

    @AfterEach
    public void tearDown() {
        System.setIn(realIn);
        System.setOut(realOut);
    }

    @Test
    public void getResponse_addTodo_confirmsAndStripsDividers() {
        NomsResponse response = noms.getResponse("todo read book");

        assertTrue(response.text().contains("read book"), "response should mention the added task");
        assertFalse(response.text().contains("____"), "GUI response should not contain console dividers");
        assertEquals(ResponseType.NORMAL, response.type());
        assertFalse(response.isError());
    }

    @Test
    public void getResponse_unknownCommand_returnsNonBlankErrorMessage() {
        NomsResponse response = noms.getResponse("blahblah");

        assertFalse(response.text().isBlank(), "an unknown command should still produce a reply");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.isError());
    }

    @Test
    public void getResponse_validCommandAfterError_resetsResponseType() {
        noms.getResponse("blahblah");

        NomsResponse response = noms.getResponse("list");

        assertEquals(ResponseType.NORMAL, response.type());
    }

    @Test
    public void getResponse_storageFailure_returnsErrorResponse() throws IOException {
        Path blockedDirectory = tempDir.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "not a directory");
        Noms nomsWithBlockedStorage = new Noms(blockedDirectory.toString(), "noms.txt");

        NomsResponse response = nomsWithBlockedStorage.getResponse("todo read book");

        assertEquals(ResponseType.ERROR, response.type());
        assertFalse(response.text().contains("gobbled up"));
        assertTrue(nomsWithBlockedStorage.getResponse("list").text().contains("menu is empty"));
    }

    @Test
    public void getResponse_byeCommand_marksExitRequested() {
        assertFalse(noms.isExitRequested(), "exit should not be requested before any command");

        noms.getResponse("bye");

        assertTrue(noms.isExitRequested(), "bye should mark the app as ready to exit");
    }

    @Test
    public void getResponse_nonExitCommand_doesNotRequestExit() {
        noms.getResponse("list");

        assertFalse(noms.isExitRequested(), "a non-exit command should leave the app running");
    }

    @Test
    public void constructor_savedTasksExist_loadsTasks() throws IOException {
        Storage storage = new Storage(tempDir.toString(), "saved.txt");
        storage.save(List.of(new ToDo("read saved book")));

        Noms loadedNoms = new Noms(tempDir.toString(), "saved.txt");

        assertTrue(loadedNoms.getResponse("list").text().contains("read saved book"));
    }

    @Test
    public void constructor_spoiledSaveLine_reportsWarningAndLoadsValidTasks() throws IOException {
        Files.writeString(tempDir.resolve("spoiled.txt"),
                "GARBAGE LINE\nT | 0 | valid task\n");
        ByteArrayOutputStream output = captureOutput();

        Noms loadedNoms = new Noms(tempDir.toString(), "spoiled.txt");

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("spoiled entry"));
        assertTrue(loadedNoms.getResponse("list").text().contains("valid task"));
    }

    @Test
    public void constructor_savePathIsDirectory_reportsLoadErrorAndStartsEmpty() throws IOException {
        Files.createDirectory(tempDir.resolve("directory.txt"));
        ByteArrayOutputStream output = captureOutput();

        Noms loadedNoms = new Noms(tempDir.toString(), "directory.txt");

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("couldn't load the saved menu"));
        assertTrue(loadedNoms.getResponse("list").text().contains("menu is empty"));
    }

    @Test
    public void run_validCommands_processesUntilBye() throws IOException {
        ByteArrayOutputStream output = prepareConsoleInput("todo read book\nbye\ntodo ignored\n");
        Noms consoleNoms = new Noms(tempDir.toString(), "console.txt");

        consoleNoms.run();

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("What's on the menu today?"));
        assertTrue(printed.contains("gobbled up your new task"));
        assertTrue(printed.contains("All done! Noms is full for now"));
        assertFalse(printed.contains("ignored"));
        assertEquals(1, new Storage(tempDir.toString(), "console.txt").load().size());
    }

    @Test
    public void run_invalidCommand_reportsErrorAndContinues() throws IOException {
        ByteArrayOutputStream output = prepareConsoleInput("blah\ntodo recovered task\nbye\n");
        Noms consoleNoms = new Noms(tempDir.toString(), "console.txt");

        consoleNoms.run();

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("couldn't understand that command"));
        assertTrue(printed.contains("recovered task"));
        assertEquals(1, new Storage(tempDir.toString(), "console.txt").load().size());
    }

    @Test
    public void run_endOfInputWithoutBye_closesNormally() {
        ByteArrayOutputStream output = prepareConsoleInput("list\n");
        Noms consoleNoms = new Noms(tempDir.toString(), "console.txt");

        consoleNoms.run();

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("menu is empty"));
        assertFalse(printed.contains("All done! Noms is full for now"));
    }

    @Test
    public void getGreeting_returnsExpectedGreeting() {
        assertEquals("Hi! I'm Noms, your hungry little task monster. What's on the menu today?",
                noms.getGreeting());
    }

    private ByteArrayOutputStream captureOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        return output;
    }

    private ByteArrayOutputStream prepareConsoleInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return captureOutput();
    }
}
