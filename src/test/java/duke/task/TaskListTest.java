package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.exception.GaryException;
import duke.storage.Storage;

public class TaskListTest {

    @TempDir
    Path tempDir;

    @Test
    public void constructor_nullInitialList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_initialListContainingNull_throwsAssertionError() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(initialTasks));
    }

    @Test
    public void setStorage_nullStorage_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList().setStorage(null));
    }

    @Test
    public void getLast_emptyList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList().getLast());
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList().add(null));
    }

    @Test
    public void addTypedTask_invalidRequiredArguments_throwAssertionError() {
        TaskList tasks = new TaskList();
        LocalDate start = LocalDate.of(2026, 8, 25);
        LocalDate end = LocalDate.of(2026, 8, 26);

        assertThrows(AssertionError.class, () -> tasks.addTodo(null));
        assertThrows(AssertionError.class, () -> tasks.addTodo(" "));
        assertThrows(AssertionError.class, () -> tasks.addDeadline(null, end));
        assertThrows(AssertionError.class, () -> tasks.addDeadline("return book", null));
        assertThrows(AssertionError.class, () -> tasks.addEvent(null, start, end));
        assertThrows(AssertionError.class, () -> tasks.addEvent("meeting", null, end));
        assertThrows(AssertionError.class, () -> tasks.addEvent("meeting", start, null));
    }

    @Test
    public void listOperations_invalidRequiredArguments_throwAssertionError() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertThrows(AssertionError.class, () -> tasks.remove(-1));
        assertThrows(AssertionError.class, () -> tasks.remove(1));
        assertThrows(AssertionError.class, () -> tasks.mark(-1, true));
        assertThrows(AssertionError.class, () -> tasks.mark(1, true));
        assertThrows(AssertionError.class, () -> tasks.findByKeyword(null));
        assertThrows(AssertionError.class, () -> tasks.findByKeyword(" "));
    }

    @Test
    public void addTodo_increasesSize_andStoresTodo() {
        TaskList tasks = new TaskList();

        tasks.addTodo("read book");

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.getLast().toDataString());
    }

    @Test
    public void addTodo_repeatedWhitespace_normalizesDescription() {
        TaskList tasks = new TaskList();

        tasks.addTodo("  read   the\tbook  ");

        assertEquals("T | 0 | read the book", tasks.getLast().toDataString());
    }

    @Test
    public void addTodo_duplicateDetails_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        GaryException exception = assertThrows(GaryException.class, () -> tasks.addTodo("READ  BOOK"));

        assertEquals("That task already exists in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void addTodo_storageDelimiterInDescription_throwsGaryException() {
        TaskList tasks = new TaskList();

        GaryException exception = assertThrows(GaryException.class, () -> tasks.addTodo("read | book"));

        assertEquals("Task descriptions cannot contain the '|' character.", exception.getMessage());
        assertEquals(0, tasks.size());
    }

    @Test
    public void addTodo_descriptionLongerThanLimit_throwsGaryException() {
        TaskList tasks = new TaskList();

        GaryException exception = assertThrows(GaryException.class, () -> tasks.addTodo("a".repeat(201)));

        assertEquals("Task descriptions cannot exceed 200 characters.", exception.getMessage());
        assertEquals(0, tasks.size());
    }

    @Test
    public void addTodo_descriptionAtLimit_addsTask() {
        TaskList tasks = new TaskList();
        String description = "a".repeat(200);

        tasks.addTodo(description);

        assertEquals("T | 0 | " + description, tasks.getLast().toDataString());
    }

    @Test
    public void addDeadline_increasesSize_andStoresDeadline() {
        TaskList tasks = new TaskList();

        tasks.addDeadline("return book", LocalDate.of(2026, 8, 25));

        assertEquals(1, tasks.size());
        assertEquals("D | 0 | return book | 2026-08-25", tasks.getLast().toDataString());
    }

    @Test
    public void addDeadline_duplicateDetails_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addDeadline("return book", LocalDate.of(2026, 8, 25));

        assertThrows(GaryException.class, () ->
                tasks.addDeadline("RETURN BOOK", LocalDate.of(2026, 8, 25)));

        assertEquals(1, tasks.size());
    }

    @Test
    public void addDeadline_sameDescriptionDifferentDate_addsBothTasks() {
        TaskList tasks = new TaskList();
        tasks.addDeadline("return book", LocalDate.of(2026, 8, 25));

        tasks.addDeadline("return book", LocalDate.of(2026, 8, 26));

        assertEquals(2, tasks.size());
    }

    @Test
    public void addEvent_increasesSize_andStoresEvent() {
        TaskList tasks = new TaskList();

        tasks.addEvent("project meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertEquals(1, tasks.size());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26", tasks.getLast().toDataString());
    }

    @Test
    public void addEvent_duplicateDetails_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addEvent("project meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertThrows(GaryException.class, () ->
                tasks.addEvent("PROJECT MEETING", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26)));

        assertEquals(1, tasks.size());
    }

    @Test
    public void addEvent_sameDescriptionDifferentDates_addsBothTasks() {
        TaskList tasks = new TaskList();
        tasks.addEvent("project meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        tasks.addEvent("project meeting", LocalDate.of(2026, 8, 26), LocalDate.of(2026, 8, 27));

        assertEquals(2, tasks.size());
    }

    @Test
    public void addEvent_startSameAsEnd_throwsGaryException() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 8, 25);

        GaryException exception = assertThrows(GaryException.class, () ->
                tasks.addEvent("project meeting", date, date));

        assertEquals("The event start date must be before the end date.", exception.getMessage());
        assertEquals(0, tasks.size());
    }

    @Test
    public void addTodo_saveFails_rollsBackAddition() throws Exception {
        Path blockingParent = tempDir.resolve("not-a-directory");
        Files.writeString(blockingParent, "content");
        TaskList tasks = new TaskList();
        tasks.setStorage(new Storage(blockingParent.resolve("duke.txt").toString()));

        assertThrows(GaryException.class, () -> tasks.addTodo("read book"));

        assertEquals(0, tasks.size());
    }

    @Test
    public void addDeadline_saveFails_rollsBackAddition() throws Exception {
        TaskList tasks = createTaskListWithFailingStorage();

        assertThrows(GaryException.class, () ->
                tasks.addDeadline("return book", LocalDate.of(2026, 8, 25)));

        assertEquals(0, tasks.size());
    }

    @Test
    public void addEvent_saveFails_rollsBackAddition() throws Exception {
        TaskList tasks = createTaskListWithFailingStorage();

        assertThrows(GaryException.class, () ->
                tasks.addEvent("meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26)));

        assertEquals(0, tasks.size());
    }

    @Test
    public void remove_saveFails_rollsBackRemoval() throws Exception {
        Path blockingParent = tempDir.resolve("not-a-directory");
        Files.writeString(blockingParent, "content");
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.setStorage(new Storage(blockingParent.resolve("duke.txt").toString()));

        assertThrows(GaryException.class, () -> tasks.remove(0));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void mark_saveFails_rollsBackStatusChange() throws Exception {
        Path blockingParent = tempDir.resolve("not-a-directory");
        Files.writeString(blockingParent, "content");
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.setStorage(new Storage(blockingParent.resolve("duke.txt").toString()));

        assertThrows(GaryException.class, () -> tasks.mark(0, true));

        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void undo_saveFails_restoresChangeAndHistory() throws Exception {
        Path blockingParent = tempDir.resolve("not-a-directory");
        Files.writeString(blockingParent, "content");
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.setStorage(new Storage(blockingParent.resolve("duke.txt").toString()));

        assertThrows(GaryException.class, tasks::undo);

        assertEquals(1, tasks.size());
        assertThrows(GaryException.class, tasks::undo);
    }

    @Test
    public void undoDeletion_saveFails_reappliesDeletionAndKeepsHistory() throws Exception {
        Path validFile = tempDir.resolve("valid-delete.txt");
        TaskList tasks = new TaskList();
        tasks.setStorage(new Storage(validFile.toString()));
        tasks.addTodo("read book");
        tasks.remove(0);
        tasks.setStorage(createFailingStorage());

        assertThrows(GaryException.class, tasks::undo);
        assertEquals(0, tasks.size());

        tasks.setStorage(new Storage(validFile.toString()));
        assertTrue(tasks.undo());
        assertEquals(1, tasks.size());
    }

    @Test
    public void undoMark_saveFails_reappliesStatusAndKeepsHistory() throws Exception {
        Path validFile = tempDir.resolve("valid-mark.txt");
        TaskList tasks = new TaskList();
        tasks.setStorage(new Storage(validFile.toString()));
        tasks.addTodo("read book");
        tasks.mark(0, true);
        tasks.setStorage(createFailingStorage());

        assertThrows(GaryException.class, tasks::undo);
        assertEquals("T | 1 | read book", tasks.get(0).toDataString());

        tasks.setStorage(new Storage(validFile.toString()));
        assertTrue(tasks.undo());
        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void mark_marksTaskDone() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        tasks.mark(0, true);

        assertEquals("T | 1 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void mark_marksTaskUndone() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.mark(0, true);

        tasks.mark(0, false);

        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void remove_removesTask_andReturnsRemovedTask() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.addTodo("write notes");

        Task removed = tasks.remove(0);

        assertEquals("T | 0 | read book", removed.toDataString());
        assertEquals(1, tasks.size());
        assertEquals("T | 0 | write notes", tasks.get(0).toDataString());
    }

    @Test
    public void validateIndex_negativeIndex_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertThrows(GaryException.class, () -> tasks.validateIndex(-1));
    }

    @Test
    public void validateIndex_outOfBounds_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertThrows(GaryException.class, () -> tasks.validateIndex(1));
    }

    @Test
    public void findByKeyword_keywordMatches_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.addTodo("write notes");
        tasks.addTodo("return book");

        assertEquals(2, tasks.findByKeyword("book").size());
        assertEquals("T | 0 | read book", tasks.findByKeyword("book").get(0).toDataString());
        assertEquals("T | 0 | return book", tasks.findByKeyword("book").get(1).toDataString());
    }

    @Test
    public void findByKeyword_caseInsensitive_matchesTasks() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertEquals(1, tasks.findByKeyword("BOOK").size());
    }

    @Test
    public void findByKeyword_underTurkishLocale_remainsCaseInsensitive() {
        TaskList tasks = new TaskList();
        tasks.addTodo("FILE REPORT");
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertEquals(1, tasks.findByKeyword("file").size());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void findByKeyword_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertEquals(0, tasks.findByKeyword("xyz").size());
    }

    @Test
    public void undo_afterAddingTask_removesAddedTask() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertTrue(tasks.undo());

        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_afterAddingDeadline_removesAddedTask() {
        TaskList tasks = new TaskList();
        tasks.addDeadline("return book", LocalDate.of(2026, 8, 25));

        assertTrue(tasks.undo());

        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_afterAddingEvent_removesAddedTask() {
        TaskList tasks = new TaskList();
        tasks.addEvent("meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertTrue(tasks.undo());

        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_afterDeletingTask_restoresExactTaskAtOriginalPosition() {
        TaskList tasks = new TaskList();
        tasks.addTodo("first");
        tasks.addDeadline("return book", LocalDate.of(2026, 8, 25));
        tasks.addTodo("last");
        tasks.mark(1, true);
        tasks.remove(1);

        assertTrue(tasks.undo());

        assertEquals(3, tasks.size());
        assertEquals("T | 0 | first", tasks.get(0).toDataString());
        assertEquals("D | 1 | return book | 2026-08-25", tasks.get(1).toDataString());
        assertEquals("T | 0 | last", tasks.get(2).toDataString());
    }

    @Test
    public void undo_afterMarkingTask_restoresPreviousStatus() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.mark(0, true);

        assertTrue(tasks.undo());

        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void undo_afterUnmarkingTask_restoresPreviousStatus() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.mark(0, true);
        tasks.mark(0, false);

        assertTrue(tasks.undo());

        assertEquals("T | 1 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void undo_afterNoOpMark_skipsNoOpAndRestoresEarlierChange() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.mark(0, true);
        tasks.mark(0, true);

        assertTrue(tasks.undo());

        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void undo_repeatedly_reversesChangesFromNewestToOldest() {
        TaskList tasks = new TaskList();
        tasks.addTodo("first");
        tasks.addTodo("second");

        assertTrue(tasks.undo());
        assertEquals(1, tasks.size());
        assertEquals("T | 0 | first", tasks.get(0).toDataString());
        assertTrue(tasks.undo());
        assertEquals(0, tasks.size());
        assertFalse(tasks.undo());
    }

    @Test
    public void undo_afterMoreThanOneHundredChanges_discardsOldestChange() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 101; i++) {
            tasks.addTodo("task " + i);
        }

        for (int i = 0; i < 100; i++) {
            assertTrue(tasks.undo());
        }

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | task 1", tasks.get(0).toDataString());
        assertFalse(tasks.undo());
    }

    @Test
    public void undo_withStorageConfigured_savesRestoredTaskList() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        TaskList tasks = new TaskList();
        tasks.setStorage(new Storage(file.toString()));
        tasks.addTodo("read book");

        assertTrue(tasks.undo());

        assertEquals("", Files.readString(file));
    }

    @Test
    public void undo_afterLoadingExistingTasks_hasNoRecordedChange() {
        ArrayList<Task> existingTasks = new ArrayList<>();
        existingTasks.add(new ToDo("read book"));
        TaskList tasks = new TaskList(existingTasks);

        assertFalse(tasks.undo());
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_existingTask_addsWithoutCreatingUndoHistory() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("loaded task");

        tasks.add(task);

        assertSame(task, tasks.get(0));
        assertFalse(tasks.undo());
    }

    @Test
    public void asList_returnsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.addTodo("first");
        tasks.addTodo("second");

        ArrayList<Task> result = tasks.asList();

        assertEquals("T | 0 | first", result.get(0).toDataString());
        assertEquals("T | 0 | second", result.get(1).toDataString());
    }

    private TaskList createTaskListWithFailingStorage() throws Exception {
        TaskList tasks = new TaskList();
        tasks.setStorage(createFailingStorage());
        return tasks;
    }

    private Storage createFailingStorage() throws Exception {
        Path blockingParent = tempDir.resolve("blocking-file-" + System.nanoTime());
        Files.writeString(blockingParent, "content");
        return new Storage(blockingParent.resolve("duke.txt").toString());
    }
}
