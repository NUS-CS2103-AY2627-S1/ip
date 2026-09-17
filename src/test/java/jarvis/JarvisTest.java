package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests command parsing and validation performed by Jarvis. */
class JarvisTest {
    @Test
    void parseTask_todoCommand_returnsTodoTask() throws JarvisException {
        Task task = Jarvis.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseTask_deadlineCommand_returnsDeadlineTask() throws JarvisException {
        Task task = Jarvis.parseTask("deadline submit report /by 2026-09-20");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2026, 9, 20), deadline.getBy());
    }

    @Test
    void parseTask_eventCommand_returnsEventTask() throws JarvisException {
        Task task = Jarvis.parseTask("event project meeting /from Mon 2pm /to Mon 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("Mon 4pm", event.getTo());
    }

    @Test
    void parseTask_unknownCommand_throwsJarvisException() {
        JarvisException exception = assertThrows(JarvisException.class, () ->
                Jarvis.parseTask("unknown command"));

        assertEquals("I don't recognize that command. Try todo, deadline, event, list, find, mark, "
                + "unmark, delete, snooze, or bye.", exception.getMessage());
    }

    @Test
    void parseTaskNumber_nonNumericArgument_throwsJarvisException() {
        JarvisException exception = assertThrows(JarvisException.class, () ->
                Jarvis.parseTaskNumber("delete nope", "delete"));

        assertEquals("Please provide a valid task number after delete.", exception.getMessage());
    }

    @Test
    void parseTaskNumber_zeroArgument_throwsJarvisException() {
        assertThrows(JarvisException.class, () -> Jarvis.parseTaskNumber("delete 0", "delete"));
    }

    @Test
    void parseTask_blankCommand_throwsJarvisException() {
        assertThrows(JarvisException.class, () -> Jarvis.parseTask("   "));
    }

    @Test
    void parseTask_invalidDeadlineDate_throwsJarvisException() {
        JarvisException exception = assertThrows(JarvisException.class, () ->
                Jarvis.parseTask("deadline report /by 2026-02-30"));

        assertEquals("A deadline date must use yyyy-mm-dd, for example: 2019-10-15",
                exception.getMessage());
    }

    @Test
    void parseTask_eventWithMissingTime_throwsJarvisException() {
        assertThrows(JarvisException.class, () ->
                Jarvis.parseTask("event meeting /from 2pm /to"));
    }

    @Test
    void parseTask_deadlineWithEmptyDescription_throwsJarvisException() {
        assertThrows(JarvisException.class, () ->
                Jarvis.parseTask("deadline /by 2026-09-20"));
    }

    @Test
    void findMatchingTasks_keywordReturnsMatchingTasks() throws JarvisException {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("buy milk"),
                new Deadline("submit report", LocalDate.of(2026, 9, 20)));

        List<Task> matchingTasks = Jarvis.findMatchingTasks(tasks, "REPORT");

        assertEquals(1, matchingTasks.size());
        assertEquals("submit report", matchingTasks.get(0).getDescription());
    }

    @Test
    void findMatchingTasks_blankKeyword_throwsJarvisException() {
        assertThrows(JarvisException.class, () -> Jarvis.findMatchingTasks(List.of(), "   "));
    }

    @Test
    void snoozeTask_deadlineCommand_updatesDeadlineAndReturnsIndex() throws JarvisException {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 10));
        List<Task> tasks = new ArrayList<>(List.of(deadline));

        int taskIndex = Jarvis.snoozeTask("snooze 1 /by 2026-09-20", tasks);

        assertEquals(0, taskIndex);
        assertEquals(LocalDate.of(2026, 9, 20), deadline.getBy());
    }

    @Test
    void isDuplicate_sameTypeAndDescriptionIgnoringCase_returnsTrue() {
        assertTrue(Jarvis.isDuplicate(new Todo("Read book"), new Todo("read book")));
    }

    @Test
    void isDuplicate_differentTaskTypes_returnsFalse() {
        assertFalse(Jarvis.isDuplicate(new Todo("read book"),
                new Deadline("read book", LocalDate.of(2026, 9, 20))));
    }
}
