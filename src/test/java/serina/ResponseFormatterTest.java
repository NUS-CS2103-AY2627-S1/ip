package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import serina.task.Deadline;
import serina.task.Todo;

/** Tests user-facing response formatting independently from command execution. */
public class ResponseFormatterTest {
    @Test
    public void greetings_systemState_includeExpectedStatusLine() {
        assertTrue(ResponseFormatter.formatGreeting(true).contains("all systems normal"));
        assertEquals("Standing by, Captain Cutter. Type help for available commands.",
                ResponseFormatter.formatGreeting(false));
        assertTrue(ResponseFormatter.formatHelp().contains("event <task> /from"));
        assertTrue(ResponseFormatter.formatGoodbye().contains("Captain Cutter"));
    }

    @Test
    public void taskCollections_emptyAndPopulated_formatHeadingsAndNumbering() {
        Todo todo = new Todo("inspect engines");
        Deadline deadline = new Deadline("report", LocalDate.of(2026, 9, 14));

        assertTrue(ResponseFormatter.formatTaskList(List.of()).startsWith("No tasks"));
        assertTrue(ResponseFormatter.formatMatchingTasks(List.of()).startsWith("No matching"));
        assertEquals(String.join("\n", "Your task roster, Captain Cutter:",
                "1.[T][ ] inspect engines", "2.[D][ ] report (by: Sep 14 2026)"),
                ResponseFormatter.formatTaskList(List.of(todo, deadline)));
    }

    @Test
    public void mutationResponses_taskCounts_useCorrectSingularAndPlural() {
        Todo todo = new Todo("inspect engines");

        assertTrue(ResponseFormatter.formatAddedTask(todo, 1).contains("1 task on the roster"));
        assertTrue(ResponseFormatter.formatDeletedTask(todo, 0).contains("0 tasks on the roster"));
        todo.markAsDone();
        assertTrue(ResponseFormatter.formatMarkedTask(todo).contains("[T][X]"));
        assertTrue(ResponseFormatter.formatAlreadyMarkedTask(todo).contains("Already complete"));
        todo.markAsNotDone();
        assertTrue(ResponseFormatter.formatUnmarkedTask(todo).contains("[T][ ]"));
        assertTrue(ResponseFormatter.formatAlreadyUnmarkedTask(todo).contains("already active"));
    }
}
