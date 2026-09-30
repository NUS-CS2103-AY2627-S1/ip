package kong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructorRejectsDescriptionThatWasNotValidated() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }

    @Test
    void deadlineConstructorRejectsMissingValidatedDate() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void eventConstructorRejectsMissingValidatedDates() {
        LocalDate validDate = LocalDate.parse("2019-10-15");

        assertThrows(AssertionError.class, () -> new Event("conference", null, validDate));
        assertThrows(AssertionError.class, () -> new Event("conference", validDate, null));
    }

    @Test
    void constructorRejectsDescriptionWithForbiddenCharacters() {
        assertThrows(AssertionError.class, () -> new Todo("buy milk | bread"));
        assertThrows(AssertionError.class, () -> new Todo("buy milk\nbread"));
    }

    @Test
    void eventConstructorRejectsStartDateAfterEndDate() {
        LocalDate startDate = LocalDate.parse("2019-10-16");
        LocalDate endDate = LocalDate.parse("2019-10-14");

        assertThrows(AssertionError.class, () -> new Event("conference", startDate, endDate));
    }

    @Test
    void markAndUnmark_togglesCompletionStatus() {
        Task task = new Task("sample task");

        assertEquals("[ ] sample task", task.toString());
        task.mark();
        assertEquals("[X] sample task", task.toString());
        task.unmark();
        assertEquals("[ ] sample task", task.toString());
    }

    @Test
    void baseTask_defaultMethods_behaveCorrectly() {
        Task incomplete = new Task("sample task");
        Task complete = new Task("sample task", true);

        assertFalse(incomplete.occursOn(LocalDate.parse("2019-10-15")));
        assertEquals(Optional.empty(), incomplete.getSortDate());
        assertEquals("? | 0 | sample task", incomplete.toFileString());
        assertEquals("? | 1 | sample task", complete.toFileString());
    }

    @Test
    void isSameTaskAndEquals_contract_validatesEquality() {
        Task task1 = new Task("sample task", false);
        Task task2 = new Task("SAMPLE TASK", false);
        Task taskDone = new Task("sample task", true);
        Task taskDiff = new Task("other task", false);

        assertTrue(task1.isSameTask(task2));
        assertFalse(task1.isSameTask(taskDiff));
        assertFalse(task1.isSameTask(null));
        assertFalse(task1.isSameTask(new Todo("sample task")));

        assertEquals(task1, task1);
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());

        assertNotEquals(task1, taskDone);
        assertNotEquals(task1, taskDiff);
        assertNotEquals(task1, null);
        assertNotEquals(task1, "string");
    }
}
