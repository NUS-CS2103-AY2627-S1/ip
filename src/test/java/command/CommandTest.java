package command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the regex-based Command matcher.
 */
public class CommandTest {
    @Test
    public void find_matchesWhenPatternPresentFromStart() {
        Command command = new Command("\\Alist\\s*\\z");
        assertTrue(command.find(0, "list"));
    }

    @Test
    public void find_matchesWithTrailingWhitespace() {
        Command command = new Command("\\Alist\\s*\\z");
        assertTrue(command.find(0, "list   "));
    }

    @Test
    public void find_doesNotMatchDifferentText() {
        Command command = new Command("\\Alist\\s*\\z");
        assertFalse(command.find(0, "listing"));
    }

    @Test
    public void find_respectsStartIndex() {
        Command command = new Command("bar");
        assertFalse(command.find(4, "foobar"));
        assertTrue(command.find(3, "foobar"));
    }

    @Test
    public void find_returnsFalseWhenPatternAbsent() {
        Command command = new Command("xyz");
        assertFalse(command.find(0, "no match here"));
    }
}
