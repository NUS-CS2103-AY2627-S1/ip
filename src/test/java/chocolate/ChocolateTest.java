package chocolate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that Chocolate returns replies that can be displayed by either UI.
 */
public class ChocolateTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_validCommands_returnsExpectedReplies() {
        Chocolate chocolate = new Chocolate(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Sweet! I've added this to your list:\n  [T][ ] buy chocolate"
                        + "\nYou now have 1 tasks on your tray.",
                chocolate.getResponse("todo buy chocolate"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] buy chocolate",
                chocolate.getResponse("list"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorReply() {
        Chocolate chocolate = new Chocolate(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Oops! That crumbled. That is not a valid command. "
                        + "Please try any of these: todo, deadline, event, "
                        + "list, mark, unmark, delete, find, archive, or bye.",
                chocolate.getResponse("invalid"));
    }

    @Test
    public void getResponse_invalidInput_returnsHelpfulErrorWithoutChangingTasks() {
        Chocolate chocolate = new Chocolate(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Sweet! I've added this to your list:\n  [T][ ] buy chocolate"
                        + "\nYou now have 1 tasks on your tray.",
                chocolate.getResponse("  todo   buy chocolate  "));
        assertEquals("Oops! That crumbled. That task is already on your list!",
                chocolate.getResponse("todo buy chocolate"));
        assertEquals("Oops! That crumbled. Task number must be at least 1.",
                chocolate.getResponse("mark 0"));
        assertEquals("Oops! That crumbled. The event end date must be after its start date.",
                chocolate.getResponse("event meeting /from 2019-10-15 /to 2019-10-15"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] buy chocolate",
                chocolate.getResponse("list"));
    }

    @Test
    public void getResponse_archiveCommands_archivesAndListsTasks() {
        Chocolate chocolate = new Chocolate(temporaryDirectory.resolve("duke.txt").toString());

        assertEquals("The archive basket is empty.", chocolate.getResponse("archive"));
        chocolate.getResponse("todo buy chocolate");
        assertEquals("Your 1 task(s) are tucked safely into the archive.",
                chocolate.getResponse("archive"));
        assertEquals("Here are the tasks in your list:", chocolate.getResponse("list"));
        assertEquals("Here are your archived treats:\n1.[T][ ] buy chocolate",
                chocolate.getResponse("archive list"));

        Chocolate restartedChocolate = new Chocolate(temporaryDirectory.resolve("duke.txt").toString());
        assertEquals("Here are the tasks in your list:", restartedChocolate.getResponse("list"));
        assertEquals("Here are your archived treats:\n1.[T][ ] buy chocolate",
                restartedChocolate.getResponse("archive list"));
    }

    @Test
    public void getResponse_taskCommands_updateAndSearchTheList() {
        Chocolate chocolate = new Chocolate(temporaryDirectory.resolve("tasks.txt").toString());

        chocolate.getResponse("todo read book");
        chocolate.getResponse("deadline return book /by 2019-10-15");
        chocolate.getResponse("event project meeting /from Mon 2pm /to 4pm");
        assertEquals("Delicious progress! I've marked this task as done:\n  [X] return book",
                chocolate.getResponse("mark 2"));
        assertEquals("No worries! I've marked this task as not done yet:\n  [ ] return book",
                chocolate.getResponse("unmark 2"));
        assertEquals("Here are the matching tasks in your list:\n1.[D][ ] return book (by: Oct 15 2019)",
                chocolate.getResponse("find RETURN"));
        assertEquals("Poof! This task has melted away:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)"
                        + "\nYou now have 2 tasks left on your tray.",
                chocolate.getResponse("delete 3"));
        assertEquals("Thanks for visiting Chocolate's Cocoa Corner. See you soon!",
                chocolate.getResponse("bye"));
    }
}
