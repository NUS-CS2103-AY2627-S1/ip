package nova.parser;

import nova.exception.NovaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    public void getCommandWord_todoCommand_returnsTodo() {
        assertEquals(
                "todo",
                Parser.getCommandWord("todo read book")
        );
    }

    @Test
    public void getCommandWord_extraSpaces_returnsCommand() {
        assertEquals(
                "deadline",
                Parser.getCommandWord("   deadline return book")
        );
    }

    @Test
    public void getCommandWord_emptyInput_returnsEmptyString() {
        assertEquals(
                "",
                Parser.getCommandWord("   ")
        );
    }

    @Test
    public void parseTodoDescription_validTodo_returnsDescription()
            throws NovaException {

        assertEquals(
                "read book",
                Parser.parseTodoDescription("todo read book")
        );
    }

    @Test
    public void parseTodoDescription_missingDescription_throwsException() {

        assertThrows(
                NovaException.class,
                () -> Parser.parseTodoDescription("todo")
        );
    }

    @Test
    public void parseTaskNumber_validMark_returnsNumber()
            throws NovaException {

        assertEquals(
                3,
                Parser.parseTaskNumber("mark 3", 4)
        );
    }

    @Test
    public void parseTaskNumber_missingNumber_throwsException() {

        assertThrows(
                NovaException.class,
                () -> Parser.parseTaskNumber("mark", 4)
        );
    }

    @Test
    public void parseTaskNumber_notANumber_throwsException() {

        assertThrows(
                NovaException.class,
                () -> Parser.parseTaskNumber("mark abc", 4)
        );
    }

    @Test
    public void parseDateTime_validDate_returnsLocalDateTime()
            throws NovaException {

        LocalDateTime expected =
                LocalDateTime.of(2026, 8, 20, 18, 0);

        LocalDateTime actual =
                Parser.parseDateTime("20/8/2026 1800");

        assertEquals(expected, actual);
    }

    @Test
    public void parseDateTime_invalidDate_throwsException() {

        assertThrows(
                NovaException.class,
                () -> Parser.parseDateTime("tomorrow evening")
        );
    }
}