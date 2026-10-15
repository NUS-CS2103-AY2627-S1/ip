package yourhand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import yourhand.storage.Storage;
import yourhand.tasks.TaskList;

/** Tests command execution independently of the graphical user interface. */
class YourHandEngineTest {
    @Test
    public void execute_addTask_returnsConfirmation() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("todo borrow book");

        assertTrue(response.contains("I'll note this down for you haiz"));
        assertTrue(response.contains("borrow book"));
    }

    @Test
    public void execute_findTask_returnsMatchingTask() {
        YourHandEngine engine = createEngine();
        engine.execute("todo borrow book");
        engine.execute("todo buy milk");

        String response = engine.execute("find BOOK");

        assertTrue(response.contains("borrow book"));
        assertFalse(response.contains("buy milk"));
    }

    @Test
    public void execute_invalidCommand_returnsErrorMessage() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("hello");

        assertTrue(response.contains("I don't speak that yet"));
    }

    @Test
    public void executeWithResult_invalidCommand_reportsFailure() {
        YourHandEngine engine = createEngine();

        ExecutionResult result = engine.executeWithResult("todo  read book");

        assertFalse(result.successful());
        assertTrue(result.message().contains("only one space"));
    }

    @Test
    public void executeWithResult_validCommand_reportsSuccess() {
        YourHandEngine engine = createEngine();

        ExecutionResult result = engine.executeWithResult("todo read book");

        assertTrue(result.successful());
        assertTrue(result.message().contains("I'll note this down for you haiz"));
    }

    @Test
    public void executeWithResult_impossibleDate_reportsCalendarError() {
        YourHandEngine engine = createEngine();

        ExecutionResult result = engine.executeWithResult("deadline report /by 2026-02-30");

        assertFalse(result.successful());
        assertTrue(result.message().contains("Date [2026-02-30]"));
    }

    @Test
    public void executeWithResult_outOfRangeTime_reportsTimeError() {
        YourHandEngine engine = createEngine();

        ExecutionResult result = engine.executeWithResult("deadline report /by 2026-09-10 25:00");

        assertFalse(result.successful());
        assertTrue(result.message().contains("Time [25:00]"));
    }

    @Test
    public void execute_help_returnsCommandGuide() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("help");

        assertTrue(response.contains("what I can help you keep in hand"));
        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("view schedule DATE"));
        assertTrue(response.contains("bye"));
    }

    @Test
    public void execute_viewSchedule_returnsTasksForDate() {
        YourHandEngine engine = createEngine();
        engine.execute("deadline submit report /by 2026-09-10");

        String response = engine.execute("view schedule 2026-09-10");

        assertTrue(response.contains("Schedule for 2026-09-10"));
        assertTrue(response.contains("submit report"));
    }

    @Test
    public void execute_viewScheduleWithoutDate_returnsHelpfulError() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("view schedule");

        assertTrue(response.contains("which date to view"));
    }

    @Test
    public void execute_leadingSpace_returnsFormatError() {
        YourHandEngine engine = createEngine();

        String response = engine.execute(" todo read book");

        assertTrue(response.contains("remove spaces before or after"));
    }

    @Test
    public void execute_multipleSpaces_returnsFormatError() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("todo  read book");

        assertTrue(response.contains("only one space"));
    }

    @Test
    public void execute_equalEventDates_returnsHelpfulError() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("event meeting /from 2026-09-10 /to 2026-09-10");

        assertTrue(response.contains("must end after it starts"));
    }

    @Test
    public void execute_invalidDate_returnsHelpfulError() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("deadline report /by 2026-02-30");

        assertTrue(response.contains("Date [2026-02-30]"));
    }

    private YourHandEngine createEngine() {
        Path testFile = Path.of("build", "test-data", "engine-test.txt");
        try {
            Files.deleteIfExists(testFile);
        } catch (IOException exception) {
            throw new AssertionError("Unable to reset engine test storage", exception);
        }
        return new YourHandEngine(new Storage(testFile), new TaskList());
    }
}
