package pdd.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pdd.task.Deadline;
import pdd.task.Task;
import pdd.task.Todo;

public class UiTest {
    private static final String LINE = "____________________________________________________________";

    private final List<String> lines = new ArrayList<>();
    private final Ui ui = new Ui(lines::add);

    private String output() {
        return String.join("\n", lines);
    }

    @Test
    public void showWelcome_printsBannerBetweenDividers() {
        ui.showWelcome();
        assertEquals(LINE, lines.get(0));
        assertEquals("Hello! I'm PDD.", lines.get(2));
        assertEquals("What can I do for you?", lines.get(3));
        assertEquals(LINE, lines.get(4));
    }

    @Test
    public void showGoodbye_printsGoodbyeMessage() {
        ui.showGoodbye();
        assertEquals("Bye. Hope to see you again soon!", output());
    }

    @Test
    public void showError_printsGivenMessageVerbatim() {
        ui.showError("OOPS!!! something went wrong");
        assertEquals("OOPS!!! something went wrong", output());
    }

    @Test
    public void showTaskList_emptyList_printsHeaderOnly() {
        ui.showTaskList(List.of());
        assertEquals("Here are the tasks in your list:", output());
    }

    @Test
    public void showTaskList_multipleTasks_printsEachNumberedFromOne() {
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        ui.showTaskList(List.of(first, second));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book\n2.[T][ ] return book", output());
    }

    @Test
    public void showTasksOn_filtersToMatchingDateOnly() {
        Deadline matching = new Deadline("return book", LocalDate.of(2019, 12, 1));
        Deadline notMatching = new Deadline("pay bills", LocalDate.of(2019, 12, 2));
        ui.showTasksOn(LocalDate.of(2019, 12, 1), List.of(matching, notMatching));
        assertEquals("Here are the tasks on Dec 01 2019:\n1.[D][ ] return book (by: Dec 01 2019)", output());
    }

    @Test
    public void showMarked_printsConfirmationWithTaskLine() {
        Todo task = new Todo("read book");
        task.markAsDone();
        ui.showMarked(task);
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book", output());
    }

    @Test
    public void showUnmarked_printsConfirmationWithTaskLine() {
        Todo task = new Todo("read book");
        ui.showUnmarked(task);
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book", output());
    }

    @Test
    public void showDeleted_printsConfirmationWithTaskLineAndRemainingCount() {
        Todo task = new Todo("read book");
        ui.showDeleted(task, 3);
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 3 tasks in the list.",
                output());
    }

    @Test
    public void showAdded_printsConfirmationWithTaskLineAndTotalCount() {
        Todo task = new Todo("read book");
        ui.showAdded(task, 1);
        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                output());
    }

    @Test
    public void showMatchingTasks_printsEachMatchNumberedFromOne() {
        Task match = new Todo("read book");
        ui.showMatchingTasks(List.of(match));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book", output());
    }

    @Test
    public void showSorted_printsConfirmationThenEachTaskNumberedFromOne() {
        Task first = new Todo("a todo");
        ui.showSorted(List.of(first));
        assertEquals("Sorted! Here are your tasks, in order:\n1.[T][ ] a todo", output());
    }
}
