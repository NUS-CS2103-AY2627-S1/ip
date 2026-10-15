package samantha.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.model.Deadline;
import samantha.model.Event;
import samantha.model.TaskList;
import samantha.model.Todo;
import samantha.storage.Storage;
import samantha.ui.Ui;

class ListCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_withoutDate_listsEveryTask() throws Exception {
        TaskList tasks = populatedTaskList();

        String output = CommandTestOutput.capture(() -> new ListCommand(null).execute(
                tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));

        assertTrue(output.contains("1. [T][ ] buy groceries"));
        assertTrue(output.contains("2. [D][ ] return book"));
        assertTrue(output.contains("3. [E][ ] conference"));
    }

    @Test
    void execute_dateFilter_includesMatchingTasksWithOriginalNumbers() throws Exception {
        TaskList tasks = populatedTaskList();

        String output = CommandTestOutput.capture(() -> new ListCommand(LocalDate.of(2019, 12, 2))
                .execute(tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));

        assertFalse(output.contains("1. [T][ ] buy groceries"));
        assertTrue(output.contains("2. [D][ ] return book"));
        assertTrue(output.contains("3. [E][ ] conference"));
    }

    @Test
    void execute_dateWithNoMatches_displaysOnlyListHeader() throws Exception {
        TaskList tasks = populatedTaskList();

        String output = CommandTestOutput.capture(() -> new ListCommand(LocalDate.of(2019, 12, 5))
                .execute(tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));

        assertTrue(output.contains("Here are the tasks in your list:"));
        assertFalse(output.contains("1. ["));
    }

    private TaskList populatedTaskList() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Deadline("return book", "2/12/2019"));
        tasks.add(new Event("conference", "2/12/2019 0900", "3/12/2019 1700"));
        return tasks;
    }
}
