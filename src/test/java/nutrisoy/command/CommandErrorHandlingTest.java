package nutrisoy.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nutrisoy.exception.DukeException;
import nutrisoy.parser.Parser;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

public class CommandErrorHandlingTest {
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        ui.startCapturingOutput();
    }

    @Test
    public void parse_irregularWhitespaceAccepted_extraArgumentsRejected() {
        assertDoesNotThrow(() -> execute("  todo\tread    book  "));
        assertThrows(DukeException.class, () -> Parser.parse("list extra"));
        assertThrows(DukeException.class, () -> Parser.parse("bye now"));
        assertThrows(DukeException.class, () -> Parser.parse(null));
    }

    @Test
    public void addTask_duplicateOrReservedDescription_exceptionThrown() throws DukeException {
        execute("todo Read Book");

        DukeException duplicateError = assertThrows(DukeException.class, () -> execute("todo read   book"));
        assertTrue(duplicateError.getMessage().contains("already exists"));
        assertThrows(DukeException.class, () -> execute("todo break | storage"));
    }

    @Test
    public void datedTask_invalidParametersOrDates_exceptionThrown() {
        String repeatedDeadlineParameter = "deadline submit /by 2026-09-20 /by 2026-09-21";
        String invalidDeadlineDate = "deadline submit /by 2026-02-30";
        String reversedParameters = "event trip /to 2026-09-23 /from 2026-09-21";
        String reversedDates = "event trip /from 2026-09-23 /to 2026-09-21";
        String matchingDates = "event trip /from 2026-09-21 /to 2026-09-21";

        assertThrows(DukeException.class, () -> execute(repeatedDeadlineParameter));
        assertThrows(DukeException.class, () -> execute(invalidDeadlineDate));
        assertThrows(DukeException.class, () -> execute(reversedParameters));
        assertThrows(DukeException.class, () -> execute(reversedDates));
        assertThrows(DukeException.class, () -> execute(matchingDates));
    }

    @Test
    public void tag_invalidDuplicateOrMissingTag_exceptionThrown() throws DukeException {
        execute("todo read book");
        execute("tag 1 school");

        assertThrows(DukeException.class, () -> execute("tag 1 SCHOOL"));
        assertThrows(DukeException.class, () -> execute("tag 1 bad#tag"));
        assertThrows(DukeException.class, () -> execute("untag 1 missing"));
        assertDoesNotThrow(() -> execute("untag 1 SCHOOL"));
    }

    @Test
    public void changeCompletionState_repeatedStateChange_exceptionThrown() throws DukeException {
        execute("todo read book");

        assertThrows(DukeException.class, () -> execute("unmark 1"));
        execute("mark 1");
        assertThrows(DukeException.class, () -> execute("mark 1"));
        execute("unmark 1");
        assertThrows(DukeException.class, () -> execute("unmark 1"));
    }

    private void execute(String input) throws DukeException {
        Parser.parse(input).execute(tasks, ui);
    }
}
