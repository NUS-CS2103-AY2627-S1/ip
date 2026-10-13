package ruby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ruby.RubyException;

class TaskListTest {

    private TaskList taskListWith(Task... tasks) throws RubyException {
        TaskList list = new TaskList();
        for (Task task : tasks) {
            list.addItem(task);
        }
        return list;
    }

    @Test
    void listItems_newList_returnsHeadingOnly() {
        TaskList taskList = new TaskList();
        assertEquals("Here's everything on your plate:", taskList.listItems());
    }

    @Test
    void addItem_addsTaskAndReportsCount() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"));
        String response = taskList.addItem(new Todo("return book"));
        assertEquals("Added to the collection:\n  [T][ ] return book"
                + "\nThat's 2 tasks on your plate.", response);
    }

    @Test
    void addItem_duplicateTask_throws() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"));
        assertThrows(RubyException.class, () -> taskList.addItem(new Todo("read book")));
    }

    @Test
    void listItems_withTasks_returnsNumberedList() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        assertEquals("Here's everything on your plate:\n1. [T][ ] read book\n2. [T][ ] return book",
                taskList.listItems());
    }

    @Test
    void markItem_marksTaskAtIndexAsDone() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        String response = taskList.markItem(0);
        assertEquals("Done — consider it polished:\n  [T][X] read book", response);
        assertEquals("Here's everything on your plate:\n1. [T][X] read book\n2. [T][ ] return book",
                taskList.listItems());
    }

    @Test
    void unmarkItem_unmarksTaskAtIndex() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        taskList.markItem(0);
        String response = taskList.unmarkItem(0);
        assertEquals("Undone — brilliance takes time:\n  [T][ ] read book", response);
    }

    @Test
    void deleteItem_removesTaskAtIndex() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        String response = taskList.deleteItem(0);
        assertEquals("Removed — gone without a trace:\n  [T][ ] read book"
                + "\nThat leaves 1 task on your plate.", response);
        assertEquals("Here's everything on your plate:\n1. [T][ ] return book",
                taskList.listItems());
    }

    @Test
    void toDataString_returnsOneLinePerTask() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        assertEquals("T | 0 | read book\nT | 0 | return book", taskList.toDataString());
    }

    @Test
    void markItem_emptyList_throws() {
        TaskList taskList = new TaskList();
        RubyException exception = assertThrows(RubyException.class, () -> taskList.markItem(0));
        assertEquals("You have no tasks to mark — add one first.", exception.getMessage());
    }

    @Test
    void unmarkItem_outOfRangeIndex_throws() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"));
        RubyException exception = assertThrows(RubyException.class, () -> taskList.unmarkItem(5));
        assertEquals("Task 6? You only have 2. Pick a number from 1 to 2.", exception.getMessage());
    }

    @Test
    void deleteItem_emptyList_throws() {
        TaskList taskList = new TaskList();
        RubyException exception = assertThrows(RubyException.class, () -> taskList.deleteItem(0));
        assertEquals("You have no tasks to delete — add one first.", exception.getMessage());
    }

    @Test
    void deleteItem_lastTask_usesPluralZeroCount() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"));

        assertEquals("Removed — gone without a trace:\n  [T][ ] read book\nThat leaves 0 tasks on your plate.",
                taskList.deleteItem(0));
    }

    @Test
    void markItem_outOfRangeIndex_throws() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"));

        RubyException exception = assertThrows(RubyException.class, () -> taskList.markItem(1));

        assertEquals("Task 2? You only have 1. Pick a number from 1 to 1.", exception.getMessage());
    }

    @Test
    void find_matchingTasks_returnsNumberedMatches() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("return book"),
                new Todo("buy milk"));
        assertEquals("Found them — I never miss:\n1. [T][ ] read book"
                        + "\n2. [T][ ] return book",
                taskList.find("book"));
    }

    @Test
    void find_keepsOriginalListNumbering() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("buy milk"),
                new Todo("return book"));
        assertEquals("Found them — I never miss:\n1. [T][ ] read book"
                        + "\n3. [T][ ] return book",
                taskList.find("book"));
    }

    @Test
    void find_caseInsensitive_matchesRegardlessOfCase() throws RubyException {
        TaskList taskList = taskListWith(new Todo("Read Book"));
        assertEquals("Found them — I never miss:\n1. [T][ ] Read Book",
                taskList.find("BOOK"));
    }

    @Test
    void find_noMatches_returnsMessage() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"));
        assertEquals("Nothing. Even I can't find what isn't there.", taskList.find("dance"));
    }

    @Test
    void find_emptyKeyword_returnsEveryTask() throws RubyException {
        TaskList taskList = taskListWith(new Todo("read book"), new Todo("buy milk"));

        assertEquals("Found them — I never miss:\n1. [T][ ] read book\n2. [T][ ] buy milk", taskList.find(""));
    }

    @Test
    void find_matchesOnFullTaskText_includesDeadlineDate() throws RubyException {
        TaskList taskList = taskListWith(new Deadline("return book",
                LocalDateTime.of(2019, 6, 6, 0, 0)));
        assertEquals("Found them — I never miss:\n1. [D][ ] return book (by: Jun 06 2019)",
                taskList.find("Jun 06"));
    }
}
