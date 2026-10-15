package jiji.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

public class StatsCommandTest {

    private TaskList tasks;
    private Ui ui;
    private final LocalDate testDate = LocalDate.of(2026, 9, 5);

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void execute_emptyTaskList_returnsEmptyNotice() {
        StatsCommand command = new StatsCommand(testDate);
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("No tasks in your list yet!"));
        assertTrue(result.contains("Use 'todo', 'deadline', or 'event'"));
    }

    @Test
    public void execute_mixedTaskList_computesAccurateMetrics() {
        Todo todo1 = new Todo("read book");
        todo1.markAsDone();
        Todo todo2 = new Todo("write report");

        Deadline deadline1 = new Deadline("submit assignment", "2026-09-01"); // Overdue relative to testDate
        Deadline deadline2 = new Deadline("prepare slides", "2026-09-10");
        deadline2.markAsDone(); // Completed, so not overdue

        Event event = new Event("team sync", "2026-09-05 1400", "2026-09-05 1500");

        tasks.addAll(todo1, todo2, deadline1, deadline2, event);

        StatsCommand command = new StatsCommand(testDate);
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("Total tasks: 5"));
        assertTrue(result.contains("Completed: 2 (40.0%)"));
        assertTrue(result.contains("Pending: 3"));

        assertTrue(result.contains("ToDos: 2 (1 completed)"));
        assertTrue(result.contains("Deadlines: 2 (1 completed, 1 overdue)"));
        assertTrue(result.contains("Events: 1 (0 completed)"));
        assertTrue(result.contains("Tip: Use 'list pending' to view only incomplete tasks!"));
    }

    @Test
    public void execute_allTasksCompleted_reports100Percent() {
        Todo todo = new Todo("buy milk");
        todo.markAsDone();
        tasks.add(todo);

        StatsCommand command = new StatsCommand(testDate);
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("Completed: 1 (100.0%)"));
        assertTrue(result.contains("Pending: 0"));
    }
}
