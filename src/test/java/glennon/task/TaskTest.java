package glennon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests shared description validation and task detail comparisons through
 * concrete task types.
 */
class TaskTest {
    @Test
    void constructor_nullOrBlankDescriptions_throwsHelpfulException() {
        String[] invalidDescriptions = {null, "", " ", "\t", " \t\n ", "\u2003", "\u00a0", "\u202f",
            "\u2007", " \t\u00a0\u202f\u2007\u2003 "};

        for (String description : invalidDescriptions) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> new Todo(description));
            assertEquals("Mission descriptions must not be blank.", exception.getMessage());
        }
    }

    @Test
    void constructor_unicodeBlankScheduledDescriptions_throwsHelpfulException() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 17, 9, 0);
        for (String description : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            IllegalArgumentException deadlineError = assertThrows(
                    IllegalArgumentException.class, () -> new Deadline(description, start));
            IllegalArgumentException eventError = assertThrows(
                    IllegalArgumentException.class, () -> new Event(description, start, start.plusHours(1)));

            assertEquals("Mission descriptions must not be blank.", deadlineError.getMessage());
            assertEquals("Mission descriptions must not be blank.", eventError.getMessage());
        }
    }

    @Test
    void constructor_surroundingWhitespace_stripsEdgesAndPreservesInternalText() {
        Todo todo = new Todo(" \t\u2003Café  reading\t@図書館 / notes: \"hi\" 😊\u2003\t ");

        assertEquals("Café  reading\t@図書館 / notes: \"hi\" 😊", todo.getDescription());
        assertEquals("[T][ ] Café  reading\t@図書館 / notes: \"hi\" 😊", todo.toString());
    }

    @Test
    void constructor_unicodeSpacePadding_trimsAllTypesWithoutChangingInternalText() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 17, 9, 0);
        String expectedDescription = "🚀 Café\u00a0\u202f\u2007  reading\t@図書館 😊";
        for (String padding : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007\u2003 ")) {
            String description = padding + expectedDescription + padding;
            List<Task> tasks = List.of(new Todo(description), new Deadline(description, start),
                    new Event(description, start, start.plusHours(1)));

            for (Task task : tasks) {
                assertEquals(expectedDescription, task.getDescription());
            }
        }
    }

    @Test
    void constructor_lineBreaksOrControlCharacters_rejectsUnsafeDescriptions() {
        List<Integer> invalidCharacters = List.of(0, 7, 8, 10, 11, 12, 13, 27, 127, 133, 0x2028, 0x2029);

        for (int character : invalidCharacters) {
            String description = "before" + new String(Character.toChars(character)) + "after";
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> new Todo(description));
            assertEquals("Mission descriptions must be on one line without control characters.",
                    exception.getMessage());
        }
    }

    @Test
    void constructor_surroundingLineBreaks_rejectsInsteadOfSilentlyTrimming() {
        for (String description : List.of("\nmission", "mission\n", "\rmission", "mission\u2028",
                "\u00a0\nmission\u202f", "\u2007mission\u2029\u00a0")) {
            assertThrows(IllegalArgumentException.class, () -> new Todo(description));
        }
    }

    @Test
    void hasSameDetails_matchingDescriptions_ignoresCompletionButPreservesObjectEquality() {
        Todo first = new Todo("read book");
        Todo second = new Todo("  read book\t");
        second.markAsDone();

        assertTrue(first.hasSameDetails(second));
        assertTrue(second.hasSameDetails(first));
        assertTrue(first.hasSameDetails(first));
        assertNotEquals(first, second);
    }

    @Test
    void hasSameDetails_differentDescriptions_preservesCaseAndInternalWhitespace() {
        Todo todo = new Todo("read book");

        assertFalse(todo.hasSameDetails(new Todo("Read book")));
        assertFalse(todo.hasSameDetails(new Todo("read  book")));
        assertFalse(todo.hasSameDetails(new Todo("read\tbook")));
        assertFalse(todo.hasSameDetails(new Todo("read\u00a0book")));
        assertFalse(todo.hasSameDetails(new Todo("read\u202fbook")));
        assertFalse(todo.hasSameDetails(new Todo("read\u2007book")));
        assertFalse(todo.hasSameDetails(new Todo("read books")));
    }

    @Test
    void hasSameDetails_unicodeSpacePadding_matchesAllTypesRegardlessOfStatus() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 17, 9, 0);
        List<Task> originalTasks = List.of(new Todo("read book"), new Deadline("read book", start),
                new Event("read book", start, start.plusHours(1)));
        for (String padding : List.of("\u00a0", "\u202f", "\u2007")) {
            String description = padding + "read book" + padding;
            List<Task> paddedTasks = List.of(new Todo(description), new Deadline(description, start),
                    new Event(description, start, start.plusHours(1)));

            for (int i = 0; i < originalTasks.size(); i++) {
                paddedTasks.get(i).markAsDone();
                assertTrue(originalTasks.get(i).hasSameDetails(paddedTasks.get(i)));
                assertTrue(paddedTasks.get(i).hasSameDetails(originalTasks.get(i)));
            }
        }
    }

    @Test
    void hasSameDetails_nullOrDifferentConcreteType_returnsFalse() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("read book", LocalDateTime.of(2026, 9, 1, 9, 0));
        SpecializedTodo specializedTodo = new SpecializedTodo("read book");

        assertFalse(todo.hasSameDetails(null));
        assertFalse(todo.hasSameDetails(deadline));
        assertFalse(deadline.hasSameDetails(todo));
        assertFalse(todo.hasSameDetails(specializedTodo));
        assertFalse(specializedTodo.hasSameDetails(todo));
    }

    @Test
    void completion_repeatedMarkAndUnmark_keepsStatusAndDisplayConsistent() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());

        for (int repetition = 0; repetition < 2; repetition++) {
            todo.markAsDone();
            assertTrue(todo.isDone());
            assertEquals("X", todo.getStatusIcon());
            assertEquals("[T][X] read book", todo.toString());
        }
        for (int repetition = 0; repetition < 2; repetition++) {
            todo.markAsNotDone();
            assertFalse(todo.isDone());
            assertEquals(" ", todo.getStatusIcon());
            assertEquals("[T][ ] read book", todo.toString());
        }
    }

    @Test
    void occursOn_unscheduledTask_neverMatchesDateRegardlessOfCompletion() {
        Todo todo = new Todo("read book");

        for (LocalDate date : List.of(LocalDate.MIN, LocalDate.of(2028, 2, 29), LocalDate.MAX)) {
            assertFalse(todo.occursOn(date));
        }
        todo.markAsDone();
        assertFalse(todo.occursOn(LocalDate.of(2028, 2, 29)));
    }

    /**
     * Supplies a distinct concrete task type for exact-type comparisons.
     */
    private static final class SpecializedTodo extends Todo {
        private SpecializedTodo(String description) {
            super(description);
        }
    }
}
