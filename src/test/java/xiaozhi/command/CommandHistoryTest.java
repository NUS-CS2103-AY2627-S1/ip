package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import xiaozhi.task.Todo;

/**
 * Tests {@link CommandHistory}'s push/pop bookkeeping, independently of any
 * particular {@link Command} it might hold.
 */
public class CommandHistoryTest {

    @Test
    public void isEmpty_newHistory_isTrue() {
        CommandHistory history = new CommandHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void push_oneCommand_isNoLongerEmpty() {
        CommandHistory history = new CommandHistory();

        history.push(new AddCommand(new Todo("read book")));

        assertFalse(history.isEmpty());
    }

    @Test
    public void pop_afterOnePush_returnsThatCommandAndBecomesEmpty() {
        CommandHistory history = new CommandHistory();
        AddCommand pushed = new AddCommand(new Todo("read book"));
        history.push(pushed);

        Command popped = history.pop();

        assertSame(pushed, popped);
        assertTrue(history.isEmpty());
    }

    @Test
    public void pop_afterTwoPushes_returnsMostRecentFirst() {
        CommandHistory history = new CommandHistory();
        AddCommand first = new AddCommand(new Todo("read book"));
        AddCommand second = new AddCommand(new Todo("write essay"));
        history.push(first);
        history.push(second);

        assertSame(second, history.pop());
        assertSame(first, history.pop());
        assertTrue(history.isEmpty());
    }
}
