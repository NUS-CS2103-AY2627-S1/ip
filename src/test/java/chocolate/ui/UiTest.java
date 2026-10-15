package chocolate.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import chocolate.exception.ChocolateException;
import chocolate.task.Deadline;
import chocolate.task.Event;
import chocolate.task.TaskList;
import chocolate.task.Todo;

/**
 * Tests user-facing message formatting in {@link Ui}.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void getWelcomeAndGoodbyeMessages_returnCocoaCornerPhrases() {
        assertEquals("Chocolate\nHello! I'm Chocolate, your task chocolatier."
                        + "\nWhat shall we sweeten up today?",
                ui.getWelcomeMessage());
        assertEquals("Thanks for visiting Chocolate's Cocoa Corner. See you soon!",
                ui.getGoodbyeMessage());
    }

    @Test
    public void getTaskListMessages_formatActiveAndMatchingTasks() throws ChocolateException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        tasks.add(todo);
        tasks.add(deadline);
        tasks.mark(1);

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book"
                        + "\n2.[D][X] return book (by: Oct 15 2019)",
                ui.getTaskListMessage(tasks));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                ui.getMatchingTasksMessage(tasks.find("READ")));
    }

    @Test
    public void getActionMessages_formatTaskAndArchiveResponses() {
        Todo todo = new Todo("buy chocolate");
        todo.markAsDone();
        Event event = new Event("meeting", "Monday", "Tuesday");

        assertEquals("Sweet! I've added this to your list:\n  [T][X] buy chocolate"
                        + "\nYou now have 3 tasks on your tray.",
                ui.getAddedMessage(todo, 3));
        assertEquals("Delicious progress! I've marked this task as done:\n  [X] buy chocolate",
                ui.getMarkedMessage(todo));
        assertEquals("No worries! I've marked this task as not done yet:\n  [ ] buy chocolate",
                ui.getUnmarkedMessage(todo));
        assertEquals("Poof! This task has melted away:\n  [T][X] buy chocolate"
                        + "\nYou now have 2 tasks left on your tray.",
                ui.getDeletedMessage(todo, 2));
        assertEquals("Your 2 task(s) are tucked safely into the archive.", ui.getArchivedMessage(2));
        assertEquals("The archive basket is empty.", ui.getNoTasksToArchiveMessage());
        assertEquals("The archive basket is empty.", ui.getArchivedTaskListMessage(java.util.List.of()));
        assertEquals("Here are your archived treats:\n1.[E][ ] meeting (from: Monday to: Tuesday)",
                ui.getArchivedTaskListMessage(java.util.List.of(event)));
        assertEquals("Oops! That crumbled. A test error", ui.getErrorMessage("A test error"));
    }
}
