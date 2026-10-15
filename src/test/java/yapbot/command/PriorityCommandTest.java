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
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.task.Todo;
import yapbot.ui.Ui;

public class PriorityCommandTest {

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
    public void execute_validIndexAndLevel_setsPriorityAndPersists() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        PriorityCommand command = new PriorityCommand("priority 1 high");

        command.execute(taskList, new Ui());

        assertEquals(Priority.HIGH, taskList.get(0).getPriority());

        Task[] loaded = new Task[10];
        Storage.load(loaded);
        assertEquals(Priority.HIGH, loaded[0].getPriority());
    }

    @Test
    public void execute_taskAlreadyHasPriority_overwritesWithNewLevel() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.get(0).setPriority(Priority.LOW);
        PriorityCommand command = new PriorityCommand("priority 1 high");

        command.execute(taskList, new Ui());

        assertEquals(Priority.HIGH, taskList.get(0).getPriority());
    }

    @Test
    public void execute_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();
        PriorityCommand command = new PriorityCommand("priority 1 high");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        PriorityCommand command = new PriorityCommand("priority 99 high");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }

    @Test
    public void execute_nonNumericIndex_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        PriorityCommand command = new PriorityCommand("priority abc high");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }

    @Test
    public void execute_missingLevel_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        PriorityCommand command = new PriorityCommand("priority 1");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }

    @Test
    public void execute_invalidLevel_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        PriorityCommand command = new PriorityCommand("priority 1 urgent");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }

    @Test
    public void execute_noArguments_exceptionThrown() {
        TaskList taskList = new TaskList();
        PriorityCommand command = new PriorityCommand("priority");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }
}
