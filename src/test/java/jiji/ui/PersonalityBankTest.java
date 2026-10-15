package jiji.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.Todo;

/**
 * Unit tests for {@link PersonalityBank}.
 */
public class PersonalityBankTest {

    @BeforeEach
    public void setUp() {
        PersonalityBank.setRandomized(false);
    }

    @AfterEach
    public void tearDown() {
        PersonalityBank.setRandomized(false);
    }

    @Test
    public void setRandomized_toggleMode_updatesState() {
        assertFalse(PersonalityBank.isRandomized());
        PersonalityBank.setRandomized(true);
        assertTrue(PersonalityBank.isRandomized());
    }

    @Test
    public void getGreeting_canonicalMode_returnsFirstGreeting() {
        String greeting = PersonalityBank.getGreeting();
        assertNotNull(greeting);
        assertTrue(greeting.contains("warm spot for you"));
    }

    @Test
    public void getWelcomeLines_returnsCozyLines() {
        assertEquals(2, PersonalityBank.getWelcomeLines().size());
        assertTrue(PersonalityBank.getWelcomeLines().get(0).contains("Purr... Welcome back!"));
    }

    @Test
    public void formatTaskAdded_canonicalMode_formatsCorrectly() {
        Todo todo = new Todo("drink warm tea");
        String result = PersonalityBank.formatTaskAdded(todo, 1);
        assertTrue(result.contains("Tucked away safely!"));
        assertTrue(result.contains("drink warm tea"));
        assertTrue(result.contains("1 tasks in our cozy bundle"));
    }

    @Test
    public void formatTaskMarked_canonicalMode_formatsCorrectly() {
        Todo todo = new Todo("drink warm tea");
        todo.markAsDone();
        String result = PersonalityBank.formatTaskMarked(todo);
        assertTrue(result.contains("Paws up! Marked this task as done:"));
        assertTrue(result.contains("Time for a gentle stretch."));
    }

    @Test
    public void formatTaskUnmarked_canonicalMode_formatsCorrectly() {
        Todo todo = new Todo("drink warm tea");
        String result = PersonalityBank.formatTaskUnmarked(todo);
        assertTrue(result.contains("No hurry at all! I've marked this task as pending again:"));
    }

    @Test
    public void formatTaskRemoved_canonicalMode_formatsCorrectly() {
        Todo todo = new Todo("drink warm tea");
        String result = PersonalityBank.formatTaskRemoved(todo, 0);
        assertTrue(result.contains("Gently cleared away! I've removed this task:"));
        assertTrue(result.contains("0 cozy tasks in your list."));
    }

    @Test
    public void formatGoodbye_canonicalMode_returnsFirstGoodbye() {
        String goodbye = PersonalityBank.formatGoodbye();
        assertTrue(goodbye.contains("Purrs and gentle head-bumps!"));
    }

    @Test
    public void formatEmptySchedule_canonicalMode_returnsCozyEmptyNotice() {
        String scheduleNotice = PersonalityBank.formatEmptySchedule("Aug 30 2026");
        assertEquals("No tasks scheduled for Aug 30 2026. A purr-fectly peaceful day to rest! ₍^ ᵕ ᵕ ^₎",
                scheduleNotice);
    }

    @Test
    public void formatMethods_randomizedMode_returnsNonNullContent() {
        PersonalityBank.setRandomized(true);
        Todo todo = new Todo("cozy catnap");
        assertNotNull(PersonalityBank.getGreeting());
        assertNotNull(PersonalityBank.formatTaskAdded(todo, 2));
        assertNotNull(PersonalityBank.formatTaskMarked(todo));
        assertNotNull(PersonalityBank.formatTaskUnmarked(todo));
        assertNotNull(PersonalityBank.formatTaskRemoved(todo, 1));
        assertNotNull(PersonalityBank.formatGoodbye());
        assertNotNull(PersonalityBank.formatEmptySchedule("tomorrow"));
    }
}
