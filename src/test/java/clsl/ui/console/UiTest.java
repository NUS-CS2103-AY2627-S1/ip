package clsl.ui.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import clsl.task.Deadline;
import clsl.task.Event;
import clsl.task.Task;
import clsl.task.ToDo;

/** Tests formatting of messages shown to users. */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    void formatWelcomeAndGoodbyeContainExpectedMessages() {
        assertTrue(ui.formatWelcome().contains("Hello! I'm Clsl."));
        assertTrue(ui.formatWelcome().contains("What can I do for you?"));
        assertEquals("\nBye. Hope to see you again soon!", ui.formatGoodbye());
    }

    @Test
    void formatErrorsIncludeErrorPrefix() {
        assertEquals("\nERROR: invalid command\n", ui.formatError("invalid command"));
        assertEquals("\nUnable to load tasks\n", ui.formatLoadingError("Unable to load tasks"));
    }

    @Test
    void formatTaskListHandlesEmptyAndNonEmptyLists() {
        Task todo = new ToDo("read book");
        Task deadline = new Deadline("submit report", "2026-09-02");

        assertEquals("\nHere are your tasks in your list:\n", ui.formatTaskList(List.of()));
        assertEquals("\nHere are your tasks in your list:\n1.[T][ ] read book"
                + "\n2.[D][ ] submit report (by: Sep 2 2026)\n",
                ui.formatTaskList(List.of(todo, deadline)));
    }

    @Test
    void formatMatchingTasksAndTasksOnIncludeTheirHeadings() {
        Task todo = new ToDo("read book");
        Event event = new Event("conference", "2026-09-01", "2026-09-03");

        assertEquals("\nHere are the matching tasks in your list:\n1.[T][ ] read book\n",
                ui.formatMatchingTasks(List.of(todo)));
        assertEquals("\nHere are the tasks occurring on 2026-09-02:\n1.[E][ ] conference"
                + " (from: Sep 1 2026 to: Sep 3 2026)\n",
                ui.formatTasksOn(LocalDate.of(2026, 9, 2), List.of(event)));
        assertEquals("\nHere are the tasks occurring on 2026-09-04:\nnone\n",
                ui.formatTasksOn(LocalDate.of(2026, 9, 4), List.of()));
    }

    @Test
    void formatTaskActionsIncludeTaskAndCount() {
        ToDo todo = new ToDo("read book");

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.\n",
                ui.formatTaskAdded(todo, 1));
        assertEquals("\nNice! I've marked this task as Done:\n[T][ ] read book\n",
                ui.formatTaskMarked(todo));
        assertEquals("\nOK, I've marked this task as not done yet:\n[T][ ] read book\n",
                ui.formatTaskUnmarked(todo));
        assertEquals("\nNoted. I've removed this task:\n  [T][ ] read book\n"
                + "Now you have 0 tasks in the list.\n",
                ui.formatTaskDeleted(todo, 0));
    }
}
