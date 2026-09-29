package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that tasks written to a file come back unchanged, and that an unusable file
 * is reported rather than ignored.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storageAt(String... parts) {
        Path path = tempDir;
        for (String part : parts) {
            path = path.resolve(part);
        }
        return new Storage(path.toString());
    }

    @Test
    public void load_fileDoesNotExist_returnsNoTasks() throws EmmaException {
        assertTrue(storageAt("emma.json").load().isEmpty());
    }

    @Test
    public void saveThenLoad_everyTaskType_comesBackUnchanged() throws EmmaException {
        Task done = new Todo("read book");
        done.setDone(true);
        TaskList tasks = new TaskList(List.of(
                done,
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Event("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16))));

        Storage storage = storageAt("emma.json");
        storage.save(tasks);

        TaskList reloaded = new TaskList(storage.load());
        assertEquals("1. [T][x] read book\n"
                + "2. [D][ ] return book (by: Oct 15 2019)\n"
                + "3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", reloaded.format());
    }

    @Test
    public void save_folderDoesNotExist_createsIt() throws EmmaException {
        Storage storage = storageAt("nested", "deeper", "emma.json");
        storage.save(new TaskList(List.of(new Todo("read book"))));
        assertTrue(Files.exists(tempDir.resolve("nested").resolve("deeper").resolve("emma.json")));
    }

    @Test
    public void save_emptyList_loadsBackEmpty() throws EmmaException {
        Storage storage = storageAt("emma.json");
        storage.save(new TaskList(List.of()));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_calledAgain_replacesTheWholeFile() throws EmmaException {
        Storage storage = storageAt("emma.json");
        storage.save(new TaskList(List.of(new Todo("first"), new Todo("second"))));
        storage.save(new TaskList(List.of(new Todo("only one now"))));

        TaskList reloaded = new TaskList(storage.load());
        assertEquals("1. [T][ ] only one now", reloaded.format());
    }

    @Test
    public void load_fileThatIsNotTheExpectedShape_saysWhereItIsWrong() throws IOException {
        Path file = tempDir.resolve("emma.json");
        Files.writeString(file, "not json at all");
        String message = assertThrows(EmmaException.class, () -> new Storage(file.toString()).load())
                .getMessage();
        assertTrue(message.contains("looks wrong on line 1"), message);
    }

    @Test
    public void save_pathBlockedByAFile_reportsTheFailure() throws IOException {
        Path blocker = tempDir.resolve("data");
        Files.writeString(blocker, "a file where the folder should be");
        Storage storage = new Storage(blocker.resolve("emma.json").toString());

        String message = assertThrows(EmmaException.class, () ->
                storage.save(new TaskList(List.of(new Todo("read book"))))).getMessage();
        assertTrue(message.startsWith("I couldn't save to"), message);
    }
}
