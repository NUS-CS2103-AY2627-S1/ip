package vermithor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Tests the task model used by Vermithor. */
class TaskTest {
    @Test
    void todoDisplaysItsTypeAndDescription() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    void deadlineDisplaysItsDate() {
        assertTrue(new Deadline("return book", LocalDate.of(2026, 6, 6))
                .toString().contains("return book"));
    }

    @Test
    void commandWordsAreCaseInsensitive() {
        assertEquals(CommandType.FIND, CommandType.fromCommandWord("FiNd"));
    }

    @Test
    void unknownCommandProducesUnknownType() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromCommandWord("not-a-command"));
    }

    @Test
    void markingTaskDoneChangesItsDisplayedStatus() {
        Task task = new ToDo("read book");
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
    }
}
