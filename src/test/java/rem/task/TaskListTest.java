package rem.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import rem.exception.InvalidTaskNumberException;
import rem.exception.MissingNoteException;
import rem.exception.RemException;

/**
 * Tests the core task mutation and scheduling operations of {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void add_multipleTasks_tasksAppendedInOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.getTask(1));
        assertSame(second, tasks.getTask(2));
    }

    @Test
    public void delete_validTaskNumber_taskRemovedAndReturned() throws InvalidTaskNumberException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertSame(first, tasks.delete(1));
        assertEquals(List.of(second), tasks.getTasks());
    }

    @Test
    public void delete_outOfRangeTaskNumbers_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(2));
    }

    @Test
    public void mark_validTaskNumber_taskMarkedAndReturned() throws InvalidTaskNumberException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.mark(1));
        assertTrue(todo.isDone());
    }

    @Test
    public void unmark_completedTask_taskMarkedNotDoneAndReturned() throws InvalidTaskNumberException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.unmark(1));
        assertFalse(todo.isDone());
    }

    @Test
    public void markAndUnmark_outOfRangeTaskNumbers_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> tasks.mark(1));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.unmark(1));
    }

    @Test
    public void setAndDeleteNote_validTask_noteUpdatedAndRemoved()
            throws InvalidTaskNumberException, MissingNoteException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.setNote(1, "Borrow it from Alice"));
        assertEquals("Borrow it from Alice", todo.getNote());
        assertSame(todo, tasks.setNote(1, "Return it tomorrow"));
        assertEquals("Return it tomorrow", todo.getNote());
        assertSame(todo, tasks.deleteNote(1));
        assertFalse(todo.hasNote());
    }

    @Test
    public void deleteNote_taskWithoutNote_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(MissingNoteException.class, () -> tasks.deleteNote(1));
    }

    @Test
    public void noteOperations_outOfRangeTaskNumbers_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.setNote(0, "note"));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.setNote(2, "note"));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.deleteNote(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.deleteNote(2));
    }

    @Test
    public void findTasksOn_matchingScheduledTasks_matchesInOriginalOrder() {
        LocalDate target = LocalDate.of(2026, 8, 29);
        Todo todo = new Todo("not scheduled");
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 9, 0),
                LocalDateTime.of(2026, 8, 30, 17, 0));
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 29, 18, 0));
        Deadline otherDeadline = new Deadline("later", LocalDateTime.of(2026, 8, 31, 18, 0));
        TaskList tasks = new TaskList(List.of(todo, event, deadline, otherDeadline));

        assertEquals(List.of(event, deadline), tasks.findTasksOn(target));
    }

    @Test
    public void findTasks_matchingKeyword_matchesDescriptionsIgnoringCaseInOriginalOrder() {
        Todo firstMatch = new Todo("Read Book");
        Deadline secondMatch = new Deadline("return book",
                LocalDateTime.of(2026, 8, 29, 18, 0));
        Todo nonMatch = new Todo("buy groceries");
        TaskList tasks = new TaskList(List.of(firstMatch, secondMatch, nonMatch));

        assertEquals(List.of(firstMatch, secondMatch), tasks.findTasks("BOOK"));
    }

    @Test
    public void findTasks_keywordInDescriptionOrNote_eachTaskReturnedOnce() {
        Todo descriptionMatch = new Todo("Call Alice");
        Todo noteMatch = new Todo("read book");
        noteMatch.setNote("Borrow it from ALICE");
        Todo doubleMatch = new Todo("Meet Alice");
        doubleMatch.setNote("Ask Alice about class");
        TaskList tasks = new TaskList(List.of(descriptionMatch, noteMatch, doubleMatch));

        assertEquals(List.of(descriptionMatch, noteMatch, doubleMatch), tasks.findTasks("alice"));
    }

    @Test
    public void findTasks_turkishDefaultLocale_caseMatchingRemainsStable() {
        java.util.Locale originalLocale = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr-TR"));
            Todo descriptionMatch = new Todo("FILE REPORT");
            Todo noteMatch = new Todo("other");
            noteMatch.setNote("FILE NOTES");
            TaskList tasks = new TaskList(List.of(descriptionMatch, noteMatch));

            assertEquals(List.of(descriptionMatch, noteMatch), tasks.findTasks("file"));
        } finally {
            java.util.Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void copy_allTaskTypesAndMutableState_independentDeepCopyReturned() throws Exception {
        Todo todo = new Todo("read");
        todo.markAsDone();
        todo.setNote("library copy");
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 10, 1, 18, 0));
        Event event = new Event("conference", LocalDateTime.of(2026, 10, 2, 9, 0),
                LocalDateTime.of(2026, 10, 3, 17, 0));
        TaskList original = new TaskList(List.of(todo, deadline, event));

        TaskList copy = original.copy();

        assertEquals(3, copy.size());
        assertInstanceOf(Todo.class, copy.getTask(1));
        assertInstanceOf(Deadline.class, copy.getTask(2));
        assertInstanceOf(Event.class, copy.getTask(3));
        for (int taskNumber = 1; taskNumber <= original.size(); taskNumber++) {
            assertNotSame(original.getTask(taskNumber), copy.getTask(taskNumber));
            assertEquals(original.getTask(taskNumber).toString(), copy.getTask(taskNumber).toString());
        }

        copy.unmark(1);
        copy.setNote(1, "changed copy");
        copy.delete(2);
        assertTrue(original.getTask(1).isDone());
        assertEquals("library copy", original.getTask(1).getNote());
        assertEquals(3, original.size());
    }

    @Test
    public void validateUnique_sameIdentityExceptStatus_exceptionIdentifiesExistingTask()
            throws RemException {
        Todo existing = new Todo("read");
        existing.markAsDone();
        existing.setNote("library copy");
        TaskList tasks = new TaskList(List.of(new Todo("other"), existing));
        Todo candidate = new Todo("read");
        candidate.setNote("library copy");

        RemException exception = assertThrows(RemException.class, () ->
                tasks.validateUnique(candidate));

        assertEquals("This task already exists as task 2. Use list to see it.", exception.getMessage());
    }

    @Test
    public void validateUnique_differentTypeScheduleDescriptionOrNote_noExceptionThrown()
            throws RemException {
        Deadline deadline = new Deadline("read", LocalDateTime.of(2026, 10, 1, 18, 0));
        Event event = new Event("meet", LocalDateTime.of(2026, 10, 2, 9, 0),
                LocalDateTime.of(2026, 10, 2, 10, 0));
        TaskList tasks = new TaskList(List.of(new Todo("read"), deadline, event));

        tasks.validateUnique(new Deadline("read", LocalDateTime.of(2026, 10, 2, 18, 0)));
        tasks.validateUnique(new Deadline("write", LocalDateTime.of(2026, 10, 1, 18, 0)));
        tasks.validateUnique(new Event("meet", LocalDateTime.of(2026, 10, 2, 9, 0),
                LocalDateTime.of(2026, 10, 2, 11, 0)));
        tasks.validateUnique(new Event("meet", LocalDateTime.of(2026, 10, 2, 8, 0),
                LocalDateTime.of(2026, 10, 2, 10, 0)));
        tasks.validateUnique(new Todo("write"));
        Todo notedTodo = new Todo("read");
        notedTodo.setNote("different");
        tasks.validateUnique(notedTodo);
    }

    @Test
    public void getTasks_modifyReturnedSnapshot_originalListUnchanged() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void constructorOrAdd_nullTask_assertionErrorThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList((Task) null)));
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void getTask_outOfRangeTaskNumber_assertionErrorThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(AssertionError.class, () -> tasks.getTask(0));
        assertThrows(AssertionError.class, () -> tasks.getTask(2));
    }

    @Test
    public void taskConstructor_blankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo(""));
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }
}
