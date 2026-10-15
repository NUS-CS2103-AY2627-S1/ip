package magnus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.task.DeadlineTask;
import magnus.task.EventTask;
import magnus.task.Task;
import magnus.task.ToDoTask;

/**
 * Tests conversion of serialized task records into task objects.
 */
public class TaskDataParserTest {
    private final TaskDataParser parser = new TaskDataParser();

    @Test
    public void parseTask_todoRecord_returnsTodoTask() {
        Task task = this.parser.parseTask("T,0,read book");

        assertInstanceOf(ToDoTask.class, task);
        assertEquals("T,0,read book", task.serialize());
    }

    @Test
    public void parseTask_completedDeadlineRecord_returnsCompletedDeadlineTask() {
        Task task = this.parser.parseTask("D,1,submit report,02/09/2026 1500");

        assertInstanceOf(DeadlineTask.class, task);
        assertEquals("[D][X] submit report (by: Sep 02, 2026 15:00)", task.toString());
    }

    @Test
    public void parseTask_eventRecord_returnsEventTask() {
        Task task = this.parser.parseTask(
                "E,0,meeting,02/09/2026 1500-02/09/2026 1600");

        assertInstanceOf(EventTask.class, task);
        assertEquals("[E][ ] meeting (from: Sep 02, 2026 15:00 to: Sep 02, 2026 16:00)",
                task.toString());
    }

    @Test
    public void parseTask_eventRecordWithEscapedCharacter_decodesEventTime() {
        Task task = this.parser.parseTask(
                "E,0,meeting,02/09/2026 15\\00-02/09/2026 1600");

        assertEquals("E,0,meeting,02/09/2026 1500-02/09/2026 1600", task.serialize());
    }

    @Test
    public void parseTask_unknownTaskType_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseTask("X,0,read book"));
    }

    @Test
    public void parseTask_invalidCompletionStatus_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseTask("T,complete,read book"));
    }

    @Test
    public void parseTask_incorrectFieldCount_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseTask("D,0,submit report"));
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseTask("T,0,read book,extra"));
    }

    @Test
    public void parseTask_missingTypeOrStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask("T"));
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(""));
    }

    @Test
    public void parseTask_blankRequiredField_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask("T,0,   "));
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask("D,0,report,   "));
    }

    @Test
    public void parseTask_nonExistentDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                this.parser.parseTask("D,0,submit report,30/02/2026 1500"));
    }

    @Test
    public void parseTask_nonIncreasingEventTimes_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(
                "E,0,meeting,02/09/2026 1600-02/09/2026 1500"));
    }

    @Test
    public void parseTask_eventTimeWithoutBothParts_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(
                "E,0,meeting,02/09/2026 1500"));
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(
                "E,0,meeting,-02/09/2026 1600"));
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(
                "E,0,meeting,02/09/2026 1500-"));
    }

    @Test
    public void parseTask_eventTimeWithIncompleteEscape_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.parser.parseTask(
                "E,0,meeting,02/09/2026 1500-02/09/2026 1600\\"));
    }
}
