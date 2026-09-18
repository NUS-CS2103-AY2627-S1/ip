package chausistant.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task formatting, completion state, and task-list collection operations. */
class TaskTest {

    @Test
    void todoTask_completionState_changesDisplayAndSaveFormats() {
        TodoTask task = new TodoTask("review | archive \\ draft");

        assertFalse(task.isCompleted());
        assertEquals("[T][ ] review | archive \\ draft", task.printTask());
        assertEquals("T | 0 | review \\| archive \\\\ draft", task.toSaveFormat());

        task.setCompleted(true);

        assertTrue(task.isCompleted());
        assertEquals("[T][X] review | archive \\ draft", task.printTask());
        assertEquals("T | 1 | review \\| archive \\\\ draft", task.toSaveFormat());
    }

    @Test
    void deadlineTask_dateOnlyAndTimedDeadlines_preserveTheirFormats() {
        DeadlineTask dateOnlyDeadline = new DeadlineTask("submit form",
                LocalDateTime.of(2026, 6, 8, 23, 59), false);
        DeadlineTask timedDeadline = new DeadlineTask("attend review",
                LocalDateTime.of(2026, 6, 8, 9, 5), true);

        assertEquals("[D][ ] submit form (by: Jun 8 2026)", dateOnlyDeadline.printTask());
        assertEquals("D | 0 | submit form | 08/06/2026", dateOnlyDeadline.toSaveFormat());
        assertEquals("[D][ ] attend review (by: Jun 8 2026 0905)", timedDeadline.printTask());
        assertEquals("D | 0 | attend review | 08/06/2026 0905", timedDeadline.toSaveFormat());
        assertEquals(LocalDateTime.of(2026, 6, 8, 9, 5), timedDeadline.getDeadline());
    }

    @Test
    void eventTask_mixedDateAndTimeFormats_preservesIntervalDetails() {
        EventTask task = new EventTask("camp", LocalDateTime.of(2026, 6, 8, 0, 0), false,
                LocalDateTime.of(2026, 6, 10, 18, 30), true);

        assertEquals("[E][ ] camp (from: Jun 8 2026 to: Jun 10 2026 1830)", task.printTask());
        assertEquals("E | 0 | camp | 08/06/2026 | 10/06/2026 1830", task.toSaveFormat());
        assertEquals(LocalDateTime.of(2026, 6, 8, 0, 0), task.getFrom());
        assertEquals(LocalDateTime.of(2026, 6, 10, 18, 30), task.getTo());
    }

    @Test
    void formatDateForDisplay_usesEnglishMonthName() {
        assertEquals("Dec 2 2019", Task.formatDateForDisplay(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void taskList_listConstructorCopiesInputAndSupportsIndexedChanges() {
        TodoTask firstTask = new TodoTask("first");
        TodoTask secondTask = new TodoTask("second");
        List<Task> sourceTasks = new ArrayList<>(List.of(firstTask));
        TaskList taskList = new TaskList(sourceTasks);
        sourceTasks.add(secondTask);

        taskList.add(1, secondTask);

        assertEquals(2, taskList.size());
        assertEquals(secondTask, taskList.get(1));
        assertEquals(firstTask, taskList.remove(0));
        assertEquals(List.of(secondTask), taskList.getTasks());
        assertThrows(UnsupportedOperationException.class, () ->
                taskList.getTasks().add(new TodoTask("third")));
    }
}
