package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.exception.EmptyPayloadException;
import gatsby.model.Event;
import gatsby.model.TaskList;
import gatsby.testutil.RecordingUi;

/** Tests event time-range parsing, creation, and validation. */
class EventCommandTest extends AbstractCommandTest {

    /** Verifies that a valid event creates the expected typed task. */
    @Test
    void execute_validEvent_addsEventAndReportsSuccess() throws Exception {
        TaskList tasks = new TaskList();
        RecordingUi ui = recordingUi();

        new EventCommand("project meeting /from 2019-12-02 1400 /to 2019-12-02 1600")
                .execute(tasks, ui);

        assertEquals(1, tasks.size());
        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("project meeting", event.getTaskName());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 14:00:00 "
                + "to: Dec 02 2019 16:00:00)", event.toString());
        assertEquals(List.of(" Got it. I've added this task:",
                "  [E][ ] project meeting (from: Dec 02 2019 14:00:00 to: Dec 02 2019 16:00:00)",
                " Now you have 1 task in the list."), ui.messages());
    }

    /** Verifies that an event without a start keyword is rejected. */
    @Test
    void execute_missingFromKeyword_throwsException() {
        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new EventCommand("project meeting").execute(new TaskList(), recordingUi()));

        assertEquals(" son there's no event name/timing for this event -_-!", exception.getMessage());
    }

    /** Verifies that an event without an end keyword is rejected. */
    @Test
    void execute_missingToKeyword_throwsException() {
        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new EventCommand("project meeting /from 2019-12-02 1400")
                        .execute(new TaskList(), recordingUi()));

        assertEquals(" son this event has no end time, it's infinite! -_-!", exception.getMessage());
    }

    /** Verifies that malformed event times are not silently accepted. */
    @Test
    void execute_invalidDate_throwsUserFacingException() {
        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new EventCommand("meeting /from 2019-02-30 1400 /to 2019-02-30 1600")
                        .execute(new TaskList(), recordingUi()));

        assertEquals(" OOPS! Please enter a valid event start date and time in the format "
                + "yyyy-MM-dd HHmm (for example, 2019-12-02 1800).", exception.getMessage());
    }

    /** Verifies that an event cannot have an empty or backwards time range. */
    @Test
    void execute_nonIncreasingTimeRange_throwsUserFacingException() {
        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new EventCommand("meeting /from 2019-12-02 1600 /to 2019-12-02 1400")
                        .execute(new TaskList(), recordingUi()));

        assertEquals(" OOPS! An event's end time must be later than its start time.", exception.getMessage());
    }

    /** Verifies that repeating an event parameter is rejected. */
    @Test
    void execute_repeatedFromParameter_throwsUserFacingException() {
        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new EventCommand("meeting /from 2019-12-02 1400 /from 2019-12-02 1500 "
                        + "/to 2019-12-02 1600").execute(new TaskList(), recordingUi()));

        assertEquals(" OOPS! The \"/from\" parameter can only be specified once.", exception.getMessage());
    }
}
