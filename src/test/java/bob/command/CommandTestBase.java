package bob.command;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import bob.storage.Storage;
import bob.storage.TaskStorage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Provides shared command test fixtures with pre-initialized task lists,
 * a user interface, and isolated temporary storage.
 */
public abstract class CommandTestBase {

    @TempDir
    protected Path tempDir;

    protected TaskList tasks;
    protected Ui ui;
    protected Storage<TaskList> storage;

    @BeforeEach
    public void setUp() {
        this.tasks = new TaskList();
        this.ui = new Ui();
        this.storage = new TaskStorage(tempDir.resolve("test_tasks.txt"));
    }
}
