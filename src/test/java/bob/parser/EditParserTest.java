package bob.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.BobException;
import bob.storage.Storage;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskDateTime;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Tests {@link EditParser}, which reads the task number and changes of an edit.
 *
 * <p>The {@link bob.command.EditCommand EditCommand} it returns keeps the changes to
 * itself, so what an edit was read as is checked by running the command against a
 * real {@link TaskList} and looking at the task it left behind.
 */
public class EditParserTest {

    /** A folder for the save file the commands write to, thrown away after each test. */
    @TempDir
    private Path tempDirectory;

    @Test
    public void parseEdit_noTaskNumber_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () -> EditParser.parseEdit(""));

        assertTrue(exception.getMessage().contains("Which task should I edit?"));
    }

    @Test
    public void parseEdit_taskNumberNotANumber_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () ->
                EditParser.parseEdit("two /desc read book"));

        // Only the word where the number should be is quoted back, not the changes after it.
        assertTrue(exception.getMessage().startsWith("\"two\" isn't a task number."));
    }

    @Test
    public void parseEdit_noChanges_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () -> EditParser.parseEdit("2"));

        assertEquals("What should I change about task 2?\nUse /desc, /by, /from or /to."
                + "\nLike dis: edit 2 /desc read book", exception.getMessage());
    }

    @Test
    public void parseEdit_textBeforeTheFirstMarker_exceptionThrown() {
        // Refused rather than taken as a new description, which an edit is given
        // only through /desc.
        BobException exception = assertThrows(BobException.class, () ->
                EditParser.parseEdit("2 return book /by 2026-12-05"));

        assertTrue(exception.getMessage().startsWith("What should I change about task 2?"));
    }

    @Test
    public void parseEdit_markerWithNothingAfterIt_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () -> EditParser.parseEdit("2 /by"));

        assertEquals("You wrote /by but nothing after it.", exception.getMessage());
        // A marker followed straight away by another has nothing after it either.
        assertThrows(BobException.class, () -> EditParser.parseEdit("2 /from /to 2026-12-03"));
        assertThrows(BobException.class, () -> EditParser.parseEdit("2 /desc   "));
    }

    @Test
    public void parseEdit_dateThatIsNotADate_exceptionThrown() {
        assertThrows(BobException.class, () -> EditParser.parseEdit("2 /by someday"));
        assertThrows(BobException.class, () -> EditParser.parseEdit("2 /to 2026-02-30"));
    }

    @Test
    public void parseEdit_numberOutsideTheList_accepted() {
        // As with mark and delete, whether a task has that number is the list's question.
        assertDoesNotThrow(() -> EditParser.parseEdit("0 /desc read book"));
        assertDoesNotThrow(() -> EditParser.parseEdit("99 /by 2026-12-02"));
    }

    @Test
    public void parseEdit_newDescription_onlyDescriptionChanged() throws BobException {
        Task task = taskAfterEdit(new Deadline("return book", TaskDateTime.parse("2026-12-02 1800")),
                "1 /desc return library book");

        assertEquals("[D][ ] return library book (by: Dec 02 2026 18:00)", task.toString());
    }

    @Test
    public void parseEdit_newDueDate_onlyDueDateChanged() throws BobException {
        Task task = taskAfterEdit(new Deadline("return book", TaskDateTime.parse("2026-12-02 1800")),
                "1 /by 2026-12-05");

        assertEquals("[D][ ] return book (by: Dec 05 2026)", task.toString());
    }

    @Test
    public void parseEdit_oneEndOfAnEvent_otherEndKept() throws BobException {
        Task task = taskAfterEdit(meetingFrom1800To2000(), "1 /to 2026-12-02 2100");

        assertEquals("[E][ ] meeting (from: Dec 02 2026 18:00 to: Dec 02 2026 21:00)", task.toString());
    }

    @Test
    public void parseEdit_markersInAnyOrder_eachValueEndsAtTheNextMarker() throws BobException {
        Task task = taskAfterEdit(meetingFrom1800To2000(), "1 /to 2026-12-03 /desc trip /from 2026-12-01");

        assertEquals("[E][ ] trip (from: Dec 01 2026 to: Dec 03 2026)", task.toString());
    }

    @Test
    public void parseEdit_markerTextInsideAWord_keptInTheValue() throws BobException {
        // /tokyo is not /to, so all of it is the new description.
        Task task = taskAfterEdit(new Deadline("return book", TaskDateTime.parse("2026-12-02 1800")),
                "1 /desc fly /tokyo");

        assertEquals("[D][ ] fly /tokyo (by: Dec 02 2026 18:00)", task.toString());
    }

    @Test
    public void parseEdit_markerWrittenTwice_exceptionThrown() {
        // Neither description is kept, since keeping either would be a guess.
        BobException exception = assertThrows(BobException.class, () ->
                EditParser.parseEdit("1 /desc read book /desc read novel"));

        assertEquals("You wrote /desc twice. Give just one.", exception.getMessage());
    }

    @Test
    public void parseEdit_markerInCapitals_exceptionSuggestsLowercase() {
        BobException exception = assertThrows(BobException.class, () ->
                EditParser.parseEdit("1 /DESC read book"));

        assertEquals("Markers are lowercase. Did yu mean /desc?", exception.getMessage());
    }

    /** Returns an event called {@code meeting} running from 18:00 to 20:00 on one day. */
    private static Event meetingFrom1800To2000() throws BobException {
        return new Event("meeting", TaskDateTime.parse("2026-12-02 1800"),
                TaskDateTime.parse("2026-12-02 2000"));
    }

    /**
     * Returns the task left by running the edit that {@code arguments} parse to
     * against a task list holding only {@code task}.
     */
    private Task taskAfterEdit(Task task, String arguments) throws BobException {
        TaskList tasks = new TaskList();
        tasks.add(task);
        // The collecting Ui, so that running the command leaves the test session's
        // own output alone. What it says is not what is being tested here.
        Storage storage = new Storage(tempDirectory.resolve("bob.txt"));
        EditParser.parseEdit(arguments).execute(tasks, Ui.forGui(), storage);
        return tasks.get(0);
    }
}
