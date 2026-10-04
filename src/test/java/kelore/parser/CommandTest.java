package kelore.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command-word matching and argument rules of {@link Command}. */
public class CommandTest {
    @Test
    public void matches_argumentlessCommand_matchesOnlyExactWord() {
        assertTrue(Command.BYE.matches("bye"));
        assertTrue(Command.LIST.matches("list"));
        assertFalse(Command.BYE.matches("bye now"));
        assertFalse(Command.LIST.matches("list "));
    }

    @Test
    public void matches_argumentCommand_matchesWordWithOrWithoutArgument() {
        assertTrue(Command.MARK.matches("mark"));
        assertTrue(Command.MARK.matches("mark 1"));
        assertTrue(Command.FIND.matches("find two words"));
        assertFalse(Command.MARK.matches("marked 1"));
        assertFalse(Command.FIND.matches("findings"));
    }

    @Test
    public void matches_differentCaseOrLeadingWhitespace_returnsFalse() {
        assertFalse(Command.TODO.matches("Todo read"));
        assertFalse(Command.TODO.matches(" todo read"));
    }
}
