package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks natural dates at the command boundary, including persistence and clock sampling.
 */
class CheeckenNaturalDateTest {
    private static final Clock MONDAY = Clock.fixed(Instant.parse("2026-09-07T15:30:00Z"), ZoneOffset.UTC);

    @TempDir
    private Path directory;

    /**
     * Verifies get response with natural dates: resolve and survive next day reload.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void getResponse_naturalDates_resolveAndSurviveNextDayReload() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Cheecken chatbot = new Cheecken(file.toString(), MONDAY);
        assertTrue(chatbot.getResponse("deadline report /by ToMoRrOw").contains("(by: Sep 08 2026)"));
        assertTrue(chatbot.getResponse("deadline past /by today 0900").contains("Sep 07 2026 9:00 AM"));
        assertTrue(chatbot.getResponse("event meeting /from MON 0900 /to Monday 1000")
                .contains("from: Sep 14 2026 9:00 AM to: Sep 14 2026 10:00 AM"));
        assertTrue(chatbot.getResponse("deadline instant /by now").contains("Sep 07 2026 3:30 PM"));
        chatbot.getResponse("mark 1");
        assertEquals("""
                D | 1 | report | 2026-09-08
                D | 0 | past | 2026-09-07T09:00
                E | 0 | meeting | 2026-09-14T09:00 | 2026-09-14T10:00
                D | 0 | instant | 2026-09-07T15:30
                """, Files.readString(file));
        Clock nextDay = Clock.offset(MONDAY, java.time.Duration.ofDays(1));
        Cheecken reopened = new Cheecken(file.toString(), nextDay);
        assertEquals(chatbot.getResponse("list"), reopened.getResponse("list"));
    }

    /**
     * Verifies get response with invalid date or flag: reports concise error without mutation.
     * @param input input supplied by the test case
     */
    @ParameterizedTest
    @ValueSource(strings = {"deadline bad /by yesterday", "deadline bad /by now 0900",
        "deadline bad /BY tomorrow", "deadline bad /byx tomorrow", "deadline bad /by tomorrow 2400",
        "event bad /FROM today /to tomorrow", "event bad /from today /TO tomorrow",
        "event bad /from today /to next week", "event bad /to today /from tomorrow"})
    void getResponse_invalidDateOrFlag_reportsConciseErrorWithoutMutation(String input) {
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString(), MONDAY);
        chatbot.getResponse("todo keep");
        String reply = chatbot.getResponse(input);
        assertTrue(reply.startsWith("No time how I set the task...\n"), reply);
        assertTrue(reply.contains("today") && reply.contains("tomorrow") && reply.contains("Mon"), reply);
        assertTrue(reply.length() < 240, reply);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep", chatbot.getResponse("list"));
    }

    /**
     * Verifies get response with event endpoints: resolve independently without ordering restriction.
     */
    @Test
    void getResponse_eventEndpoints_resolveIndependentlyWithoutOrderingRestriction() {
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString(), MONDAY);
        assertTrue(chatbot.getResponse("event reverse /from tomorrow /to today")
                .contains("from: Sep 08 2026 to: Sep 07 2026"));
        assertTrue(chatbot.getResponse("event mixed /from today /to 7/9/2026 0000")
                .contains("from: Sep 07 2026 to: Sep 07 2026 12:00 AM"));
    }

    /**
     * Verifies get response with clock crosses midnight: uses one instant per command.
     */
    @Test
    void getResponse_clockCrossesMidnight_usesOneInstantPerCommand() {
        Clock clock = new AdvancingClock();
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString(), clock);
        assertTrue(chatbot.getResponse("event boundary /from now /to now")
                .contains("from: Dec 31 2026 11:59 PM to: Dec 31 2026 11:59 PM"));
        assertTrue(chatbot.getResponse("deadline next command /by today").contains("(by: Jan 01 2027)"));
    }

    /**
     * Advances a second on each read to expose multiple clock reads within a command.
     */
    private static class AdvancingClock extends Clock {
        private int reads;

        /**
         * Returns the timezone used by the advancing test clock.
         * @return UTC, the timezone used by this test clock
         */
        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        /**
         * Creates a fixed test clock in the requested timezone.
         * @param zone timezone for the returned clock
         * @return fixed clock using the supplied timezone
         */
        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(Instant.parse("2026-12-31T23:59:59Z"), zone);
        }

        /**
         * Returns the next instant from the advancing test clock.
         * @return next instant, advancing one second on each call
         */
        @Override
        public Instant instant() {
            return Instant.parse("2026-12-31T23:59:59Z").plusSeconds(reads++);
        }
    }
}
