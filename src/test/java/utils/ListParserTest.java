package utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import recordbase.exceptions.RecordException;
import recordbase.types.Priority;
import recordbase.types.RecordList;
import recordbase.utils.ListParser;

public class ListParserTest {
    private RecordList list;

    @BeforeEach
    void setUp() {
        list = new RecordList();
    }

    @Test
    void parseToDo_validCommandWithDate_itemAdded() {
        String command = "todo Buy milk 20260115 14:30";

        int index = ListParser.parseToDo(command, list);

        assertTrue(index == 0);
        assertTrue(list.getItem(index).toString().contains("Scheduled: 15 Jan 2026, 2:30 PM"));
    }

    @Test
    void parseToDo_validCommandWithoutTime_itemAdded() {
        String command = "todo Buy milk 20260115";

        int index = ListParser.parseToDo(command, list);

        assertTrue(index == 0);
        assertTrue(list.getItem(index).toString().contains("Scheduled: 15 Jan 2026, 12:00 AM"));
    }

    @Test
    void parseToDo_validCommand_itemAdded() {
        String command = "todo Buy milk";

        int index = ListParser.parseToDo(command, list);

        assertTrue(index == 0);
    }

    @Test
    void createListToDoFromLocalDT_withPriority_storesPriority() {
        int index = ListParser.parseToDo("todo Submit quiz /priority high", list);

        assertTrue(list.getItem(index).getPriority() == Priority.HIGH);
    }

    @Test
    void createListDeadlineFromLocalDT_withNumericPriority_storesPriority() {
        int index = ListParser.parseDeadline(
                "deadline Submit report /by 20260115 /priority 2", list);

        assertTrue(list.getItem(index).getPriority() == Priority.MEDIUM_HIGH);
    }

    @Test
    void createListEventFromLocalDT_withoutPriority_defaultsToMedium() {
        int index = ListParser.parseEvent(
                "event Meeting /from 20260115 /to 20260116", list);

        assertTrue(list.getItem(index).getPriority() == Priority.MEDIUM);
    }

    @Test
    void parseToDo_validCommandWithSpecialChars_itemAdded() {
        String command = "todo Buy milk '\"\'\"}><./where";

        int index = ListParser.parseToDo(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseToDo_nullCommand_exceptionThrown() {
        assertThrows(AssertionError.class, () -> ListParser.parseToDo(null, list));
    }

    @Test
    void parseToDo_noTaskGivenWithSpace_exceptionThrown() {
        String command = "todo ";

        assertThrows(RecordException.class, () -> ListParser.parseToDo(command, list));
    }

    @Test
    void parseToDo_noTaskGivenNoSpace_exceptionThrown() {
        String command = "todo";

        assertThrows(RecordException.class, () -> ListParser.parseToDo(command, list));
    }

    @Test
    void parseToDo_onlyPriorityOption_exceptionThrown() {
        assertThrows(RecordException.class, () -> ListParser.parseToDo("todo /priority 1", list));
    }

    @Test
    void parseToDo_unknownOption_exceptionThrown() {
        assertThrows(RecordException.class, () -> ListParser.parseToDo("todo Buy milk /where shops", list));
    }

    @Test
    void parseDeadline_validDateAndTime_itemAdded() {
        String command = "deadline Submit report /by 20260115 14:30";

        int index = ListParser.parseDeadline(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseDeadline_validDateWithoutTime_itemAdded() {
        String command = "deadline Submit report /by 20260115";

        int index = ListParser.parseDeadline(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseDeadline_invalidNoDateTime_exceptionThrown() {
        String command = "deadline Submit report";

        assertThrows(RecordException.class, () -> ListParser.parseDeadline(command, list));
    }

    @Test
    void parseDeadline_invalidCalendarDate_exceptionThrown() {
        // February 30 is not a valid date.
        String command = "deadline Submit report /by 20260230 14:30";

        assertThrows(RecordException.class, () -> ListParser.parseDeadline(command, list));
    }

    @Test
    void parseDeadline_invalidTime_exceptionThrown() {
        // 25:00 is not a valid time.
        String command = "deadline Submit report /by 20260115 25:00";

        assertThrows(RecordException.class, () -> ListParser.parseDeadline(command, list));
    }

    @Test
    void parseDeadline_invalidDateFormat_exceptionThrown() {
        String command = "deadline Submit report /by 15-01-2026 14:30";

        assertThrows(RecordException.class, () -> ListParser.parseDeadline(command, list));
    }

    @Test
    void parseEvent_validDateAndTime_itemAdded() {
        String command = "event Team meeting /from 20260115 14:30 /to 20260115 15:30";

        int index = ListParser.parseEvent(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseEvent_validDatesWithoutTimes_itemAdded() {
        String command = "event Conference /from 20260115 /to 20260116";

        int index = ListParser.parseEvent(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseEvent_optionsInDifferentOrder_itemAdded() {
        String command = "event /to 20260116 /priority high Team meeting /from 20260115";

        int index = ListParser.parseEvent(command, list);

        assertTrue(index == 0);
        assertTrue(list.getItem(index).getPriority() == Priority.HIGH);
    }

    @Test
    void parseEvent_validFromDateToDateTime_itemAdded() {
        String command = "event Video session /from 20260115 /to 20260115 10:30";

        int index = ListParser.parseEvent(command, list);

        assertTrue(index == 0);
    }

    @Test
    void parseEvent_dateOnlyEndBeforeStart_exceptionThrown() {
        String command = "event Running session /from 20260115 09:00 /to 20260115";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_invalidCommand_exceptionThrown() {
        String command = "event Team meeting";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_invalidStartDate_exceptionThrown() {
        String command = "event Team meeting /from 20261301 14:30 /to 20260115 15:30";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_invalidEndTime_exceptionThrown() {
        String command = "event Team meeting /from 20260115 14:30 /to 20260115 24:00";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));

    }

    @Test
    void parseEvent_duplicatePriority_exceptionThrown() {
        String command = "event Meeting /from 20260115 /priority 1 /to 20260116 /priority 2";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_endBeforeStart_exceptionThrown() {
        String command = "event Team meeting /from 20260115 15:30 /to 20260115 14:30";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_invalidDoubleFrom_itemHandlingIsVerified() {
        String command = "event Team meeting /from /from 20260115 15:30 /to /to 20260115 14:30";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }

    @Test
    void parseEvent_invalidDoubleTo_itemHandlingIsVerified() {
        String command = "event Team meeting /from /from 20260115 15:30 /to /to 20260115 14:30";

        assertThrows(RecordException.class, () -> ListParser.parseEvent(command, list));
    }
}
