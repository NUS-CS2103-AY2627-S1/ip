package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests keyword searching while preserving original task positions. */
class TaskListFindTest {
    @Test
    void find_matchingKeyword_returnsOriginalTaskNumbersCaseInsensitive() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy bread"));
        taskList.add(new Deadline("return BOOK", java.time.LocalDateTime.of(2026, 8, 27, 0, 0)));

        assertEquals(List.of(1, 3), taskList.find("book"));
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertEquals(List.of(), taskList.find("holiday"));
    }
}
