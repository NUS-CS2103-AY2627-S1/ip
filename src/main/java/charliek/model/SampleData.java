package charliek.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import charliek.parser.DateTimeParser;

/**
 * Provides the starter tasks shown to a user on the first application run.
 */
public final class SampleData {
    /**
     * The first date used by the starter deadline and event examples.
     */
    private static final LocalDate FIRST_SAMPLE_DATE = LocalDate.of(2099, 1, 10);

    /**
     * The date used by the date-time deadline example.
     */
    private static final LocalDate DEADLINE_SAMPLE_DATE = LocalDate.of(2099, 1, 20);

    /**
     * The start of the date-time event example.
     */
    private static final LocalDateTime EVENT_SAMPLE_START = LocalDateTime.of(2099, 1, 12, 9, 0);

    /**
     * The end of the date-time event example.
     */
    private static final LocalDateTime EVENT_SAMPLE_END = LocalDateTime.of(2099, 1, 12, 10, 30);

    private SampleData() {
        // Utility class.
    }

    /**
     * Creates a varied starter task list for a new user.
     *
     * @return seven sample tasks containing to-dos, deadlines, and events.
     */
    public static List<Task> create() {
        ToDo completedTask = new ToDo("Read the quick-start help");
        completedTask.markAsDone();

        return List.of(
                completedTask,
                new ToDo("Add your first personal task"),
                new Deadline("Plan the week's priorities", FIRST_SAMPLE_DATE.plusDays(5)),
                new Deadline("Submit an objective report", DEADLINE_SAMPLE_DATE.atTime(17, 0)),
                new Event("Morning briefing (no podium required)",
                        DateTimeParser.ParsedDateTime.ofDate(FIRST_SAMPLE_DATE),
                        DateTimeParser.ParsedDateTime.ofDate(FIRST_SAMPLE_DATE.plusDays(1))),
                new Event("Deep work (hot takes muted)",
                        DateTimeParser.ParsedDateTime.ofDateTime(EVENT_SAMPLE_START),
                        DateTimeParser.ParsedDateTime.ofDateTime(EVENT_SAMPLE_END)),
                new ToDo("Drink water before another hot take"));
    }
}
