package baymax.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import baymax.exception.BaymaxException;

/** Tests model boundaries independently of parsing and persistence. */
public class TaskTest {
    @Test
    public void completion_repeatedTransitions_preservesDescriptionAndStorage() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toStorageString());
        task.markAsDone();
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
        assertEquals("1 | read book", task.toStorageString());
        task.markAsUndone();
        task.markAsUndone();
        assertEquals("0 | read book", task.toStorageString());
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void constructor_storageSeparatorsAndControls_rejectsUnsafeDescriptions() {
        for (String description : new String[]{"a|b", "a\nb", "a\rb", "a\tb", "a\u0000b", "a\u007fb"}) {
            assertThrows(BaymaxException.class, () -> new Todo(description), description);
        }
        assertEquals("T | 0 | 阅读 📚", new Todo("阅读 📚").toStorageString());
    }

    @Test
    public void hasSameDetails_typeDescriptionAndDates_determineDuplicates() {
        LocalDate first = LocalDate.of(2024, 2, 28);
        LocalDate second = first.plusDays(1);
        Todo todo = new Todo("work");
        Todo done = new Todo("work");
        done.markAsDone();
        assertTrue(todo.hasSameDetails(done));
        assertFalse(todo.hasSameDetails(new Todo("Work")));
        assertFalse(todo.hasSameDetails(new Deadline("work", first)));
        Event event = new Event("work", first, second);
        assertTrue(event.hasSameDetails(new Event("work", first, second)));
        assertFalse(event.hasSameDetails(new Event("work", first.minusDays(1), second)));
        assertFalse(event.hasSameDetails(new Event("work", first, second.plusDays(1))));
        assertFalse(event.hasSameDetails(new Event("rest", first, second)));
    }

    @Test
    public void dates_boundaryYearsAndEventOrder_validateDirectConstruction() {
        for (LocalDate date : new LocalDate[]{LocalDate.of(1, 1, 1), LocalDate.of(9999, 12, 31)}) {
            assertSame(date, TaskDate.validate(date));
        }
        for (LocalDate date : new LocalDate[]{LocalDate.of(0, 12, 31), LocalDate.of(10000, 1, 1)}) {
            assertThrows(BaymaxException.class, () -> new Deadline("work", date));
            assertThrows(BaymaxException.class, () -> new Event("work", date, LocalDate.of(2024, 1, 1)));
            assertThrows(BaymaxException.class, () -> new Event("work", LocalDate.of(2024, 1, 1), date));
        }
        LocalDate date = LocalDate.of(2024, 2, 29);
        assertThrows(BaymaxException.class, () -> new Event("work", date, date));
        assertThrows(BaymaxException.class, () -> new Event("work", date, date.minusDays(1)));
    }

    @Test
    public void find_turkishLocaleAndIndependentResults_preservesOriginalList() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList();
            Todo task = new Todo("FINISH work");
            tasks.add(task);
            TaskList found = tasks.find("finish\tWORK");
            assertEquals(1, found.size());
            assertSame(task, found.remove(0));
            assertEquals(1, tasks.size());
            assertSame(task, tasks.remove(0));
            assertEquals(0, tasks.size());
            assertEquals(0, tasks.find("work").size());
        } finally {
            Locale.setDefault(original);
        }
    }
}
