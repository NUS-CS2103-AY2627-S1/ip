package serangooner.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class CommandTypeTest {
    @Test
    public void getKeyword_everyCommand_isUniqueSoParsingStaysUnambiguous() {
        Set<String> keywords = new HashSet<>();
        for (CommandType command : CommandType.values()) {
            assertTrue(keywords.add(command.getKeyword()),
                    "duplicate keyword: " + command.getKeyword());
        }
    }

    @Test
    public void getKeyword_everyCommand_isLowerCaseAndCarriesNoSpaces() {
        for (CommandType command : CommandType.values()) {
            String keyword = command.getKeyword();
            assertFalse(keyword.isBlank());
            assertEquals(keyword.toLowerCase(), keyword);
            assertFalse(keyword.contains(" "), keyword + " carries a space");
        }
    }

    @Test
    public void getSyntax_everyCommand_startsWithItsOwnKeyword() {
        for (CommandType command : CommandType.values()) {
            assertTrue(command.getSyntax().startsWith(command.getKeyword()),
                    command.getSyntax() + " does not start with " + command.getKeyword());
        }
    }

    @Test
    public void getDescription_everyCommand_saysSomething() {
        for (CommandType command : CommandType.values()) {
            assertFalse(command.getDescription().isBlank());
        }
    }

    @Test
    public void values_always_holdTheKeywordsTheParserAccepts() {
        assertEquals("todo", CommandType.TODO.getKeyword());
        assertEquals("bye", CommandType.BYE.getKeyword());
        assertEquals("unmark", CommandType.UNMARK.getKeyword());
    }
}
