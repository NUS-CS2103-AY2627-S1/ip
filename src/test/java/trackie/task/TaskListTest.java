package trackie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations independently of the user interface and storage. */
public class TaskListTest {
    @Test
    public void addGetAndRemove_validTasks_updatesListInOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Deadline("submit report", LocalDate.of(2026, 9, 20));

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(firstTask, tasks.remove(0));
        assertSame(secondTask, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_returnedSnapshotCannotMutateTaskList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write notes")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void find_matchingKeyword_returnsMatchesInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 9, 20));
        Task attendMeeting = new Todo("attend meeting");
        TaskList tasks = new TaskList(List.of(readBook, returnBook, attendMeeting));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(readBook, returnBook), matches);
    }

    @Test
    public void find_differentKeywordCase_returnsMatch() {
        Task readBook = new Todo("read book");
        TaskList tasks = new TaskList(List.of(readBook));

        assertEquals(List.of(readBook), tasks.find("BOOK"));
    }
}
