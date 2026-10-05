package friday;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import friday.task.RepeatFrequency;
import friday.task.TaskList;
import friday.task.Todo;

/**
 * Tests the content assembled for user-facing responses.
 */
class UiTest {

    @Test
    void getWelcome_includesAsciiBannerBeforeGreeting() {
        String welcome = new Ui().getWelcome();

        assertTrue(welcome.startsWith(" _____ ____  ___ ____    _ __   __\n"));
        assertTrue(welcome.contains("|_|   |_| \\_\\___|____/_/   \\_\\_|\n\nAll systems are online"));
    }

    @Test
    void taskResponses_singleTask_includeTaskAndCounts() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        assertTrue(ui.getTaskAdded(task, 1).contains("read book"));
        assertTrue(ui.getTaskAdded(task, 1).contains("1 task"));
        assertTrue(ui.getTaskMarked(task).contains("read book"));
        assertTrue(ui.getTaskUnmarked(task).contains("read book"));
        assertTrue(ui.getTaskDeleted(task, 0).contains("0 tasks"));
        assertTrue(ui.getTaskRepeated(task, RepeatFrequency.WEEKLY).contains("weekly"));
    }

    @Test
    void listResponses_tasksAndEmptyReminders_includeRelevantContent() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(ui.getTaskList(tasks).contains("1. [T][ ] read book"));
        assertTrue(ui.getMatchingTasks(tasks).contains("matching tasks"));
        assertTrue(ui.getTasksSorted(tasks).contains("arranged chronologically"));
        assertTrue(ui.getReminders(new TaskList()).contains("no dated reminders"));
    }

    @Test
    void generalResponses_includeExpectedGuidance() {
        Ui ui = new Ui();

        assertTrue(ui.getWelcome().contains("All systems are online"));
        assertTrue(ui.getBye().contains("Powering down"));
        assertTrue(ui.getError("invalid input").contains("invalid input"));
        assertTrue(ui.getHelp().contains("todo <description>"));
    }
}
