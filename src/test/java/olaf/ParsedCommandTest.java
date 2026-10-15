package olaf;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests validation of parsed-command factories and payload accessors.
 */
class ParsedCommandTest {
    @Test
    void createWithoutPayload_actionRequiresPayload_illegalArgumentExceptionThrown() {
        for (ParsedCommand.Action action : ParsedCommand.Action.values()) {
            if (action == ParsedCommand.Action.EXIT || action == ParsedCommand.Action.LIST) {
                continue;
            }

            assertThrows(IllegalArgumentException.class,
                    () -> ParsedCommand.createWithoutPayload(action), action.name());
        }
    }

    @Test
    void createForTaskNumber_actionDoesNotUseTaskNumber_illegalArgumentExceptionThrown() {
        for (ParsedCommand.Action action : ParsedCommand.Action.values()) {
            if (action == ParsedCommand.Action.MARK || action == ParsedCommand.Action.UNMARK
                    || action == ParsedCommand.Action.DELETE) {
                continue;
            }

            assertThrows(IllegalArgumentException.class,
                    () -> ParsedCommand.createForTaskNumber(action, 1), action.name());
        }
    }

    @Test
    void payloadFactories_nullPayload_nullPointerExceptionThrown() {
        LocalDate date = LocalDate.of(2026, 9, 1);

        assertThrows(NullPointerException.class, () -> ParsedCommand.add(null));
        assertThrows(NullPointerException.class, () -> ParsedCommand.find(null));
        assertThrows(NullPointerException.class, () -> ParsedCommand.rescheduleDeadline(1, null));
        assertThrows(NullPointerException.class,
                () -> ParsedCommand.rescheduleEvent(1, null, date));
        assertThrows(NullPointerException.class,
                () -> ParsedCommand.rescheduleEvent(1, date, null));
    }

    @Test
    void payloadGetters_payloadDoesNotBelongToAction_illegalStateExceptionThrown() {
        ParsedCommand listCommand = ParsedCommand.createWithoutPayload(ParsedCommand.Action.LIST);
        ParsedCommand deadlineCommand = ParsedCommand.rescheduleDeadline(
                1, LocalDate.of(2026, 9, 1));

        assertThrows(IllegalStateException.class, listCommand::getTask);
        assertThrows(IllegalStateException.class, listCommand::getTaskNumber);
        assertThrows(IllegalStateException.class, listCommand::getKeyword);
        assertThrows(IllegalStateException.class, listCommand::getScheduledDate);
        assertThrows(IllegalStateException.class, deadlineCommand::getEndDate);
    }
}
