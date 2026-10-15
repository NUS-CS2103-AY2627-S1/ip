package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import elora.EloraException;

class TaskFactoryTest {

    private final TaskFactory taskFactory = new TaskFactory();

    @Test
    void createTodo_validDescription_returnsMatchingTodo() throws EloraException {
        Todo todo = taskFactory.createTodo("read book");
        assertEquals(new Todo("read book"), todo);
    }

    @Test
    void createTodo_emptyDescription_throws() {
        assertThrows(EloraException.class, () -> taskFactory.createTodo(""));
    }

    @Test
    void createTodo_pipeCharacter_throws() {
        assertThrows(EloraException.class, () -> taskFactory.createTodo("read | book"));
    }

    @Test
    void createDeadline_validInput_returnsMatchingDeadline() throws EloraException {
        Deadline deadline = taskFactory.createDeadline("return book /by 2019-10-15");
        assertEquals(new Deadline("return book", LocalDate.parse("2019-10-15")), deadline);
    }

    @Test
    void createDeadline_missingByKeyword_throws() {
        assertThrows(EloraException.class, () -> taskFactory.createDeadline("return book"));
    }

    @Test
    void createDeadline_duplicateByKeyword_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createDeadline("return book /by 2019-10-15 /by 2019-10-20"));
    }

    @Test
    void createDeadline_nonExistentDate_throws() {
        assertThrows(EloraException.class, () -> taskFactory.createDeadline("return book /by 2019-02-30"));
    }

    @Test
    void createEvent_validInput_returnsMatchingEvent() throws EloraException {
        Event event = taskFactory.createEvent("meeting /from Mon 2pm /to 4pm");
        assertEquals(new Event("meeting", "Mon 2pm", "4pm"), event);
    }

    @Test
    void createEvent_missingToKeyword_throws() {
        assertThrows(EloraException.class, () -> taskFactory.createEvent("meeting /from Mon 2pm"));
    }

    @Test
    void createEvent_endDateBeforeStartDate_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createEvent("trip /from 2019-10-20 /to 2019-10-15"));
    }

    @Test
    void createEvent_freeTextTimes_skipsChronologicalCheckAndSucceeds() throws EloraException {
        Event event = taskFactory.createEvent("meeting /from 4pm /to 2pm");
        assertEquals(new Event("meeting", "4pm", "2pm"), event);
    }

    @Test
    void createEvent_validIsoDateRange_returnsMatchingEvent() throws EloraException {
        Event event = taskFactory.createEvent("trip /from 2029-01-01 /to 2029-01-02");
        assertEquals(new Event("trip", "2029-01-01", "2029-01-02"), event);
    }

    @Test
    void createEvent_fromLooksLikeDateButIsNotReal_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createEvent("trip /from 2029-01-32 /to 2029-02-01"));
    }

    @Test
    void createEvent_toLooksLikeDateButIsNotReal_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createEvent("trip /from 2029-01-01 /to 2029-01-32"));
    }

    @Test
    void createEvent_bothFromAndToLookLikeDatesButAreNotReal_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createEvent("test /from 2020-01-40 /to 2020-01-41"));
    }

    @Test
    void createEvent_nonExistentLeapDayShapedDate_throws() {
        assertThrows(EloraException.class, () ->
                taskFactory.createEvent("trip /from 2029-02-29 /to 2029-03-01"));
    }
}
