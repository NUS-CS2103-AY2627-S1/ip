package echo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.storage.Storage;
import echo.ui.Ui;

/**
 * Tests the response generation, CLI execution, and state tracking of {@link Echo}.
 */
public class EchoTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getWelcomeMessage_returnsNonEmptyGreeting() {
        Echo echo = createEcho();

        assertTrue(echo.getWelcomeMessage().contains("Online and listening"));
    }

    @Test
    public void getWelcomeMessage_withCorruptedStorage_includesCorruptedStorageNotice() throws IOException {
        Path filePath = temporaryDirectory.resolve("corrupted_welcome.txt");
        Files.write(filePath, List.of("CORRUPT | ENTRY"), StandardCharsets.UTF_8);

        Echo echo = new Echo(new Ui(new Scanner("")), new Storage(filePath));

        assertTrue(echo.getWelcomeMessage().contains("[STORAGE NOTICE]"));
        assertTrue(echo.getWelcomeMessage().contains("corrupted entries"));
    }

    @Test
    public void getWelcomeMessage_withStorageLoadingFault_handlesFaultNotice() throws IOException {
        Path dirPath = temporaryDirectory.resolve("directory_as_file");
        Files.createDirectories(dirPath);

        Echo echo = new Echo(new Ui(new Scanner("")), new Storage(dirPath));

        assertTrue(echo.getWelcomeMessage().contains("[STORAGE FAULT]"));
    }

    @Test
    public void constructors_initializeCorrectly() {
        Path filePath = temporaryDirectory.resolve("ctor_test.txt");
        Echo echo1 = new Echo(filePath.toString());
        assertNotNull(echo1.getWelcomeMessage());

        Echo echo2 = new Echo(new Storage(filePath));
        assertNotNull(echo2.getWelcomeMessage());
    }

    @Test
    public void getResponse_validTodoCommand_returnsAddedMessage() {
        Echo echo = createEcho();

        String response = echo.getResponse("todo read book");

        assertTrue(response.contains("[DIRECTIVE LOGGED]"));
        assertTrue(response.contains("read book"));
        assertFalse(echo.isExit());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Echo echo = createEcho();

        String response = echo.getResponse("invalidcommand");

        assertTrue(response.contains("do not recognise 'invalidcommand'"));
        assertFalse(echo.isExit());
    }

    @Test
    public void getResponse_exitCommand_setsExitFlag() {
        Echo echo = createEcho();

        String response = echo.getResponse("bye");

        assertFalse(response.isBlank());
        assertTrue(echo.isExit());
    }

    @Test
    public void run_executesCommandsAndTerminatesOnBye() {
        String script = "   \ntodo finish assignment\ninvalid\nbye\n";
        Ui ui = new Ui(new Scanner(script));
        Echo echo = new Echo(ui, new Storage(temporaryDirectory.resolve("run_script.txt")));

        echo.run();

        assertTrue(echo.isExit());
    }

    @Test
    public void run_terminatesCleanlyOnEof() {
        Ui ui = new Ui(new Scanner(""));
        Echo echo = new Echo(ui, new Storage(temporaryDirectory.resolve("run_eof.txt")));

        echo.run();

        assertFalse(echo.isExit());
    }

    private Echo createEcho() {
        return new Echo(new Ui(new Scanner("")), new Storage(temporaryDirectory.resolve("echo_test.txt")));
    }
}
