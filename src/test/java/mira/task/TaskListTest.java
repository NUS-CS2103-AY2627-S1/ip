package mira.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import mira.exception.MiraException;

class TaskListTest {
    @Test
    void setDone_validAndBoundaryNumbers_updatesSelectedTask() throws MiraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("last"));

        Task first = tasks.setDone(1, true);
        Task last = tasks.setDone(2, true);
        assertTrue(first.isDone());
        assertTrue(last.isDone());

        tasks.setDone(1, false);
        assertFalse(first.isDone());
        assertTrue(last.isDone());
    }

    @Test
    void delete_validNumber_removesAndRenumbersTasks() throws MiraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("middle"));
        tasks.add(new Todo("last"));

        Task deleted = tasks.delete(2);

        assertEquals("middle", deleted.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("last", tasks.get(2).getDescription());
    }

    @Test
    void get_outOfRangeNumber_throwsWithoutChangingList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));

        assertThrows(MiraException.class, () -> tasks.get(0));
        assertThrows(MiraException.class, () -> tasks.get(2));
        assertEquals(1, tasks.size());
        assertEquals("only", tasks.asList().get(0).getDescription());
    }

    @Test
    void find_matchingDescriptions_isCaseInsensitiveAndLiteral() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Deadline("return library copy", LocalDate.of(2026, 8, 28)));
        tasks.add(new Todo("symbols [.*| stay literal"));

        List<Task> bookMatches = tasks.find("BOOK");
        List<Task> symbolMatches = tasks.find("[.*|");

        assertEquals(1, bookMatches.size());
        assertEquals("Read Book", bookMatches.get(0).getDescription());
        assertEquals(1, symbolMatches.size());
        assertEquals("symbols [.*| stay literal", symbolMatches.get(0).getDescription());
        assertTrue(tasks.find("Aug 28 2026").isEmpty());
        assertEquals(3, tasks.size());
    }
}
