package anniechat.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import anniechat.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command execution independently of the CLI and JavaFX interfaces. */
public class CommandHandlerTest {
    /** Temporary directory provided by JUnit for each test. */
    @TempDir
    Path temporaryDirectory;

    @Test
    public void handle_addCommand_addsAndSavesTask() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        CommandResult result = handler.handle("todo read book");

        assertTrue(result.getMessage().contains("I've added this task"));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(filePath));
        assertFalse(result.isExitRequested());
    }

    @Test
    public void handle_unknownCommand_returnsSharedResponse() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        CommandResult result = handler.handle("nonsense");

        assertTrue(result.getMessage().contains("Meow..."));
        assertTrue(result.getMessage().contains("Try list"));
        assertFalse(result.isExitRequested());
    }

    @Test
    public void handle_byeCommand_requestsExit() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        CommandResult result = handler.handle("bye");

        assertEquals("Meow for now! See you later!", result.getMessage());
        assertTrue(result.isExitRequested());
    }

    @Test
    public void handle_markWithoutTaskNumber_returnsInvalidCommandResponse() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        CommandResult result = handler.handle("mark");

        assertTrue(result.getMessage().contains("couldn't understand"));
        assertFalse(result.isExitRequested());
    }

    @Test
    public void handle_findWithoutKeyword_returnsInvalidCommandResponse() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        CommandResult result = handler.handle("find");

        assertTrue(result.getMessage().contains("couldn't understand"));
        assertFalse(result.isExitRequested());
    }

    @Test
    public void constructor_malformedSavedData_reportsLoadError() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D | 0 | missing date\n");

        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        assertTrue(handler.getStartupMessage().contains("couldn't load"));
    }

    @Test
    public void handle_taggedTask_canBeFoundByTag() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler handler = new CommandHandler(new Storage(filePath.toString()));

        handler.handle("todo read book #school");
        CommandResult result = handler.handle("find #school");

        assertTrue(result.getMessage().contains("read book #school"));
    }

    @Test
    public void handle_taggedTask_preservesTagWhenReloaded() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        CommandHandler firstHandler = new CommandHandler(new Storage(filePath.toString()));
        firstHandler.handle("todo submit report #school");

        CommandHandler reloadedHandler = new CommandHandler(new Storage(filePath.toString()));
        CommandResult result = reloadedHandler.handle("list");

        assertTrue(result.getMessage().contains("submit report #school"));
    }
}
