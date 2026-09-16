package puyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.TaskList;
import puyo.task.ToDo;
import puyo.ui.Ui;

/**
 * Tests for {@code AddCommand}.
 */
public class AddCommandTest {

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test_tasks.txt").toString());
    }

    @Test
    public void execute_addTodo_addsTaskAndPersists() throws PuyoException {
        ToDo todo = new ToDo("read book");
        AddCommand command = new AddCommand(todo);

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getName());
    }

    @Test
    public void isExit_returnsFalse() {
        ToDo todo = new ToDo("test exit");
        AddCommand command = new AddCommand(todo);

        assertFalse(command.isExit());
    }
}
