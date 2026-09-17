package orbit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests which task details distinguish new work from a duplicate. */
public class DuplicateTaskTest {
    @Test
    public void containsDuplicate_completedAndLoadedTodo_matchesNormalizedDescription() {
        Todo existing = new Todo("Read book");
        existing.markAsDone();
        TaskList tasks = new TaskList(List.of(existing));

        assertTrue(tasks.containsDuplicate(new Todo("  READ BOOK  ")));
        assertFalse(tasks.containsDuplicate(new Todo("Read another book")));
        assertFalse(tasks.containsDuplicate(new Todo("Read  book")));
        assertFalse(new TaskList().containsDuplicate(existing));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentTypes_doesNotMatch() {
        List<Task> tasks = List.of(new Todo("Read book"),
                new Deadline("Read book", "2026-09-15"), new Event("Read book", "2pm", "3pm"));

        for (int i = 0; i < tasks.size(); i++) {
            assertFalse(tasks.get(i).isDuplicateOf(null));
            for (int j = 0; j < tasks.size(); j++) {
                if (i != j) {
                    assertFalse(tasks.get(i).isDuplicateOf(tasks.get(j)));
                }
            }
        }
    }

    @Test
    public void isDuplicateOf_deadline_requiresMatchingDateAndDescription() {
        Deadline existing = new Deadline("Read book", "2026-09-15");

        assertTrue(existing.isDuplicateOf(new Deadline(" read BOOK ", "2026-09-15")));
        assertFalse(existing.isDuplicateOf(new Deadline("Read book", "2026-09-16")));
        assertFalse(existing.isDuplicateOf(new Deadline("Return book", "2026-09-15")));
    }

    @Test
    public void isDuplicateOf_event_requiresBothTimesAndDescription() {
        Event existing = new Event("Meeting", "Mon 2pm", "Mon 3pm");

        assertTrue(existing.isDuplicateOf(new Event(" meeting ", " MON 2PM ", " mon 3PM ")));
        assertFalse(existing.isDuplicateOf(new Event("Meeting", "Mon 1pm", "Mon 3pm")));
        assertFalse(existing.isDuplicateOf(new Event("Meeting", "Mon 2pm", "Mon 4pm")));
        assertFalse(existing.isDuplicateOf(new Event("Lunch", "Mon 2pm", "Mon 3pm")));
        assertFalse(existing.isDuplicateOf(new Event("Meeting", "Mon 14:00", "Mon 15:00")));
    }
}
