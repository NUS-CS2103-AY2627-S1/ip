package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import exception.FriedbergException;

/**
 * Tests for the {@link TaskStringParser} class.
 */
class TaskStringParserTest {
    @Test
    void taskStringParser_serializeTasks_returnsCorrectData() throws FriedbergException {
        List<Task> tasks = List.of(
                new ToDo("read book"),
                new Deadline("submit ip", "2026-09-09"),
                new Event("project meeting", "2026-09-09", "2026-09-10"));

        String data = TaskStringParser.serializeTasks(tasks);

        assertEquals("read book,P,T\n"
                + "submit ip,P,D,2026-09-09\n"
                + "project meeting,P,E,2026-09-09,2026-09-10\n", data);
    }

    @Test
    void taskStringParser_deserializeTasks_returnsCorrectTasks() throws FriedbergException {
        String data = "read book,P,T\n"
                + "submit ip,D,D,2026-09-09\n"
                + "project meeting,P,E,2026-09-09,2026-09-10";

        ArrayList<Task> tasks = TaskStringParser.deserializeTasks(data);

        assertEquals(3, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("read book,P,T", tasks.get(0).serialize());
        assertEquals("submit ip,D,D,2026-09-09", tasks.get(1).serialize());
        assertEquals("project meeting,P,E,2026-09-09,2026-09-10", tasks.get(2).serialize());
    }

    @Test
    void taskStringParser_deserializeEmptyString_returnsEmptyList() throws FriedbergException {
        ArrayList<Task> tasks = TaskStringParser.deserializeTasks("");

        assertEquals(0, tasks.size());
    }
}
