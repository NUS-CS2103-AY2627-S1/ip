package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import bob.BobException;

class TaskListTest {
    @Test
    void find_matchingKeyword_returnsMatchingTasks() {
        Todo readBook = new Todo("read book");
        Todo returnBook = new Todo("return book");
        TaskList tasks = new TaskList(List.of(readBook, new Todo("buy pen"), returnBook));

        assertEquals(List.of(readBook, returnBook), tasks.find("book"));
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("pen"));
    }

    @Test
    void getTaskIndex_firstTaskNumber_returnsZero() throws BobException {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertEquals(0, tasks.getTaskIndex(1));
    }

    @Test
    void getTaskIndex_lastTaskNumber_returnsLastIndex() throws BobException {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertEquals(1, tasks.getTaskIndex(2));
    }

    @Test
    void getTaskIndex_emptyList_throwsBobException() {
        TaskList tasks = new TaskList();

        assertThrows(BobException.class, () -> tasks.getTaskIndex(1));
    }

    @Test
    void getTaskIndex_numberBelowRange_throwsBobException() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(BobException.class, () -> tasks.getTaskIndex(0));
    }

    @Test
    void getTaskIndex_numberAboveRange_throwsBobException() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(BobException.class, () -> tasks.getTaskIndex(2));
    }
}
