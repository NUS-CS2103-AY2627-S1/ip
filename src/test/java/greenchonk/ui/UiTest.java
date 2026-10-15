package greenchonk.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.task.Deadline;
import greenchonk.task.TaskList;
import greenchonk.task.TaskStatus;
import greenchonk.task.Todo;

class UiTest {
    @Test
    void commandInput_multipleLines_linesReadInOrder() {
        ByteArrayInputStream input = new ByteArrayInputStream(
                "list\nbye\n".getBytes(StandardCharsets.UTF_8));
        Ui ui = new Ui(input, new PrintStream(new ByteArrayOutputStream()));

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void taskFeedback_variedTasksAndCounts_allDetailsShown() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));
        TaskList tasks = new TaskList(List.of(todo, deadline));

        ui.showTaskAdded(todo, 1);
        ui.showTaskAdded(deadline, 2);
        ui.showTaskStatusUpdated(todo, TaskStatus.DONE);
        ui.showTaskStatusUpdated(todo, TaskStatus.NOT_DONE);
        ui.showTaskEdited(todo, new Todo("read chapter"));
        ui.showTaskDeleted(deadline, 1);
        ui.showTaskDeleted(todo, 0);
        ui.showTaskList(tasks);
        ui.showTaskList(new TaskList());

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Green Chonk is now carrying 1 task."));
        assertTrue(text.contains("Green Chonk is now carrying 2 tasks."));
        assertTrue(text.contains("marked this task as done"));
        assertTrue(text.contains("marked this task as not done yet"));
        assertTrue(text.contains("Before: [T][ ] read book"));
        assertTrue(text.contains("After:  [T][ ] read chapter"));
        assertTrue(text.contains("1.[T][ ] read book"));
        assertTrue(text.contains("2.[D][ ] submit report (by: Sep 18 2026)"));
        assertTrue(text.contains("not carrying any tasks yet"));
    }

    @Test
    void informationalFeedback_allMessagesRemainActionable() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        LocalDate date = LocalDate.of(2026, 9, 18);

        ui.showWelcome();
        ui.showError("Try a valid command.");
        ui.showLoadingError("Repair the data file.");
        ui.showScheduleHeader(date);
        ui.showNumberedTask(3, new Todo("prepare demo"));
        ui.showEmptySchedule(date);
        ui.showFindHeader();
        ui.showNoMatchingTasks();
        ui.showHelp();
        ui.showGoodbye();

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Green Chonk—part bear, part pear"));
        assertTrue(text.contains("Oops! Green Chonk couldn't chomp that"));
        assertTrue(text.contains("couldn't load saved tasks"));
        assertTrue(text.contains("tasks scheduled for 2026-09-18"));
        assertTrue(text.contains("3.[T][ ] prepare demo"));
        assertTrue(text.contains("no deadlines or events scheduled"));
        assertTrue(text.contains("matching tasks in your list"));
        assertTrue(text.contains("found no matching tasks"));
        assertTrue(text.contains("edit TASK_NUMBER /description DESCRIPTION"));
        assertTrue(text.contains("Task backpack zipped"));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(ByteArrayInputStream.nullInputStream(),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
