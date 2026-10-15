package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import happybot.task.ToDo;

public class ParserTextTest {
    private static final String FIND_USAGE = "Use: find <keyword>, such as find book.";

    private static final String PIPE_MESSAGE = "A task cannot contain the '|' character.";

    // ==================== parseToDo ====================

    @Test
    public void parseToDo_description_todoCreated() throws HappyBotException {
        ToDo todo = Parser.parseToDo("read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseToDo_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        assertEquals("read book", Parser.parseToDo("   read book   ").getDescription());
    }

    @Test
    public void parseToDo_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo(""));

        assertEquals("The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseToDo_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo("    "));

        assertEquals("The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseToDo_pipeInDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo("read book | CD"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    // ==================== parseKeyword ====================

    @Test
    public void parseKeyword_word_keywordReturned() throws HappyBotException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_spacesAroundKeyword_keywordTrimmed() throws HappyBotException {
        assertEquals("book", Parser.parseKeyword("   book   "));
    }

    @Test
    public void parseKeyword_severalWords_wholeTextReturned() throws HappyBotException {
        // The whole text is searched for, so a keyword may hold a space.
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseKeyword(""));

        assertEquals(FIND_USAGE, e.getMessage());
    }

    @Test
    public void parseKeyword_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseKeyword("    "));

        assertEquals(FIND_USAGE, e.getMessage());
    }
}
