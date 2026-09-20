package bkxss;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests the common completion behavior inherited by every task type. */
class TaskTest {
    @Test
    void task_newTask_isIncompleteWithBlankStatusIcon() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void task_markAsDone_changesStatusToCompleted() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    void task_markAsNotDone_afterCompletion_changesStatusToIncomplete() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void task_matchesKeyword_isCaseInsensitiveAndSearchesDescriptionOnly() {
        Todo todo = new Todo("Read a book");

        assertTrue(todo.matchesKeyword("BOOK"));
        assertFalse(todo.matchesKeyword("movie"));
    }

    @Test
    void task_statusChanges_areIdempotentForEveryTaskType() {
        List<Task> tasks = List.of(new Todo("read"),
                new Deadline("return", LocalDateTime.of(2026, 9, 12, 18, 0)),
                new Event("meeting", "Mon 2pm", "4pm"));

        for (Task task : tasks) {
            task.markAsNotDone();
            assertFalse(task.isDone());
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            assertTrue(task.toString().contains("[X]"));
            task.markAsNotDone();
            task.markAsNotDone();
            assertFalse(task.isDone());
            assertTrue(task.toString().contains("[ ]"));
        }
    }

    @Test
    void task_hasSameDetails_comparesTypeAndDescriptionButIgnoresStatus() {
        Todo original = new Todo("read");
        Todo completed = new Todo("read");
        completed.markAsDone();

        assertTrue(original.hasSameDetails(original));
        assertTrue(original.hasSameDetails(completed));
        assertTrue(completed.hasSameDetails(original));
        assertFalse(original.hasSameDetails(null));
        assertFalse(original.hasSameDetails(new Todo("Read")));
        assertFalse(original.hasSameDetails(new Todo("read more")));
        assertFalse(original.hasSameDetails(new Deadline("read", LocalDateTime.of(2026, 9, 12, 18, 0))));
        assertFalse(original.hasSameDetails(new Event("read", "Mon 2pm", "4pm")));
    }

    @Test
    void task_hasSameDetails_deadlinesIncludeDueDate() {
        LocalDateTime by = LocalDateTime.of(2026, 9, 12, 18, 0);
        Deadline original = new Deadline("return", by);
        Deadline completed = new Deadline("return", by);
        completed.markAsDone();

        assertTrue(original.hasSameDetails(completed));
        assertFalse(original.hasSameDetails(new Deadline("return", by.plusMinutes(1))));
        assertFalse(original.hasSameDetails(new Deadline("different", by)));
    }

    @Test
    void task_hasSameDetails_eventsIncludeBothBoundaries() {
        Event original = new Event("meeting", "Mon 2pm", "4pm");
        Event completed = new Event("meeting", "Mon 2pm", "4pm");
        completed.markAsDone();

        assertTrue(original.hasSameDetails(completed));
        assertFalse(original.hasSameDetails(new Event("meeting", "Mon 3pm", "4pm")));
        assertFalse(original.hasSameDetails(new Event("meeting", "Mon 2pm", "5pm")));
        assertFalse(original.hasSameDetails(new Event("different", "Mon 2pm", "4pm")));
    }

    @Test
    void task_validateDescription_rejectsNullBlankDelimitersAndControlCharacters() {
        assertThrows(BkxssException.class, () -> Task.validateDescription(null));
        for (String description : List.of("", " \t ", "\u2003", "a|b", "a\nb", "a\rb", "a\tb",
                "a\u0000b", "a\u007fb")) {
            assertThrows(BkxssException.class, () -> Task.validateDescription(description), description);
        }
        assertDoesNotThrow(() -> Task.validateDescription("买书 / read & review!"));
    }

    @Test
    void task_matchesKeyword_turkishLocale_usesLocaleIndependentCaseFolding() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Todo todo = new Todo("FINISH reading 中文");
            assertTrue(todo.matchesKeyword("finish"));
            assertTrue(todo.matchesKeyword("READ"));
            assertTrue(todo.matchesKeyword("中文"));
            assertFalse(todo.matchesKeyword("missing"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void task_matchesKeyword_deadlineAndEventDetails_areNotSearchable() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 9, 12, 18, 0));
        Event event = new Event("meeting", "Mon 2pm", "4pm");

        assertTrue(deadline.matchesKeyword("BOOK"));
        assertFalse(deadline.matchesKeyword("Sep"));
        assertTrue(event.matchesKeyword("meet"));
        assertFalse(event.matchesKeyword("Mon"));
    }
}
