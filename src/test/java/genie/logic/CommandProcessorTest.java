package genie.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import genie.util.DateFormats;

public class CommandProcessorTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void addDeadline_inThePast_returnsError() {
        CommandProcessor processor = createProcessor();
        String date = formatDateTime(LocalDateTime.now().minusDays(1));

        String response = processor.processCommand("deadline submit report /by " + date);

        assertTrue(response.contains("must not be in the past"));
    }

    @Test
    public void addEvent_withStartAfterEnd_returnsError() {
        CommandProcessor processor = createProcessor();
        String start = formatDateTime(LocalDateTime.now().plusDays(2));
        String end = formatDateTime(LocalDateTime.now().plusDays(1));

        String response = processor.processCommand(
                "event meeting /from " + start + " /to " + end);

        assertTrue(response.contains("start time must be before its end time"));
    }

    @Test
    public void addEvent_inThePast_returnsError() {
        CommandProcessor processor = createProcessor();
        String start = formatDateTime(LocalDateTime.now().minusDays(2));
        String end = formatDateTime(LocalDateTime.now().minusDays(1));

        String response = processor.processCommand(
                "event meeting /from " + start + " /to " + end);

        assertTrue(response.contains("must not be in the past"));
    }

    @Test
    public void addDeadline_withInvalidCalendarDate_returnsError() {
        CommandProcessor processor = createProcessor();

        String response = processor.processCommand(
                "deadline submit report /by 31/9/2026 1800");

        assertTrue(response.contains("valid date and time"));
    }

    @Test
    public void markAlreadyCompletedTask_returnsError() {
        CommandProcessor processor = createProcessor();
        processor.processCommand("todo submit report");
        processor.processCommand("mark 1");

        String response = processor.processCommand("mark 1");

        assertTrue(response.contains("already marked as done"));
    }

    @Test
    public void unmarkIncompleteTask_returnsError() {
        CommandProcessor processor = createProcessor();
        processor.processCommand("todo submit report");

        String response = processor.processCommand("unmark 1");

        assertTrue(response.contains("already marked as undone"));
    }

    @Test
    public void editEvent_withEndBeforeStart_returnsError() {
        CommandProcessor processor = createProcessor();
        String start = formatDateTime(LocalDateTime.now().plusDays(1));
        String end = formatDateTime(LocalDateTime.now().plusDays(2));
        processor.processCommand("event meeting /from " + start + " /to " + end);

        String response = processor.processCommand("edit 1 to " + start);

        assertTrue(response.contains("start time must be before its end time"));
    }

    @Test
    public void editDeadline_inThePast_returnsError() {
        CommandProcessor processor = createProcessor();
        String futureDate = formatDateTime(LocalDateTime.now().plusDays(1));
        String pastDate = formatDateTime(LocalDateTime.now().minusDays(1));
        processor.processCommand("deadline submit report /by " + futureDate);

        String response = processor.processCommand("edit 1 by " + pastDate);

        assertTrue(response.contains("must not be in the past"));
    }

    private CommandProcessor createProcessor() {
        Path taskFile = tempDirectory.resolve("tasks.txt");
        return new CommandProcessor(taskFile.toString());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateFormats.INPUT_FORMAT);
    }
}
