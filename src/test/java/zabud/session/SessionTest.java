package zabud.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zabud.tasks.Deadline;
import zabud.tasks.Event;
import zabud.tasks.Todo;

/**
 * Tests task-list boundaries plus session history and persistence failure cases.
 */
class SessionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void taskList_handlesInvalidIndexesRemovalAndCapacity() {
        TaskList list = new TaskList();
        assertNull(list.get(0));
        assertNull(list.get(1));
        assertNull(list.remove(-1));
        for (int index = 0; index < 100; index++) {
            assertTrue(list.add(new Todo("task " + index)));
        }
        assertFalse(list.add(new Todo("overflow")));
        assertEquals("task 0", list.remove(1).getDescription());
        assertEquals("task 1", list.get(1).getDescription());
        assertEquals(99, list.size());
    }

    @Test
    void taskList_findKeyword_matchesPartialTextIgnoringCaseInEntryOrder() {
        TaskList list = new TaskList();
        Todo firstMatch = new Todo("read book");
        Deadline secondMatch = new Deadline("return book", LocalDate.of(2019, 12, 2), null);
        list.add(firstMatch);
        list.add(new Todo("watch movie"));
        list.add(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), list.find("BOOK"));
        assertEquals(List.of(firstMatch, secondMatch), list.find("ook"));
        assertEquals(List.of(), list.find("game"));
    }

    @Test
    void session_roundTripsTasksAndHistoryAndEnforcesHistoryLimit() {
        Path path = temporaryDirectory.resolve("session.txt");
        Session first = new Session(path);
        first.getTaskList().add(new Todo("read | book"));
        first.getTaskList().add(new Deadline("return book", LocalDate.of(2019, 12, 2), null));
        first.getTaskList().add(new Event("meeting", LocalDate.of(2025, 8, 6), LocalTime.of(14, 0),
                LocalDate.of(2025, 8, 6), LocalTime.of(16, 0)));
        first.getTaskList().get(2).markAsDone();
        first.recordCommand("   ");
        for (int index = 0; index <= Session.COMMAND_HISTORY_LIMIT; index++) {
            first.recordCommand("command " + index);
        }

        Session restored = new Session(path);
        assertEquals(3, restored.getTaskList().size());
        assertEquals("read | book", restored.getTaskList().get(1).getDescription());
        assertTrue(restored.getTaskList().get(2).isDone());
        assertTrue(restored.getTaskList().get(3).toString().contains("from: 2025-08-06 14:00"));
        assertEquals("command 1000", restored.previousCommand());
        for (int index = 0; index < Session.COMMAND_HISTORY_LIMIT - 1; index++) {
            restored.previousCommand();
        }
        assertEquals("command 1", restored.previousCommand());
        assertEquals("command 2", restored.nextCommand());
        restored.resetHistoryNavigation();
        assertEquals("command 1000", restored.previousCommand());
    }

    @Test
    void session_ignoresMissingUnreadableAndCorruptSavedData() throws Exception {
        assertEquals(0, new Session(temporaryDirectory.resolve("missing.txt")).getTaskList().size());
        Path corrupt = temporaryDirectory.resolve("corrupt.txt");
        Files.writeString(corrupt, "T|1|!\nD|0|dGVzdA==|bad\nE|0|dGVzdA==|bad|bad|bad|bad\nC|!");
        Session session = new Session(corrupt);
        assertEquals(0, session.getTaskList().size());
        assertEquals("", session.previousCommand());
    }
}
