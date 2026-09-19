package fox.parser;

import java.time.LocalDate;

import fox.exception.FoxException;
import fox.task.Deadline;
import fox.task.Event;
import fox.task.Task;
import fox.task.Todo;

/** Dependency-free tests for task command parsing. */
public class TaskParserTest {
    /** Runs parser tests with assertions enabled. */
    public static void main(String[] args) throws Exception {
        TaskParser parser = new TaskParser();
        assert parser.parse("todo", "todo read book") instanceof Todo;
        Task deadline = parser.parse("deadline", "deadline submit /by 2026-12-02");
        assert deadline instanceof Deadline;
        assert ((Deadline) deadline).getByDate().equals(LocalDate.of(2026, 12, 2));
        assert parser.parse("event", "event meeting /from 10am /to 11am") instanceof Event;
        try {
            parser.parse("deadline", "deadline submit /by 2026-02-30");
            throw new AssertionError("Invalid dates should be rejected");
        } catch (FoxException expected) {
            assert expected.getMessage().contains("valid date");
        }
    }
}
