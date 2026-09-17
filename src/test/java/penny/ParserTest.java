package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ParserTest {

    private TaskList tasklist;

    @BeforeEach
    void setUp() {
        tasklist = new TaskList();
    }

    @Test
    @DisplayName("Throws PennyException when command is unknown")
    void handleInput_unknownCommand_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "foobar", false);
        });

        assertEquals("I don't think I understand.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when LIST is called on an empty list")
    void handleInput_listEmptyTasks_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "list", false);
        });

        assertEquals("There are no tasks on your list", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when DUE argument is blank")
    void handleInput_dueBlankArgument_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "due   ", false);
        });

        assertEquals("Due date cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when MARK receives a non-integer argument")
    void handleInput_markNonInteger_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "mark abc", false);
        });

        assertEquals("Mark expects a number", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when MARK index is out of bounds")
    void handleInput_markOutOfBounds_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "mark 1", false);
        });

        assertEquals("Mark out of bounds", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when UNMARK receives a non-integer argument")
    void handleInput_unmarkNonInteger_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "unmark xyz", false);
        });

        assertEquals("Unmark expects a number", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when UNMARK index is negative or out of bounds")
    void handleInput_unmarkOutOfBounds_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "unmark 0", false);
        });

        assertEquals("Unmark out of bounds", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when DELETE receives a non-integer argument")
    void handleInput_deleteNonInteger_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "delete two", false);
        });

        assertEquals("Delete expects a number", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when DELETE index is out of bounds")
    void handleInput_deleteOutOfBounds_throwsPennyException() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "delete 5", false);
        });

        assertEquals("Delete out of bounds", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException when adding a task with a description already on the list")
    void handleInput_duplicateDescription_throwsPennyException() throws PennyException {
        Parser.handleInput(tasklist, "todo read book", false);

        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "todo read book", false);
        });

        assertEquals("This task is already on your list.", exception.getMessage());
    }

    @Test
    @DisplayName("Throws PennyException for a duplicate description of a different task type and date")
    void handleInput_duplicateDescriptionDifferentDate_throwsPennyException() throws PennyException {
        Parser.handleInput(tasklist, "deadline submit report /by 20122026 1800", false);

        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.handleInput(tasklist, "event submit report /from 25122026 0900 /to 25122026 1000", false);
        });

        assertEquals("This task is already on your list.", exception.getMessage());
    }
}
