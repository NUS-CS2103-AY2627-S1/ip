package friday.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import friday.model.TaskList;
import friday.model.ToDo;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Tests moving tasks between active and archived task lists.
 */
class ArchiveCommandTest {
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that archiving one task removes it from active tasks and persists it in the archive.
     */
    @Test
    void execute_archiveOneTask_movesTaskToArchive() throws Exception {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));
        TaskList archivedTasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("friday.txt").toString());
        Storage archiveStorage = new Storage(temporaryDirectory.resolve("archive.txt").toString());

        new ArchiveCommand(2).execute(tasks, archivedTasks, createUi(), storage, archiveStorage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals(1, archivedTasks.size());
        assertEquals("[T][ ] second", archivedTasks.get(0).toString());
        assertEquals(1, archiveStorage.load().getTasks().size());
    }

    /**
     * Verifies that archiving and restoring all tasks preserve task order.
     */
    @Test
    void execute_archiveAndUnarchiveAll_preservesTaskOrder() throws Exception {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));
        TaskList archivedTasks = new TaskList(new ToDo("older"));
        Storage storage = new Storage(temporaryDirectory.resolve("friday.txt").toString());
        Storage archiveStorage = new Storage(temporaryDirectory.resolve("archive.txt").toString());

        new ArchiveCommand().execute(tasks, archivedTasks, createUi(), storage, archiveStorage);
        new UnarchiveCommand().execute(tasks, archivedTasks, createUi(), storage, archiveStorage);

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] older", tasks.get(0).toString());
        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertTrue(archivedTasks.asList().isEmpty());
        assertTrue(archiveStorage.load().getTasks().isEmpty());
    }

    /**
     * Verifies that restoring one archived task appends it to active tasks and removes it from the archive.
     */
    @Test
    void execute_unarchiveOneTask_movesTaskToActiveListEnd() throws Exception {
        TaskList tasks = new TaskList(new ToDo("active"));
        TaskList archivedTasks = new TaskList(new ToDo("first"), new ToDo("second"));
        Storage storage = new Storage(temporaryDirectory.resolve("friday.txt").toString());
        Storage archiveStorage = new Storage(temporaryDirectory.resolve("archive.txt").toString());

        new UnarchiveCommand(2).execute(tasks, archivedTasks, createUi(), storage, archiveStorage);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] active", tasks.get(0).toString());
        assertEquals("[T][ ] second", tasks.get(1).toString());
        assertEquals(1, archivedTasks.size());
        assertEquals("[T][ ] first", archivedTasks.get(0).toString());
        assertEquals(2, storage.load().getTasks().size());
    }

    private static Ui createUi() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        return new Ui(new java.util.Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
