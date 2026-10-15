package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.exception.DingleberryException;
import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests deleting tasks from the list. */
class DeleteCommandTest {
    /** Redirects storage writes to a temporary folder during tests. */
    @TempDir
    private Path tempDir;

    @Test
    void executeValidTaskNumberRemovesAndPersistsTask() throws Exception {
        TaskList tasks = new TaskList(new Todo("read lecture notes"),
                new Todo("submit assignment"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        new DeleteCommand(1).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("submit assignment", tasks.get(0).getDescription());
        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(1, loadedTasks.size());
        assertEquals("submit assignment",
                loadedTasks.get(0).getDescription());
    }

    @Test
    void executeOutOfRangeTaskNumberThrowsException() {
        TaskList tasks = new TaskList(new Todo("read lecture notes"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> new DeleteCommand(2).execute(tasks, new Ui(), storage));

        assertEquals("That task number is not in the list.",
                exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void executeZeroTaskNumberThrowsExceptionWithoutMutation() {
        TaskList tasks = new TaskList(new Todo("read lecture notes"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        assertThrows(DingleberryException.class,
                () -> new DeleteCommand(0).execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
    }
}
