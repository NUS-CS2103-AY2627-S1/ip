package chudgpt.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;
import chudgpt.ui.Ui;

/** Tests that status commands update the task selected by their index. */
public class ChangeTaskStatusCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_nonFirstTask_updatesSelectedTask() throws Exception {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("first task"));
        taskList.addTask(new ToDo("second task"));
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        new ChangeTaskStatusCommand(1, true).execute(taskList, new Ui(), storage);

        assertFalse(taskList.getTask(0).isCompleted());
        assertTrue(taskList.getTask(1).isCompleted());
    }
}
