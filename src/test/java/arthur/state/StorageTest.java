package arthur.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import arthur.exceptions.StorageException;
import arthur.tasks.TodoTask;

// AI-assisted: Tests storage with injected temporary paths instead of application data.
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyContext() {
        CommandContext context = new Storage(temporaryDirectory.resolve("arthur.bin")).load();

        assertTrue(context.tasks().isEmpty());
        assertTrue(context.commandAliases().isEmpty());
    }

    @Test
    public void save_thenLoad_persistsTasksAndAliases() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        context.commandAliases().put("t", "todo");
        Storage storage = new Storage(temporaryDirectory.resolve("nested").resolve("arthur.bin"));

        storage.save(context);
        CommandContext loadedContext = storage.load();

        assertEquals("todo", loadedContext.commandAliases().get("t"));
        assertInstanceOf(TodoTask.class, loadedContext.tasks().getFirst());
        assertEquals("[T][ ] read book", loadedContext.tasks().getFirst().toString());
    }

    @Test
    public void load_corruptFile_returnsEmptyContext() throws IOException {
        Path storageFile = temporaryDirectory.resolve("arthur.bin");
        Files.writeString(storageFile, "not serialized data");

        CommandContext context = new Storage(storageFile).load();

        assertTrue(context.tasks().isEmpty());
        assertTrue(context.commandAliases().isEmpty());
    }

    @Test
    public void save_fileBlockingDirectory_throwsStorageException() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "file");

        assertThrows(StorageException.class, () ->
                new Storage(blockingFile.resolve("arthur.bin")).save(new CommandContext()));
    }
}
