package lumine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lumine.LumineException;
import lumine.storage.Storage;

/**
 * Tests undo behavior and persistence for task-list changes.
 */
class TaskListTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void undoLastChange_afterAdd_removesTaskAndPersistsResult() {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        TaskList taskList = createTaskList(saveFile);
        taskList.setCurrentCommand("todo temporary task");
        taskList.addTask(new Todo("temporary task"));

        assertEquals("todo temporary task", taskList.undoLastChange());

        assertEquals(0, taskList.size());
        assertEquals("Nice, there are no task on your list!", taskList.formatTasks());
        assertEquals("", readFile(saveFile));
    }

    @Test
    void undoLastChange_withoutPreviousChange_throwsLumineException() {
        TaskList taskList = createTaskList(temporaryDirectory.resolve("lumine.txt"));

        LumineException exception = assertThrows(LumineException.class, taskList::undoLastChange);

        assertEquals("Sorry, there is no command to undo. :C", exception.getMessage());
    }

    @Test
    void undoLastChange_afterDelete_restoresOriginalPositionAndPersistsResult() {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        TaskList taskList = createTaskList(saveFile);
        taskList.addTask(new Todo("first task"));
        taskList.addTask(new Todo("second task"));
        taskList.deleteTask(1);

        taskList.undoLastChange();

        assertEquals("Here are the tasks in your list:\n"
                        + "1.[T][ ] first task\n"
                        + "2.[T][ ] second task", taskList.formatTasks());
        assertEquals("T | 0 | first task" + System.lineSeparator() + "T | 0 | second task",
                readFile(saveFile));
    }

    @Test
    void undoLastChange_afterMark_restoresUndoneStatus() {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        TaskList taskList = createTaskList(saveFile);
        taskList.addTask(new Todo("task"));
        taskList.markAsDone(1);

        taskList.undoLastChange();

        assertEquals("Here are the tasks in your list:\n1.[T][ ] task", taskList.formatTasks());
        assertEquals("T | 0 | task", readFile(saveFile));
    }

    @Test
    void undoLastChange_afterUnmark_restoresDoneStatus() {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        TaskList taskList = createTaskList(saveFile);
        taskList.addTask(new Todo("task"));
        taskList.markAsDone(1);
        taskList.markAsUndone(1);

        taskList.undoLastChange();

        assertEquals("Here are the tasks in your list:\n1.[T][X] task", taskList.formatTasks());
        assertEquals("T | 1 | task", readFile(saveFile));
    }

    private TaskList createTaskList(Path saveFile) {
        return new TaskList(new Storage(saveFile.toString()));
    }

    private String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (Exception exception) {
            throw new AssertionError("Expected save file to be readable", exception);
        }
    }
}
