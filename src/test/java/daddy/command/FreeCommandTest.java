package daddy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import daddy.exception.DaddyException;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Tests free-time command behavior that depends on the current time.
 */
class FreeCommandTest {
    /** Verifies that a partial current minute is rounded up before searching and displaying the result. */
    @Test
    void execute_currentTimeHasSeconds_roundsUpAndShowsDeterministicSlot() throws DaddyException {
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalDateTime currentTime = LocalDateTime.of(2026, 9, 14, 10, 15, 20);
        Clock clock = Clock.fixed(currentTime.atZone(zone).toInstant(), zone);
        FreeCommand command = new FreeCommand(Duration.ofHours(4), clock);
        List<String> output = new ArrayList<>();

        command.execute(new TaskList(), new Ui(output::add), null);

        assertEquals(List.of(
                " Daddy found you a recovery window:",
                "   Mon, 14 Sep 2026, 10:16\u201314:16",
                " That's a 4-hour slot. Protect it, then get back to the mission."), output);
    }

    /** Verifies that an exact-minute current time is not rounded into the following minute. */
    @Test
    void execute_currentTimeAtMinuteBoundary_keepsCurrentMinute() throws DaddyException {
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalDateTime currentTime = LocalDateTime.of(2026, 9, 14, 10, 15);
        Clock clock = Clock.fixed(currentTime.atZone(zone).toInstant(), zone);
        FreeCommand command = new FreeCommand(Duration.ofMinutes(90), clock);
        List<String> output = new ArrayList<>();

        command.execute(new TaskList(), new Ui(output::add), null);

        assertTrue(output.stream().anyMatch(line -> line.contains("10:15–11:45")));
    }

    /** Verifies that command construction rejects missing duration and clock collaborators. */
    @Test
    void constructor_missingRequiredValue_assertionThrown() {
        Clock clock = Clock.systemUTC();

        assertThrows(AssertionError.class, () -> new FreeCommand(null, clock));
        assertThrows(AssertionError.class, () -> new FreeCommand(Duration.ofHours(1), null));
    }
}
