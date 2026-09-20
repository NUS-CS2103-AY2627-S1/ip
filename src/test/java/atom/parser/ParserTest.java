package atom.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import atom.command.ByeCommand;
import atom.command.Command;
import atom.command.DeadlineCommand;
import atom.command.DeleteCommand;
import atom.command.EventCommand;
import atom.command.ListCommand;
import atom.command.MarkCommand;
import atom.command.ToDoCommand;
import atom.command.UnmarkCommand;
import atom.exception.AtomInvalidCommandException;
import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;

public class ParserTest {
    private final InputStream originalSystemIn = System.in;

    void setInput(String simulatedInput) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalSystemIn);
    }

    @Test
    public void readLine_bye_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("bye");

        assertEquals(new ByeCommand(new String[]{}), c);
    }

    @Test
    public void readLine_listExtraSpaces_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("   list   ");

        assertEquals(new ListCommand(new String[]{}), c);
    }

    @Test
    public void readLine_markExtraSpaces_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("mark      1");

        assertEquals(new MarkCommand(new String[]{"1"}), c);
    }

    @Test
    public void readLine_unmarkCapitalisedCommand_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("uNmaRk 10");

        assertEquals(new UnmarkCommand(new String[]{"10"}), c);
    }

    @Test
    public void readLine_delete_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("delete 64");

        assertEquals(new DeleteCommand(new String[]{"64"}), c);
    }

    @Test
    public void readLine_deleteInvalidType_exceptionThrown() {
        Parser p = new Parser();

        AtomInvalidTypeException exception = assertThrows(
                AtomInvalidTypeException.class, () -> {
                    p.readLine("delete task");
                }
        );

        assertEquals("Oh no! Invalid argument type for delete",
                exception.getMessage());
    }

    @Test
    public void readLine_todo_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("todo borrow book");

        assertEquals(new ToDoCommand(new String[]{"borrow book"}), c);
    }

    @Test
    public void readLine_deadline_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("deadline return book /by 2024-10-02 18:00");

        assertEquals(new DeadlineCommand(new String[]{"return book", "by 2024-10-02 18:00"}), c);
    }

    @Test
    public void readLine_event_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("event meeting /from 2024-10-02 14:00 /to 2024-10-02 16:00");

        assertEquals(new EventCommand(new String[]{"meeting", "from 2024-10-02 14:00", "to 2024-10-02 16:00"}), c);
    }

    @Test
    public void readLine_eventWithTime_writtenCorrectly() {
        Parser p = new Parser();
        Command c = p.readLine("event meeting /from 2024-10-02 14:00 /to 2024-10-02 16:00");

        assertEquals(new EventCommand(new String[]{"meeting", "from 2024-10-02 14:00", "to 2024-10-02 16:00"}), c);
    }

    @Test
    public void readLine_eventMismatchedArguments_exceptionThrown() {
        Parser p = new Parser();

        AtomMismatchedArgumentsException exception = assertThrows(
                AtomMismatchedArgumentsException.class, () -> {
                    p.readLine("event meeting /from 2pm");
                }
        );

        assertEquals("Oh no! event expects 3 arguments but got 2.",
                exception.getMessage());
    }

    @Test
    public void readLine_unknownCommand_exceptionThrown() {
        Parser p = new Parser();

        AtomInvalidCommandException exception = assertThrows(
                AtomInvalidCommandException.class, () -> {
                    p.readLine("hello adsasd");
                }
        );

        assertEquals("Oh no! I don't know what that means.",
                exception.getMessage());
    }

    @Test
    public void readLine_emptyInput_exceptionThrown() {
        Parser p = new Parser();

        AtomInvalidCommandException exception = assertThrows(
                AtomInvalidCommandException.class, () -> {
                    p.readLine("");
                }
        );

        assertEquals("Oh no! I don't know what that means.",
                exception.getMessage());
    }
}
