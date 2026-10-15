package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.task.Todo;
import yapbot.ui.Ui;

public class AddCommandTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    public void redirectStorageToTempFile() {
        Storage.setTargetPathForTesting(tempDir.resolve("yapBot.txt"));
    }

    @AfterEach
    public void restoreRealStoragePath() {
        Storage.resetTargetPathForTesting();
    }

    @Test
    public void execute_addsTaskToListAndPersistsIt() throws YapBotException {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        AddCommand command = new AddCommand(task);

        command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);
        assertEquals(1, loadedCount);
        assertEquals("T | 0 | read book", loaded[0].toFileFormat());
    }

    @Test
    public void isExit_always_returnsFalse() {
        AddCommand command = new AddCommand(new Todo("read book"));

        assertFalse(command.isExit());
    }

    @Test
    public void getTask_returnsTaskPassedToConstructor() {
        Task task = new Todo("read book");
        AddCommand command = new AddCommand(task);

        assertSame(task, command.getTask());
    }
}
