package duke.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import duke.Deadline;
import duke.Event;
import duke.Task;
import duke.TaskMatch;
import duke.Todo;
import org.junit.jupiter.api.Test;

/** Tests the pure text formatting used by the JavaFX conversation. */
class TaskFormatterTest {
    @Test
    void renderMatches_nonFirstResult_keepsOriginalNumberAndSearchHeading() {
        assertEquals("Matching tasks:\n\n3. [To do] read book",
                TaskFormatter.renderMatches(List.of(new TaskMatch(3, new Todo("read book")))));
        assertEquals("No matching tasks found.\n• Try a different keyword.\n• Type list to see all tasks.",
                TaskFormatter.renderMatches(List.of()));
    }

    @Test
    void renderTasks_formatsEmptyAndNumberedLists() {
        assertEquals("Your task list is empty.\n• Add your first task: todo read a book",
                TaskFormatter.renderTasks(List.of()));

        List<Task> tasks = List.of(new Todo("read book"), new Deadline("submit report", LocalDate.of(2026, 9, 20)));
        assertEquals("Your tasks:\n\n1. [To do] read book\n\n"
                        + "2. [To do] submit report\nDue: 20 Sep 2026", TaskFormatter.renderTasks(tasks));
    }

    @Test
    void formatProgressSummary_andHeaderStats_reportCompletion() {
        Task completed = new Todo("done");
        completed.markAsDone();
        Task pending = new Todo("next");
        List<Task> tasks = List.of(completed, pending);

        assertEquals("1 of 2 tasks done. One step at a time.", TaskFormatter.formatProgressSummary(tasks));
        assertEquals("2 TASKS  ·  1 DONE", TaskFormatter.formatHeaderStats(tasks));
        assertEquals("You've finished your task. Nice work!",
                TaskFormatter.formatProgressSummary(List.of(completed)));
        assertEquals("0 of 1 task done. One step at a time.",
                TaskFormatter.formatProgressSummary(List.of(pending)));
        assertEquals("No tasks yet. Add one whenever you're ready.",
                TaskFormatter.formatProgressSummary(List.of()));
    }

    @Test
    void formatTask_event_showsStatusAndTimesOnSeparateLines() {
        Task event = new Event("team meeting", "Monday 2pm", "Monday 4pm");
        event.markAsDone();

        assertEquals("[Done] team meeting\nFrom: Monday 2pm\nTo: Monday 4pm",
                TaskFormatter.formatTask(event));
    }

    @Test
    void formatDetails_deadlineAndEvent_hasNoSpaceBasedAlignment() {
        assertEquals(List.of("Due: 30 Sep 2026"),
                TaskFormatter.formatDetails(new Deadline("ST2334", LocalDate.of(2026, 9, 30))));
        assertEquals(List.of("From: Monday 2pm", "To: Monday 4pm"),
                TaskFormatter.formatDetails(new Event("meeting", "Monday 2pm", "Monday 4pm")));
        assertEquals(List.of(), TaskFormatter.formatDetails(new Todo("read")));
    }

    @Test
    void formatTitle_statusAndLiteralDescription_remainsIndependentOfDetails() {
        Task deadline = new Deadline("ST2334 <notes> & practice", LocalDate.of(2026, 9, 30));
        deadline.markAsDone();
        assertEquals("[Done] ST2334 <notes> & practice", TaskFormatter.formatTitle(deadline));
        assertEquals("[D][X] ST2334 <notes> & practice (by: Sep 30 2026)", deadline.toString());
    }

    @Test
    void formatTask_literalDescription_doesNotInterpretMarkupOrChangeStoredData() {
        Task task = new Todo("read `notes` & <draft>");

        assertEquals("[To do] read `notes` & <draft>", TaskFormatter.formatTask(task));
        assertEquals("[T][ ] read `notes` & <draft>", task.toString());
    }
}
