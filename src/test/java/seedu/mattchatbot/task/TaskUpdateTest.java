package seedu.mattchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.mattchatbot.MattChatBotException;

/**
 * Tests that each task type accepts the update markers that suit it, refuses
 * the ones that do not, and is left exactly as it was by a refused update.
 */
public class TaskUpdateTest {

    private static LocalDateTime at(int year, int month, int day, int hour) {
        return LocalDateTime.of(year, month, day, hour, 0);
    }

    private static TaskUpdate describedAs(String description) {
        return new TaskUpdate(description, null, null, null);
    }

    @Test
    public void applyUpdate_descriptionOnTodo_descriptionChanged() throws Exception {
        Todo todo = new Todo("read book");
        todo.applyUpdate(describedAs("read two books"));
        assertEquals("[T][ ] read two books", todo.toString());
    }

    @Test
    public void applyUpdate_dueDateOnDeadline_dueDateChanged() throws Exception {
        Deadline deadline = new Deadline("return book", at(2019, 10, 15, 0));
        deadline.applyUpdate(new TaskUpdate(null, at(2019, 12, 1, 0), null, null));
        assertEquals("[D][ ] return book (by: Dec 01 2019)", deadline.toString());
    }

    @Test
    public void applyUpdate_endTimeOnEvent_startTimeUnchanged() throws Exception {
        Event event = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        event.applyUpdate(new TaskUpdate(null, null, null, at(2019, 10, 15, 18)));
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)",
                event.toString());
    }

    @Test
    public void applyUpdate_doneTask_stillDone() throws Exception {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.applyUpdate(describedAs("read two books"));
        assertEquals("[T][X] read two books", todo.toString());
    }

    @Test
    public void applyUpdate_dueDateOnTodo_refusedAndNothingChanged() {
        Todo todo = new Todo("read book");
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                todo.applyUpdate(new TaskUpdate("changed", at(2019, 12, 1, 0), null, null)));
        assertEquals("A todo has no due date, so there is no /by to amend.", e.getMessage());
        // The description was valid, but a refused update must not apply any
        // part of itself, or the user would be left with a half-changed task.
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void applyUpdate_startTimeOnTodo_refusedAndNothingChanged() {
        Todo todo = new Todo("read book");
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                todo.applyUpdate(new TaskUpdate("changed", null, at(2019, 12, 1, 0), null)));
        assertEquals("A todo has no start or end time, so there is no /from or /to to amend.",
                e.getMessage());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void applyUpdate_eventTimesOnDeadline_refusedAndNothingChanged() {
        Deadline deadline = new Deadline("return book", at(2019, 10, 15, 0));
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                deadline.applyUpdate(new TaskUpdate("changed", null, at(2019, 12, 1, 0), null)));
        assertEquals("A deadline has no start and end times. Use /by to amend when it falls due.",
                e.getMessage());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void applyUpdate_dueDateOnEvent_refusedAndNothingChanged() throws Exception {
        Event event = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                event.applyUpdate(new TaskUpdate("changed", at(2019, 12, 1, 0), null, null)));
        assertEquals("An event has no /by. Use /from and /to to amend when it runs.",
                e.getMessage());
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void applyUpdate_endMovedBeforeStart_refusedAndNothingChanged() throws Exception {
        Event event = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                event.applyUpdate(new TaskUpdate("changed", null, null, at(2019, 10, 15, 9))));
        assertEquals("An event must end after it starts, and this one would run from"
                + " Oct 15 2019, 2:00pm to Oct 15 2019, 9:00am.", e.getMessage());
        // Neither the end time nor the description may have been applied.
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void applyUpdate_startMovedAfterEnd_refusedAndNothingChanged() throws Exception {
        Event event = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        assertThrows(MattChatBotException.class, () ->
                event.applyUpdate(new TaskUpdate(null, null, at(2019, 10, 15, 18), null)));
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void applyUpdate_bothEventTimesMovedTogether_accepted() throws Exception {
        // The pair is judged as it would end up, not one end at a time, so a
        // shift that stays forwards is allowed even though the new start is
        // after the old end.
        Event event = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        event.applyUpdate(new TaskUpdate(null, null, at(2019, 10, 15, 18), at(2019, 10, 15, 20)));
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 6:00pm to: Oct 15 2019, 8:00pm)",
                event.toString());
    }
}
