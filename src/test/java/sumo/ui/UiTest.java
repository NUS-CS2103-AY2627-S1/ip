package sumo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sumo.task.Deadline;
import sumo.task.Task;
import sumo.task.Todo;

/** Tests the console text emitted by {@link Ui}. */
public class UiTest {
    private List<String> output;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        output = new ArrayList<>();
        ui = new Ui(output::add);
    }

    @Test
    public void simpleMessages_allMethods_expectedTextEmitted() {
        ui.showSeparator();
        ui.showGoodbye();
        ui.showCommandError("bad command");
        ui.showLoadingError("bad file");
        ui.showInvalidTaskError(4, "bad record");
        ui.showSavingError("read only");
        ui.showNoTasksToSort();

        assertEquals(List.of(
                "____________________________________________________________",
                "Until next time. I'll be here when you need me.",
                " I could not complete that command: bad command",
                " I could not load your saved tasks: bad file",
                " I could not load saved task on line 4: bad record",
                " I could not save your tasks: read only",
                " No tasks to be sorted."), output);
    }

    @Test
    public void showWelcome_expectedBannerAndGreetingEmitted() {
        ui.showWelcome();

        assertEquals(5, output.size());
        assertEquals("____________________________________________________________", output.get(0));
        assertEquals("My name is Sumo.", output.get(2));
        assertEquals("I'm here to help you stay on task.", output.get(3));
        assertEquals("____________________________________________________________", output.get(4));
    }

    @Test
    public void taskQueries_noMatches_explicitEmptyMessages() {
        ui.showTaskList(List.of());
        ui.showMatchingTasks(List.of());
        ui.showTasksOnDate(LocalDate.of(2026, 2, 3).atStartOfDay(), List.of());

        assertEquals(List.of(" No tasks in your list yet.", " No matching tasks found.",
                " No tasks on Feb 03 2026."), output);
    }

    @Test
    public void taskQueries_multipleTasks_numberedWithExpectedHeadings() {
        List<Task> tasks = List.of(new Todo("read"), new Todo("write"));

        ui.showTaskList(tasks);
        ui.showMatchingTasks(tasks.subList(1, 2));
        ui.showTasksOnDate(LocalDate.of(2026, 2, 3).atStartOfDay(), tasks.subList(0, 1));

        assertEquals(List.of(
                " Here are the tasks in your list:", " 1.[T][ ] read", " 2.[T][ ] write",
                " Here are the matching tasks in your list:", " 1.[T][ ] write",
                " Here are the tasks on Feb 03 2026:", " 1.[T][ ] read"), output);
    }

    @Test
    public void showSortedTaskList_mixedStatuses_groupedAndNumbered() {
        Todo first = new Todo("first");
        Todo completed = new Todo("completed");
        completed.markAsDone();

        ui.showSortedTaskList(List.of(first, completed));

        assertEquals(List.of(
                " Here are your tasks sorted chronologically (ascending):",
                " Incomplete tasks:", " 1.[T][ ] first",
                " Completed tasks:", " 2.[T][X] completed",
                " Sorted view only; the normal task order is unchanged."), output);
    }

    @Test
    public void showSortedTaskList_onlyCompletedTasks_incompleteHeadingOmitted() {
        Todo completed = new Todo("completed");
        completed.markAsDone();

        ui.showSortedTaskList(List.of(completed));

        assertEquals(List.of(
                " Here are your tasks sorted chronologically (ascending):",
                " Completed tasks:", " 1.[T][X] completed",
                " Sorted view only; the normal task order is unchanged."), output);
    }

    @Test
    public void mutationMessages_allMethods_expectedTextEmitted() {
        Deadline task = new Deadline("submit", LocalDate.of(2026, 2, 3));
        ui.showTaskAdded(task, 1);
        task.markAsDone();
        ui.showTaskMarked(task);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        ui.showTaskDeleted(task, 0);

        assertEquals(List.of(
                " Understood. I've added this task:", "   [D][ ] submit (by: Feb 03 2026)",
                " Now you have 1 tasks in the list.",
                " Confirmed. Task completed:", "   [D][X] submit (by: Feb 03 2026)",
                " Plans change. I've marked this task as incomplete:", "   [D][ ] submit (by: Feb 03 2026)",
                " Noted. I've removed this task:", "   [D][ ] submit (by: Feb 03 2026)",
                " Now you have 0 tasks in the list."), output);
    }
}
