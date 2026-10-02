package ari;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ari.exception.EmptyArgumentException;
import ari.task.Task;

public class ParserTest {
    @Test
    public void parseTask_todoCommand_returnsTodoTask() throws EmptyArgumentException {
        Task task = Parser.parseTask("todo read book", CommandType.TODO);

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTask_deadlineWithRepeatedWhitespace_returnsDeadlineTask()
            throws EmptyArgumentException {
        String input = "deadline Deadline Test       /by        Sunday 12PM        ";

        Task task = Parser.parseTask(input, CommandType.DEADLINE);

        assertEquals("[D][ ] Deadline Test (by: Sunday 12PM)", task.toString());
    }

    @Test
    public void parseTask_deadlineWithIsoDate_storesAndFormatsDate()
            throws EmptyArgumentException {
        Task task = Parser.parseTask(
                "deadline return book /by 2019-12-02",
                CommandType.DEADLINE
        );

        assertEquals("[D][ ] return book (by: Dec 2 2019)", task.toString());
        assertEquals("D | 0 | return book | 2019-12-02", task.toDataString());
    }

    @Test
    public void parseTask_eventCommand_returnsEventTask() throws EmptyArgumentException {
        String input = "event project meeting /from Monday 2PM /to Monday 4PM";

        Task task = Parser.parseTask(input, CommandType.EVENT);

        assertEquals(
                "[E][ ] project meeting (from: Monday 2PM to: Monday 4PM)",
                task.toString()
        );
    }

    @Test
    public void parseTask_deadlineWithoutDescription_throwsEmptyArgumentException() {
        try {
            Parser.parseTask("deadline", CommandType.DEADLINE);
            fail();
        } catch (EmptyArgumentException e) {
            assertEquals(
                    "Oh no! You can't have an empty description for deadlines",
                    e.getMessage()
            );
        }
    }

    @Test
    public void parseTask_deadlineWithoutByField_throwsEmptyArgumentException() {
        try {
            Parser.parseTask("deadline read book", CommandType.DEADLINE);
            fail();
        } catch (EmptyArgumentException e) {
            assertEquals(
                    "Oh no! You can't have an empty description for deadlines",
                    e.getMessage()
            );
        }
    }

    @Test
    public void parseTask_eventWithoutToField_throwsEmptyArgumentException() {
        try {
            Parser.parseTask("event meeting /from Monday 2PM", CommandType.EVENT);
            fail();
        } catch (EmptyArgumentException e) {
            assertEquals(
                    "Oh no! You can't have an empty description for events",
                    e.getMessage()
            );
        }
    }

    @Test
    public void parseTask_nonTaskCommand_throwsIllegalArgumentException() {
        try {
            Parser.parseTask("list", CommandType.LIST);
            fail();
        } catch (EmptyArgumentException e) {
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Command does not create a task", e.getMessage());
        }
    }

    @Test
    public void parseFindKeyword_repeatedWhitespace_returnsEntireKeyword()
            throws EmptyArgumentException {
        String keyword = Parser.parseFindKeyword("  find    read book  ");

        assertEquals("read book", keyword);
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsEmptyArgumentException() {
        try {
            Parser.parseFindKeyword("find   ");
            fail();
        } catch (EmptyArgumentException e) {
            assertEquals(
                    "Oh no! You can't have an empty description for finding tasks",
                    e.getMessage()
            );
        }
    }
}
