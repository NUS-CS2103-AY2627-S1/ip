package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

public class DeleteCommandTest {

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
    public void execute_validIndex_removesTaskAndPersistsRemainder() throws YapBotException {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write code");
        taskList.add(first);
        taskList.add(second);
        DeleteCommand command = new DeleteCommand("delete 1");

        command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);
        assertEquals(1, loadedCount);
        assertEquals("write code", loaded[0].getDescription());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrownAndListUnchanged() {
        TaskList taskList = new TaskList();
        DeleteCommand command = new DeleteCommand("delete 1");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
        assertEquals(0, taskList.size());
    }
}
