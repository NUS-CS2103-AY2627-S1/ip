package milo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import milo.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.ToDo;

public class ParserTest {

    @Test
    public void parseTask_todoCommand_expectedToDoTask() throws MiloException {
        Task task = Parser.parseTask("todo read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("[T] [ ] read book", task.toString());
    }

    @Test
    public void parseTask_deadlineWithSlashDate_expectedDeadlineTask() throws MiloException {
        Task task = Parser.parseTask("deadline return book /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDeadlineDate());
    }

    @Test
    public void parseTask_eventWithSlashDates_expectedEventTask() throws MiloException {
        Task task = Parser.parseTask("event exam /from 2/12/2019 0900 /to 3/12/2019 1100");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | exam | 2019-12-02 0900 | 2019-12-03 1100",
                event.storageString());
    }

    @Test
    public void parseTask_missingTaskParts_expectedHelpfulMessages() {
        MiloException missingDeadline = assertThrows(MiloException.class, () ->
                Parser.parseTask("deadline return book"));
        assertEquals("A deadline without a deadline isn't really a deadline is it...",
                missingDeadline.getMessage());

        MiloException missingEventRange = assertThrows(MiloException.class, () ->
                Parser.parseTask("event exam /from 2019-12-02 0900"));
        assertEquals("Erm... An event has to start and end...", missingEventRange.getMessage());
    }

    @Test
    public void parseTask_invalidCommandFormat_expectedFormatMessage() {
        MiloException exception = assertThrows(MiloException.class, () ->
                Parser.parseTask("deadline return book /from 2019-12-02 1800"));

        assertEquals("Let's get that deadline sorted! Use deadline description /by yyyy-MM-dd HHmm.",
                exception.getMessage());
    }
}
