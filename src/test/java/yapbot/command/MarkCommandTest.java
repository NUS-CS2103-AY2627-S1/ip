package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class MarkCommandTest {

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
    public void execute_validIndex_marksTaskAsDoneAndPersists() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        MarkCommand command = new MarkCommand("mark 1");

        command.execute(taskList, new Ui());

        assertEquals("1", taskList.get(0).getStatusValue());

        Task[] loaded = new Task[10];
        Storage.load(loaded);
        assertEquals("1", loaded[0].getStatusValue());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrown() {
        TaskList taskList = new TaskList();
        MarkCommand command = new MarkCommand("mark 1");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }
}
