package quackers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_mixedCaseKeyword_returnsMatchingDescriptionsInOriginalOrder() {
        Todo readBook = new Todo("read book");
        Event bookClub = new Event("attend Book club", "2pm", "4pm");
        TaskList tasks = new TaskList(List.of(
                readBook,
                new Deadline("return notes", LocalDate.of(2027, 6, 6)),
                bookClub));

        assertEquals(List.of(readBook, bookClub), tasks.find("BOOK"));
    }

    @Test
    void find_keywordOnlyInTaskDetails_doesNotMatch() {
        Deadline deadline = new Deadline("return novel", LocalDate.of(2027, 6, 6));
        Event event = new Event("meeting", "book room", "4pm");
        TaskList tasks = new TaskList(List.of(deadline, event));

        assertEquals(List.of(), tasks.find("book"));
    }

    @Test
    void updateStatus_note_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Note("keep this information")));

        QuackersException error = assertThrows(
                QuackersException.class, () -> tasks.updateStatus(0, true));

        assertEquals("Quack? Notes do not have a completion status.", error.getMessage());
    }

    @Test
    void find_keywordWithNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("cycling"));
    }

    @Test
    void add_task_appendsToEndAndIncreasesSize() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(List.of(first, second), tasks.getTasks());
    }

    @Test
    void delete_validIndex_removesAndReturnsThatTask() throws QuackersException {
        Todo readBook = new Todo("read book");
        Todo returnBook = new Todo("return book");
        TaskList tasks = new TaskList(List.of(readBook, returnBook));

        assertEquals(readBook, tasks.delete(0));
        assertEquals(1, tasks.size());
        assertEquals(List.of(returnBook), tasks.getTasks());
    }

    @Test
    void delete_indexBeyondList_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        QuackersException error = assertThrows(
                QuackersException.class, () -> tasks.delete(1));

        assertEquals("Quack? Please enter a task number from the list.", error.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void delete_negativeIndex_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        QuackersException error = assertThrows(
                QuackersException.class, () -> tasks.delete(-1));

        assertEquals("Quack? Please enter a task number from the list.", error.getMessage());
    }

    @Test
    void updateStatus_markThenUnmark_togglesCompletion() throws QuackersException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertEquals(todo, tasks.updateStatus(0, true));
        assertTrue(todo.isDone());

        assertEquals(todo, tasks.updateStatus(0, false));
        assertFalse(todo.isDone());
    }

    @Test
    void updateStatus_indexBeyondList_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        QuackersException error = assertThrows(
                QuackersException.class, () -> tasks.updateStatus(5, true));

        assertEquals("Quack? Please enter a task number from the list.", error.getMessage());
    }

    @Test
    void getTasks_sourceListChangedAfterConstruction_taskListUnaffected() {
        // The constructor copies its argument, so later changes to the caller's list must not leak in.
        List<Task> sourceTasks = new ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("return book"));

        assertEquals(1, tasks.size());
    }

    @Test
    void getTasks_returnedSnapshot_cannotBeModified() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("return book")));
    }
}
