package botavius.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests searching tasks in a task list. */
class TaskListTest {

    /** Verifies that find searches descriptions and returns matching tasks. */
    @Test
    void find_matchingText_returnsMatchingTasks() {
        new TaskList("[T][ ] buy milk\n[T][ ] borrow book\n");

        assertEquals("Here are the matching tasks in your list:\n"
                + "1: [T][ ] borrow book", TaskList.find("book"));
    }

    /** Verifies that find reports when the search text is not present. */
    @Test
    void find_missingText_returnsNoMatchMessage() {
        new TaskList("[T][ ] buy milk\n");

        assertEquals("There are no matching tasks.", TaskList.find("book"));
    }
}
