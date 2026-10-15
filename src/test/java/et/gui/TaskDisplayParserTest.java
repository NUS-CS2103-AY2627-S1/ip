package et.gui;

import et.task.TaskType;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests extraction of task details from ET response lines.
 */
class TaskDisplayParserTest {
    @Test
    void parseTaskLine_numberedCompletedDeadline_returnsDisplayDetails() {
        Optional<TaskDisplayParser.TaskDisplay> result = TaskDisplayParser.parseTaskLine(
                "     2.[D][✓] return book (by: Dec 02 2019 6:00 PM)");

        assertTrue(result.isPresent());
        TaskDisplayParser.TaskDisplay display = result.get();
        assertEquals("2", display.taskNumber());
        assertEquals(TaskType.DEADLINE, display.taskType());
        assertTrue(display.isDone());
        assertEquals("return book", display.description());
        assertEquals(1, display.scheduleDetails().size());
        assertEquals("DUE", display.scheduleDetails().get(0).label());
        assertEquals("Dec 02 2019 6:00 PM", display.scheduleDetails().get(0).value());
    }

    @Test
    void parseTaskLineUnnumberedIncompleteTodo_returnsDisplayDetails() {
        Optional<TaskDisplayParser.TaskDisplay> result = TaskDisplayParser.parseTaskLine("[T][ ] phone home");

        assertTrue(result.isPresent());
        assertEquals("", result.get().taskNumber());
        assertEquals(TaskType.TODO, result.get().taskType());
        assertFalse(result.get().isDone());
        assertEquals("phone home", result.get().description());
        assertTrue(result.get().scheduleDetails().isEmpty());
    }

    @Test
    void parseTaskLineIncompleteEvent_returnsSeparateScheduleDetails() {
        Optional<TaskDisplayParser.TaskDisplay> result = TaskDisplayParser.parseTaskLine(
                "3.[E][ ] project meeting (from: Dec 02 2019 9:00 AM to: Dec 02 2019 10:00 AM)");

        assertTrue(result.isPresent());
        TaskDisplayParser.TaskDisplay display = result.get();
        assertEquals("project meeting", display.description());
        assertEquals(2, display.scheduleDetails().size());
        assertEquals("FROM", display.scheduleDetails().get(0).label());
        assertEquals("Dec 02 2019 9:00 AM", display.scheduleDetails().get(0).value());
        assertEquals("TO", display.scheduleDetails().get(1).label());
        assertEquals("Dec 02 2019 10:00 AM", display.scheduleDetails().get(1).value());
    }

    @Test
    void parseTaskLine_regularResponseText_returnsEmptyValue() {
        assertTrue(TaskDisplayParser.parseTaskLine("I found these in our little Earth mission:").isEmpty());
    }
}
