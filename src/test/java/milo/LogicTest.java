package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import milo.task.TaskList;
import milo.task.ToDo;

/** Tests that command processing returns display messages without using the user interface. */
public class LogicTest {

    /** Verifies that list and find commands return the existing numbered response messages. */
    @Test
    public void execute_listAndFindCommands_expectedDisplayMessages() {
        Logic logic = new Logic(new TaskList(List.of(new ToDo("read book"), new ToDo("return book"))));

        assertEquals("Here is your to-do list:\n"
                + "    1. [T] [ ] read book\n"
                + "    2. [T] [ ] return book", logic.execute("list"));
        assertEquals("Here are the tasks I found:\n    1. [T] [ ] read book", logic.execute("find read"));
    }

    /** Verifies that command errors and exit handling are returned to Milo for display. */
    @Test
    public void execute_invalidCommandsAndExit_expectedMessagesAndExitFlag() {
        Logic logic = new Logic(new TaskList());

        assertEquals("Give me a keyword and I'll do the digging!", logic.execute("find"));
        assertEquals("Bye bye. Hope to see you soon!", logic.execute("bye"));
        assertTrue(logic.isExitCommand("bye"));
        assertFalse(logic.isExitCommand("list"));
    }
}
