package hermes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hermes.HermesException;

/**
 * Tests the operations on the task list, what each reports, and that every
 * change reaches the data file.
 *
 * <p>Dates are kept in August so the month reads "Aug" whatever the locale of the
 * machine running the tests.
 */
public class LogBookTest {

    /** The moment the due-by cases measure against. Fixed, so the tests do not age. */
    private static final LocalDateTime CUTOFF = LocalDateTime.of(2026, 8, 30, 12, 0);

    /** Separates the heading of a sort or undo reply from the list below it. */
    private static final String LINE_BREAK = System.lineSeparator();

    /**
     * A directory JUnit creates and deletes per test. Constructing a LogBook
     * needs a Storage, and adding tasks writes through it, so the tests are
     * pointed at a throwaway file rather than the real data/Hermes.txt.
     */
    @TempDir
    Path tempDir;

    private Path dataFile;
    private LogBook logBook;

    @BeforeEach
    public void setUp() {
        dataFile = tempDir.resolve("Hermes.txt");
        logBook = new LogBook(new Storage(dataFile.toString()));
    }

    /** Adds a todo for each description, in order. */
    private void logTodos(String... descriptions) throws HermesException {
        for (String description : descriptions) {
            logBook.log(new ToDo(description));
        }
    }

    /** Returns what a fresh LogBook reads back from the same data file. */
    private String reloaded() {
        return new LogBook(new Storage(dataFile.toString())).listTasks();
    }

    // ---- log ----

    @Test
    public void log_firstTask_confirmsAndCountsInSingular() throws HermesException {
        assertEquals("""
                It is recorded. I have set down this task:
                  [T][ ] read
                Thy scroll now holds 1 task.
                """, logBook.log(new ToDo("read")));
    }

    @Test
    public void log_secondTask_countsInPlural() throws HermesException {
        logTodos("read");

        assertEquals("""
                It is recorded. I have set down this task:
                  [T][ ] write
                Thy scroll now holds 2 tasks.
                """, logBook.log(new ToDo("write")));
    }

    @Test
    public void log_taskAlreadyListed_addedWithAWarningFirst() throws HermesException {
        logTodos("read");

        assertEquals("""
                Take heed: this task already stands upon thy scroll.
                It is recorded. I have set down this task:
                  [T][ ] read
                Thy scroll now holds 2 tasks.
                """, logBook.log(new ToDo("read")));
        assertEquals("1. [T][ ] read\n2. [T][ ] read\n", logBook.listTasks());
    }

    @Test
    public void log_taskAlreadyListedButCompleted_stillWarns() throws HermesException {
        logTodos("read");
        logBook.mark(0);

        assertEquals("""
                Take heed: this task already stands upon thy scroll.
                It is recorded. I have set down this task:
                  [T][ ] read
                Thy scroll now holds 2 tasks.
                """, logBook.log(new ToDo("read")));
    }

    @Test
    public void log_sameDescriptionButDifferentDate_noWarning() throws HermesException {
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 27, 15, 0)));

        assertEquals("""
                It is recorded. I have set down this task:
                  [D][ ] essay (by: 28/08/2026 1500)
                Thy scroll now holds 2 tasks.
                """, logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 28, 15, 0))));
    }

    @Test
    public void log_task_savedToDataFile() throws HermesException, IOException {
        logTodos("read");

        assertEquals(List.of("T | 0 | read"), Files.readAllLines(dataFile));
    }

    @Test
    public void log_dataFileCannotBeWritten_throwsButKeepsTaskForTheSession() throws IOException {
        // A directory where the data file should be makes every save fail.
        Files.createDirectory(dataFile);

        HermesException exception = assertThrows(HermesException.class, () -> logBook.log(new ToDo("read")));

        assertEquals("Alas, I could not write to " + dataFile + ".\n"
                + "Thy change holds for now, but it shall be lost\n"
                + "when I depart.\n", exception.getMessage());
        assertEquals("1. [T][ ] read\n", logBook.listTasks());
    }

    // ---- mark ----

    @Test
    public void mark_oneTask_declaresItFulfilled() throws HermesException {
        logTodos("read");

        assertEquals("""
                Well done. I declare the following task as fulfilled:
                  [T][X] read
                """, logBook.mark(0));
    }

    @Test
    public void mark_severalTasks_declaresThemFulfilledTogether() throws HermesException {
        logTodos("read", "write", "rest");

        assertEquals("""
                Well done. I declare the following tasks as fulfilled:
                  [T][X] read
                  [T][X] rest
                """, logBook.mark(0, 2));
        assertEquals("1. [T][X] read\n2. [T][ ] write\n3. [T][X] rest\n", logBook.listTasks());
    }

    @Test
    public void mark_taskAlreadyFulfilled_reportedSeparately() throws HermesException {
        logTodos("read", "write");
        logBook.mark(0, 1);

        assertEquals("""
                The following tasks were already fulfilled:
                  [T][X] read
                  [T][X] write
                """, logBook.mark(0, 1));
    }

    @Test
    public void mark_someAlreadyFulfilled_reportsBothGroups() throws HermesException {
        logTodos("read", "write");
        logBook.mark(0);

        assertEquals("""
                Well done. I declare the following task as fulfilled:
                  [T][X] write

                The following task was already fulfilled:
                  [T][X] read
                """, logBook.mark(0, 1));
    }

    @Test
    public void mark_oneNumberNamesNoTask_throwsAndMarksNothing() throws HermesException, IOException {
        logTodos("read");

        HermesException exception = assertThrows(HermesException.class, () -> logBook.mark(0, 5));

        assertEquals("Alas, no task numbered 6 is to be found.", exception.getMessage());
        assertEquals("1. [T][ ] read\n", logBook.listTasks());
        assertEquals(List.of("T | 0 | read"), Files.readAllLines(dataFile));
    }

    @Test
    public void mark_task_savedToDataFile() throws HermesException {
        logTodos("read");
        logBook.mark(0);

        assertEquals("1. [T][X] read\n", reloaded());
    }

    // ---- unmark ----

    @Test
    public void unmark_oneTask_declaresItUnfulfilled() throws HermesException {
        logTodos("read");
        logBook.mark(0);

        assertEquals("""
                As thou wishest. I declare the following task as unfulfilled:
                  [T][ ] read
                """, logBook.unmark(0));
    }

    @Test
    public void unmark_severalTasks_declaresThemUnfulfilledTogether() throws HermesException {
        logTodos("read", "write");
        logBook.mark(0, 1);

        assertEquals("""
                As thou wishest. I declare the following tasks as unfulfilled:
                  [T][ ] read
                  [T][ ] write
                """, logBook.unmark(0, 1));
    }

    @Test
    public void unmark_taskAlreadyUnfulfilled_reportedSeparately() throws HermesException {
        logTodos("read");

        assertEquals("""
                The following task was already unfulfilled:
                  [T][ ] read
                """, logBook.unmark(0));
    }

    @Test
    public void unmark_someAlreadyUnfulfilled_reportsBothGroups() throws HermesException {
        logTodos("read", "write", "rest");
        logBook.mark(0);

        assertEquals("""
                As thou wishest. I declare the following task as unfulfilled:
                  [T][ ] read

                The following tasks were already unfulfilled:
                  [T][ ] write
                  [T][ ] rest
                """, logBook.unmark(0, 1, 2));
    }

    @Test
    public void unmark_oneNumberNamesNoTask_throwsAndUnmarksNothing() throws HermesException {
        logTodos("read");
        logBook.mark(0);

        HermesException exception = assertThrows(HermesException.class, () -> logBook.unmark(0, -1));

        assertEquals("Alas, no task numbered 0 is to be found.", exception.getMessage());
        assertEquals("1. [T][X] read\n", logBook.listTasks());
    }

    // ---- delete ----

    @Test
    public void delete_oneTask_removesItAndCountsWhatRemains() throws HermesException {
        logTodos("read", "write");

        assertEquals("""
                It is done. I have guided this task down to the Underworld:
                  [T][ ] read
                Thy scroll now holds 1 task.
                """, logBook.delete(0));
        assertEquals("1. [T][ ] write\n", reloaded());
    }

    @Test
    public void delete_severalTasksOutOfOrder_removesEachNamedTask() throws HermesException {
        logTodos("read", "write", "rest");

        assertEquals("""
                It is done. I have guided these tasks down to the Underworld:
                  [T][ ] read
                  [T][ ] rest
                Thy scroll now holds 1 task.
                """, logBook.delete(2, 0));
        assertEquals("1. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void delete_lastRemainingTasks_countsNoneInPlural() throws HermesException {
        logTodos("read", "write");

        assertEquals("""
                It is done. I have guided these tasks down to the Underworld:
                  [T][ ] read
                  [T][ ] write
                Thy scroll now holds 0 tasks.
                """, logBook.delete(0, 1));
    }

    @Test
    public void delete_oneNumberNamesNoTask_throwsAndRemovesNothing() throws HermesException, IOException {
        logTodos("read", "write");

        HermesException exception = assertThrows(HermesException.class, () -> logBook.delete(0, 98));

        assertEquals("Alas, no task numbered 99 is to be found.", exception.getMessage());
        assertEquals("1. [T][ ] read\n2. [T][ ] write\n", logBook.listTasks());
        assertEquals(List.of("T | 0 | read", "T | 0 | write"), Files.readAllLines(dataFile));
    }

    // ---- listTasks ----

    @Test
    public void listTasks_noTasks_explainsHowToBegin() {
        assertEquals("Thy scroll is blank. Begin it with a todo, deadline or event, "
                + "for instance: todo borrow book", logBook.listTasks());
    }

    @Test
    public void listTasks_someTasks_numbersThemFromOne() throws HermesException {
        logTodos("read", "write");

        assertEquals("1. [T][ ] read\n2. [T][ ] write\n", logBook.listTasks());
    }

    // ---- listTasksDueBy ----

    @Test
    public void listTasksDueBy_taskDueBeforeCutoff_taskListed() throws HermesException {
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 29, 9, 0)));

        assertEquals("[D][ ] essay (by: 29/08/2026 0900)", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_taskDueAfterCutoff_taskExcluded() throws HermesException {
        logBook.log(new Deadline("far future", LocalDateTime.of(2026, 12, 25, 9, 0)));

        assertEquals("Nothing is due by 30/08/2026 1200.", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_taskDueExactlyAtCutoff_taskListed() throws HermesException {
        // The cutoff is inclusive: a task due at the very moment asked about counts.
        logBook.log(new Deadline("on the dot", CUTOFF));

        assertEquals("[D][ ] on the dot (by: 30/08/2026 1200)", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_completedTask_taskExcluded() throws HermesException {
        logBook.log(new Deadline("already done", LocalDateTime.of(2026, 8, 26, 9, 0)));
        logBook.mark(0);

        assertEquals("Nothing is due by 30/08/2026 1200.", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_todoWithoutDate_taskExcluded() throws HermesException {
        // A todo has no date, so it can never be due by anything.
        logBook.log(new ToDo("borrow book"));

        assertEquals("Nothing is due by 30/08/2026 1200.", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_severalTasksDue_listedSoonestFirst() throws HermesException {
        // Added out of order, and an event is measured by its start time.
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 29, 9, 0)));
        logBook.log(new Deadline("old", LocalDateTime.of(2026, 8, 25, 8, 0)));
        logBook.log(new Event("meeting",
                LocalDateTime.of(2026, 8, 26, 10, 0), LocalDateTime.of(2026, 8, 26, 11, 0)));

        assertEquals("""
                [D][ ] old (by: 25/08/2026 0800)
                [E][ ] meeting (from: 26/08/2026 1000 to: 26/08/2026 1100)
                [D][ ] essay (by: 29/08/2026 0900)""",
                logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_emptyLogBook_noticeReturned() {
        assertEquals("Nothing is due by 30/08/2026 1200.", logBook.listTasksDueBy(CUTOFF));
    }

    @Test
    public void listTasksDueBy_queryRun_listOrderUnchanged() throws HermesException {
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 29, 9, 0)));
        logBook.log(new Deadline("old", LocalDateTime.of(2026, 8, 25, 8, 0)));

        logBook.listTasksDueBy(CUTOFF);

        // The query sorts inside its stream, so the numbering shown by list is
        // still insertion order afterwards.
        assertEquals("""
                1. [D][ ] essay (by: 29/08/2026 0900)
                2. [D][ ] old (by: 25/08/2026 0800)
                """, logBook.toString());
    }

    // ---- sort ----

    @Test
    public void sort_noTasks_saysThereIsNothingToOrder() throws HermesException {
        assertEquals("There is naught to set in order; thy scroll is empty.", logBook.sort());
    }

    @Test
    public void sort_mixedTasks_datedSoonestFirstThenUndatedThenCompleted() throws HermesException {
        logBook.log(new ToDo("read"));
        logBook.log(new Deadline(true, "handed in", "2026-08-01T09:00"));
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 29, 9, 0)));
        logBook.log(new Event("meeting",
                LocalDateTime.of(2026, 8, 26, 10, 0), LocalDateTime.of(2026, 8, 26, 11, 0)));

        String sortedList = """
                1. [E][ ] meeting (from: 26/08/2026 1000 to: 26/08/2026 1100)
                2. [D][ ] essay (by: 29/08/2026 0900)
                3. [T][ ] read
                4. [D][X] handed in (by: 01/08/2026 0900)
                """;
        assertEquals("Swift as my winged sandals, I have ordered thy tasks, soonest first:"
                + LINE_BREAK + sortedList, logBook.sort());
        assertEquals(sortedList, reloaded());
    }

    // ---- findTasks ----

    @Test
    public void findTasks_noTasks_saysThereIsNothingToSearch() {
        assertEquals("There is naught to search; thy scroll is empty.", logBook.findTasks("book"));
    }

    @Test
    public void findTasks_keywordInDescriptions_listsEveryMatchIgnoringCase() throws HermesException {
        logTodos("Borrow BOOK", "write", "return book");

        assertEquals("[T][ ] Borrow BOOK\n[T][ ] return book", logBook.findTasks("book"));
    }

    @Test
    public void findTasks_noDescriptionMatches_namesTheKeyword() throws HermesException {
        logTodos("read");

        assertEquals("I have searched from Olympus to the Underworld, yet no task speaks of 'zebra'.",
                logBook.findTasks("zebra"));
    }

    // ---- undo ----

    @Test
    public void undo_nothingChangedYet_saysThereIsNothingToUndo() throws HermesException {
        assertEquals("There is naught to undo.", logBook.undo());
    }

    @Test
    public void undo_afterMark_restoresUnmarkedTaskAndSavesIt() throws HermesException {
        logTodos("read");
        logBook.mark(0);

        // The saved state must hold its own copies: were it the same task
        // objects, marking would alter the saved state too and undo would do nothing.
        assertEquals("Fast like myself, I have reversed thy last deed. The scroll stands as such:"
                + LINE_BREAK + "1. [T][ ] read\n", logBook.undo());
        assertEquals("1. [T][ ] read\n", reloaded());
    }

    @Test
    public void undo_afterDelete_restoresTheTask() throws HermesException {
        logTodos("read", "write");
        logBook.delete(0);

        logBook.undo();

        assertEquals("1. [T][ ] read\n2. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void undo_afterLog_removesTheTask() throws HermesException {
        logTodos("read", "write");

        logBook.undo();

        assertEquals("1. [T][ ] read\n", logBook.listTasks());
    }

    @Test
    public void undo_afterSort_restoresTheEarlierOrder() throws HermesException {
        logBook.log(new Deadline("later", LocalDateTime.of(2026, 8, 29, 9, 0)));
        logBook.log(new Deadline("sooner", LocalDateTime.of(2026, 8, 25, 9, 0)));
        logBook.sort();

        logBook.undo();

        assertEquals("""
                1. [D][ ] later (by: 29/08/2026 0900)
                2. [D][ ] sooner (by: 25/08/2026 0900)
                """, logBook.listTasks());
    }

    @Test
    public void undo_repeated_stepsBackOneChangeEachTime() throws HermesException {
        logTodos("read");
        logBook.mark(0);
        logBook.delete(0);

        logBook.undo();
        logBook.undo();

        assertEquals("1. [T][ ] read\n", logBook.listTasks());
        logBook.undo();
        assertEquals("There is naught to undo.", logBook.undo());
    }

    @Test
    public void undo_afterCommandsThatChangedNothing_hasNothingToUndo() throws HermesException {
        // A rejected mark and a sort of an empty list leave nothing to reverse.
        assertThrows(HermesException.class, () -> logBook.mark(0));
        logBook.sort();

        assertEquals("There is naught to undo.", logBook.undo());
    }

    // ---- getSkippedLines ----

    @Test
    public void getSkippedLines_dataFileWithUnreadableLines_reportsHowMany() throws IOException {
        Files.write(dataFile, List.of("T | 0 | read", "rubbish", "T | 9 | bad flag"));

        LogBook reopened = new LogBook(new Storage(dataFile.toString()));

        assertEquals(2, reopened.getSkippedLines());
        assertEquals("1. [T][ ] read\n", reopened.listTasks());
    }
}
