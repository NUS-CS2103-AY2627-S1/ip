package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datahandler.DataHandler;
import exception.FriedbergException;
import exception.FriedbergUserInputException;
import task.ToDo;

/**
 * Characterizes event execution so parsing can be extracted without changing behavior.
 */
class EventCommandTest {
    @TempDir
    Path tempDir;

    private DataHandler dataHandler;
    private CommandContext context;
    private final EventCommand command = new EventCommand();

    @BeforeEach
    void setUp() throws Exception {
        dataHandler = new DataHandler(tempDir, tempDir.resolve("tasks.txt"));
        context = new CommandContext(dataHandler);
        context.addTask(new ToDo("existing task"));
    }

    @Test
    void execute_validEvent_addsAndPersistsOnce() throws Exception {
        String response = command.execute("event meeting /from 2026-09-09 /to 2026-09-10", context);

        assertEquals("Got it. I've added this task:\n"
                + "[E][ ] meeting (from: 09/09/2026 (Wed) to: 10/09/2026 (Thu))\n"
                + "Now you have 2 tasks in the list.", response);
        assertEquals(2, context.getTasksSize());
        assertEquals("existing task,P,T\nmeeting,P,E,2026-09-09,2026-09-10", dataHandler.read());
        assertEquals(context.renderTasks(), new CommandContext(dataHandler).renderTasks());
    }

    @Test
    void execute_missingFrom_preservesDelimiterError() throws Exception {
        assertRejectedWithoutChanges("event meeting /to 2026-09-10",
                new FriedbergUserInputException("event task expected to have /from"));
    }

    @Test
    void execute_missingTo_preservesDelimiterError() throws Exception {
        assertRejectedWithoutChanges("event meeting /from 2026-09-09",
                new FriedbergUserInputException("event task expected to have /to"));
    }

    @Test
    void execute_invalidStartDate_returnsSpecificDateError() throws Exception {
        assertRejectedWithoutChanges("event meeting /from invalid /to 2026-09-10",
                new FriedbergUserInputException("Date for /from must use yyyy-MM-dd, e.g. 2026-09-10"));
    }

    @Test
    void execute_invalidEndDate_returnsSpecificDateError() throws Exception {
        assertRejectedWithoutChanges("event meeting /from 2026-09-09 /to invalid",
                new FriedbergUserInputException("Date for /to must use yyyy-MM-dd, e.g. 2026-09-10"));
    }

    @Test
    void execute_duplicateFrom_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges(
                "event meeting /from 2026-09-09 /from 2026-09-10 /to 2026-09-11",
                new FriedbergUserInputException("Parameter /from must be specified exactly once"));
    }

    @Test
    void execute_duplicateTo_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges(
                "event meeting /from 2026-09-09 /to 2026-09-10 /to 2026-09-11",
                new FriedbergUserInputException("Parameter /to must be specified exactly once"));
    }

    @Test
    void execute_parametersInWrongOrder_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges("event meeting /to 2026-09-10 /from 2026-09-09",
                new FriedbergUserInputException("Parameter /from must appear before /to"));
    }

    @Test
    void execute_nonexistentDate_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges("event meeting /from 2026-02-30 /to 2026-03-01",
                new FriedbergUserInputException("Date for /from does not exist: 2026-02-30"));
    }

    @Test
    void execute_startAfterEnd_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges("event meeting /from 2026-09-11 /to 2026-09-10",
                new FriedbergUserInputException("Event start date must be before its end date"));
    }

    @Test
    void execute_equalDates_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges("event meeting /from 2026-09-10 /to 2026-09-10",
                new FriedbergUserInputException("Event start date must be before its end date"));
    }

    @Test
    void execute_repeatedSpaces_rejectsWithoutChanges() throws Exception {
        assertRejectedWithoutChanges("event  meeting /from 2026-09-09 /to 2026-09-10",
                new FriedbergUserInputException("Use exactly one space between command parts"));
    }

    /**
     * Checks that parsing failure leaves both tasks and saved data unchanged.
     *
     * @param input malformed event input
     * @param expectedError expected exception type and message
     * @throws Exception if temporary storage cannot be read
     */
    private void assertRejectedWithoutChanges(String input, FriedbergException expectedError) throws Exception {
        String originalTasks = context.renderTasks();
        String originalData = dataHandler.read();

        FriedbergException actualError = assertThrows(expectedError.getClass(), () -> command.execute(input, context));

        assertEquals(expectedError.getMessage(), actualError.getMessage());
        assertEquals(originalTasks, context.renderTasks());
        assertEquals(originalData, dataHandler.read());
    }
}
