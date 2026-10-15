package samantha.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.model.Deadline;
import samantha.model.Event;
import samantha.model.TaskList;
import samantha.model.Todo;
import samantha.storage.Storage;
import samantha.ui.Ui;

class FindCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_keywordMatchesDescriptions_caseInsensitiveAndKeepsOriginalNumbers()
            throws Exception {
        TaskList tasks = populatedTaskList();
        tasks.getTask(1).markDone();

        String output = CommandTestOutput.capture(() -> new FindCommand("BOOK")
                .execute(tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));

        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1. [T][X] read book"));
        assertTrue(output.contains("2. [D][ ] return book (by: Dec 2 2019)"));
        assertFalse(output.contains("3. [E][ ] attend conference"));
    }

    @Test
    void execute_keywordHasNoMatches_displaysOnlyMatchingTasksHeader() throws Exception {
        String output = CommandTestOutput.capture(() -> new FindCommand("holiday")
                .execute(populatedTaskList(), new Ui(),
                        new Storage(temporaryDirectory.resolve("tasks.txt"))));

        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertFalse(output.contains("1. ["));
        assertFalse(output.contains("2. ["));
        assertFalse(output.contains("3. ["));
    }

    private TaskList populatedTaskList() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2/12/2019"));
        tasks.add(new Event("attend conference", "2/12/2019 0900", "2/12/2019 1700"));
        return tasks;
    }
}
