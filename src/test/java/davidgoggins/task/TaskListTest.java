package davidgoggins.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import davidgoggins.DavidGogginsException;
import davidgoggins.storage.Storage;
import davidgoggins.ui.Ui;

/**
 * Tests {@link TaskList}: searching, numbering, and the changes that are saved to disk.
 *
 * <p>Read-only tests build their list directly, so no save file is read. Tests that
 * change the list save into a fresh temporary folder, which JUnit deletes afterwards, so
 * they can check the file without touching the user's real task list.
 */
public class TaskListTest {

    /** A new, empty folder for each test, created and deleted by JUnit. */
    @TempDir
    Path tempDir;

    /** Builds a list holding exactly the given tasks, with a Storage that is never used. */
    private static TaskList listOf(Task... tasks) {
        Storage unused = new Storage("build/tmp/task-list-test.txt", new Ui());
        return new TaskList(unused, List.of(tasks));
    }

    /** The three tasks the searches below are run against. */
    private static TaskList sampleList() throws DavidGogginsException {
        return listOf(new Todo("read book"),
                new Deadline("return book", "2026-06-06"),
                new Todo("run 10 miles"));
    }

    @Test
    public void find_keywordInSomeDescriptions_onlyThoseReturned() throws DavidGogginsException {
        List<Task> matches = sampleList().find("book");
        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: 2026-06-06)", matches.get(1).toString());
    }

    /** A user typing quickly should not have to match the capitals they first used. */
    @Test
    public void find_keywordInDifferentCase_stillMatches() throws DavidGogginsException {
        assertEquals(2, sampleList().find("BOOK").size());
    }

    /** The stored description is what varies in case here, rather than the keyword. */
    @Test
    public void find_descriptionInDifferentCase_stillMatches() throws DavidGogginsException {
        assertEquals(1, listOf(new Todo("Read Book")).find("read").size());
    }

    @Test
    public void find_noDescriptionContainsKeyword_emptyListReturned() throws DavidGogginsException {
        assertTrue(sampleList().find("swim").isEmpty());
    }

    /** Matching is on any part of the description, not only whole words. */
    @Test
    public void find_keywordIsPartOfAWord_stillMatches() throws DavidGogginsException {
        assertEquals(1, sampleList().find("mile").size());
    }

    /** Searching must not disturb the list it searches. */
    @Test
    public void find_afterSearching_listUnchanged() throws DavidGogginsException {
        TaskList tasks = sampleList();
        tasks.find("book");
        assertEquals(3, tasks.size());
    }

    /** Matches are numbered from 1, not by their position in the whole list. */
    @Test
    public void format_matchesFromMiddleOfList_numberedFromOne() throws DavidGogginsException {
        String lines = TaskList.format(sampleList().find("book"));
        assertEquals(" 1.[T][ ] read book" + System.lineSeparator()
                + " 2.[D][ ] return book (by: 2026-06-06)", lines);
    }

    @Test
    public void format_emptyList_emptyStringReturned() {
        assertEquals("", TaskList.format(List.of()));
    }

    @Test
    public void countDone_someTasksDone_onlyDoneTasksCounted() throws DavidGogginsException {
        Todo doneTodo = new Todo("run 10 miles");
        doneTodo.markAsDone();
        TaskList tasks = listOf(new Todo("read book"), doneTodo);
        assertEquals(1, tasks.countDone());
    }

    @Test
    public void countDone_emptyList_zeroReturned() {
        assertEquals(0, listOf().countDone());
    }

    /** The whole list is numbered by the same code the search results use. */
    @Test
    public void toString_wholeList_sameFormatAsMatches() throws DavidGogginsException {
        TaskList tasks = sampleList();
        assertEquals(TaskList.format(tasks.find("")), tasks.toString());
    }

    /** Returns the save file used by the tests that change the list. */
    private Path saveFile() {
        return tempDir.resolve("tasks.txt");
    }

    /** Builds a list that loads from and saves to {@link #saveFile()}. */
    private TaskList savedList() {
        return new TaskList(new Storage(saveFile().toString(), new Ui()));
    }

    @Test
    public void add_newTask_savedToFile() throws IOException {
        TaskList tasks = savedList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile()));
    }

    @Test
    public void mark_existingTask_markedDoneAndSaved() throws IOException {
        TaskList tasks = savedList();
        tasks.add(new Todo("read book"));
        Task marked = tasks.mark(1);
        assertTrue(marked.isDone());
        assertEquals(List.of("T | 1 | read book"), Files.readAllLines(saveFile()));
    }

    @Test
    public void unmark_doneTask_markedNotDoneAndSaved() throws IOException {
        TaskList tasks = savedList();
        tasks.add(new Todo("read book"));
        tasks.mark(1);
        Task unmarked = tasks.unmark(1);
        assertFalse(unmarked.isDone());
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile()));
    }

    /** Removing from the middle must renumber the tasks after it. */
    @Test
    public void remove_middleTask_laterTasksMoveUp() throws IOException, DavidGogginsException {
        TaskList tasks = savedList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2026-06-06"));
        tasks.add(new Todo("run 10 miles"));

        Task removed = tasks.remove(2);

        assertEquals("[D][ ] return book (by: 2026-06-06)", removed.toString());
        assertEquals("[T][ ] run 10 miles", tasks.get(2).toString());
        assertEquals(List.of("T | 0 | read book", "T | 0 | run 10 miles"), Files.readAllLines(saveFile()));
    }

    @Test
    public void constructor_existingSaveFile_tasksLoaded() throws IOException {
        Files.writeString(saveFile(), "T | 1 | read book\nD | 0 | return book | 2026-06-06\n");
        TaskList tasks = savedList();
        assertEquals(2, tasks.size());
        assertEquals(" 1.[T][X] read book" + System.lineSeparator()
                + " 2.[D][ ] return book (by: 2026-06-06)", tasks.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void isValidTaskNumber_numberInRange_true(int taskNumber) throws DavidGogginsException {
        assertTrue(sampleList().isValidTaskNumber(taskNumber));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 4, Integer.MAX_VALUE})
    public void isValidTaskNumber_numberOutOfRange_false(int taskNumber) throws DavidGogginsException {
        assertFalse(sampleList().isValidTaskNumber(taskNumber));
    }

    @Test
    public void findDuplicateNumber_duplicateInList_itsNumberReturned() throws DavidGogginsException {
        assertEquals(2, sampleList().findDuplicateNumber(new Deadline("Return Book", "2026-06-06")));
    }

    @Test
    public void findDuplicateNumber_noDuplicate_zeroReturned() throws DavidGogginsException {
        assertEquals(0, sampleList().findDuplicateNumber(new Deadline("return book", "2026-06-07")));
    }
}
