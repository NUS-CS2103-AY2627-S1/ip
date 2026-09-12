package minweeder.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import minweeder.task.Deadline;
import minweeder.task.TaskList;
import minweeder.task.Todo;

public class UiTest {

    @Test
    public void decorate_message_surroundedByDividers() {
        Ui ui = new Ui();
        String line = "────────────────────────────────────────────────────────────────\n";

        String result = ui.decorate("hello");

        assertEquals(line + "hello\n" + line, result);
    }

    @Test
    public void showWelcome_returnsGreeting() {
        Ui ui = new Ui();

        assertEquals("Heyyo I'm Minweeder!\nLETS GET THINGS DONE RAHH", ui.showWelcome());
    }

    @Test
    public void showGoodbye_returnsGoodbyeMessage() {
        Ui ui = new Ui();

        assertEquals("Goodbye! Hope you had a productive session :)", ui.showGoodbye());
    }

    @Test
    public void showLoadingError_includesUnderlyingMessage() {
        Ui ui = new Ui();

        assertEquals("I couldn't read your saved tasks, so let's start afresh. disk full",
                ui.showLoadingError("disk full"));
    }

    @Test
    public void showSkippedLines_includesCount() {
        Ui ui = new Ui();

        assertEquals("BTW 3 line(s) of your save file were unreadable so some may be missing :(",
                ui.showSkippedLines(3));
    }

    @Test
    public void showError_includesMessage() {
        Ui ui = new Ui();

        assertEquals("Erm...you can't do that...that's not a valid command.",
                ui.showError("that's not a valid command."));
    }

    @Test
    public void showTaskAdded_includesLabelTaskAndCount() {
        Ui ui = new Ui();
        Todo todo = new Todo("read book");

        String result = ui.showTaskAdded("Todo", todo, 1);

        assertEquals("Okay! Todo successfully added:\n  [T][ ] read book\nNow you have 1 tasks in your list.",
                result);
    }

    @Test
    public void showTaskDeleted_includesTaskAndRemainingCount() {
        Ui ui = new Ui();
        Todo todo = new Todo("read book");

        String result = ui.showTaskDeleted(todo, 0);

        assertEquals("Task successfully removed: \n [T][ ] read book\nNow you have 0 tasks in your list.",
                result);
    }

    @Test
    public void showTaskMarked_includesTask() {
        Ui ui = new Ui();
        Todo todo = new Todo("read book");
        todo.mark();

        String result = ui.showTaskMarked(todo);

        assertEquals("Congrats! Task has been marked as completed:\n  [T][X] read book", result);
    }

    @Test
    public void showTaskUnmarked_includesTask() {
        Ui ui = new Ui();
        Todo todo = new Todo("read book");

        String result = ui.showTaskUnmarked(todo);

        assertEquals("Done! Task has been marked as not done yet:\n  [T][ ] read book", result);
    }

    @Test
    public void showList_emptyList_showsHeaderOnly() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();

        assertEquals("Here are your tasks:", ui.showList(tasks));
    }

    @Test
    public void showList_multipleTasks_numbersEachTask() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        assertEquals("Here are your tasks:\n1. [T][ ] read book\n2. [T][ ] return book", ui.showList(tasks));
    }

    @Test
    public void showTasksOn_noMatchingTasks_showsHeaderOnly() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String result = ui.showTasksOn(LocalDate.of(2024, 12, 2), tasks);

        assertEquals("Tasks occurring on Dec 02 2024:", result);
    }

    @Test
    public void showTasksOn_matchingDeadline_showsOnlyMatchingTask() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDateTime.of(2024, 12, 2, 18, 0)));

        String result = ui.showTasksOn(LocalDate.of(2024, 12, 2), tasks);

        // The am/pm marker's case is locale/JDK-dependent (see DeadlineTest), so compare
        // case-insensitively rather than pinning one case.
        assertEquals("Tasks occurring on Dec 02 2024:\n2. [D][ ] submit report (by: Dec 02 2024, 6:00pm)"
                .toLowerCase(), result.toLowerCase());
    }

    @Test
    public void showFoundTasks_noMatches_showsHeaderOnly() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String result = ui.showFoundTasks(List.of(), tasks);

        assertEquals("Here are the matching tasks in your list:", result);
    }

    @Test
    public void showFoundTasks_someMatches_numbersByFullListPosition() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("join club"));
        tasks.add(new Todo("read book"));

        String result = ui.showFoundTasks(List.of(1), tasks);

        assertEquals("Here are the matching tasks in your list:\n2. [T][ ] read book", result);
    }
}
