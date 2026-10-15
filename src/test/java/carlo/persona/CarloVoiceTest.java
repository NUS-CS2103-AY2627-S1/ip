package carlo.persona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import carlo.exception.CarloException;
import carlo.task.Deadline;
import carlo.task.Task;
import carlo.task.Todo;

/**
 * Tests for {@link CarloVoice}, using a fixed time of Friday 9 Oct 2026,
 * 10:00am and a seeded random number generator so results are repeatable.
 */
class CarloVoiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 9, 10, 0);

    private final CarloVoice voice = new CarloVoice(new Random(42));

    @Test
    void greet_overdueTask_isConcernedAndNamesIt() throws CarloException {
        List<Task> tasks = List.of(new Deadline("assignment 1", "2026-09-20"), new Todo("clean up"));

        CarloMessage message = voice.greet(tasks, NOW);

        assertEquals(CarloMood.CONCERNED, message.mood());
        assertTrue(message.text().startsWith("Good morning!"));
        assertTrue(message.text().contains("\"assignment 1\" is overdue"));
    }

    @Test
    void greet_taskDueSoon_givesHeadsUp() throws CarloException {
        CarloMessage message = voice.greet(List.of(new Deadline("essay", "2026-10-10")), NOW);

        assertEquals(CarloMood.HEADS_UP, message.mood());
        assertTrue(message.text().contains("\"essay\" is coming up (due tomorrow)"));
    }

    @Test
    void greet_emptyList_isCheerful() {
        CarloMessage message = voice.greet(List.of(), NOW);

        assertEquals(CarloMood.CHEERFUL, message.mood());
        assertTrue(message.text().contains("Your list is clear"));
    }

    @Test
    void taskCompleted_lastOpenTask_mentionsRest() {
        CarloMessage message = voice.taskCompleted(new Todo("clean up"), 0);

        assertEquals(CarloMood.CHEERFUL, message.mood());
        assertTrue(message.text().contains("clean up"));
        assertTrue(message.text().contains("you've earned it"));
    }

    @Test
    void taskAdded_overdueDeadline_isConcerned() throws CarloException {
        CarloMessage message = voice.taskAdded(new Deadline("form", "2026-10-01"), NOW);

        assertEquals(CarloMood.CONCERNED, message.mood());
        assertTrue(message.text().startsWith("Got it! I've added \"form\" (8 days late)"));
    }
}
