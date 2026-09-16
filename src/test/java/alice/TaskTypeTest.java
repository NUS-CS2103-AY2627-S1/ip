package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTypeTest {

    @Test
    void fromTask_returnsCorrectType() {
        Task todo = new ToDos("read book");
        Task deadline = new Deadlines("return book", LocalDate.parse("2024-12-25"));
        Task event = new Events("meeting", LocalDate.parse("2024-12-20"), LocalDate.parse("2024-12-22"));

        assertEquals(TaskType.TODO, TaskType.fromTask(todo));
        assertEquals(TaskType.DEADLINE, TaskType.fromTask(deadline));
        assertEquals(TaskType.EVENT, TaskType.fromTask(event));
    }

    @Test
    void getIcon_returnsCorrectString() {
        assertEquals("[T]", TaskType.TODO.getIcon());
        assertEquals("[D]", TaskType.DEADLINE.getIcon());
        assertEquals("[E]", TaskType.EVENT.getIcon());
    }

    @Test
    void getCommand_returnsCorrectKeyword() {
        assertEquals("todo", TaskType.TODO.getCommand());
        assertEquals("deadline", TaskType.DEADLINE.getCommand());
        assertEquals("event", TaskType.EVENT.getCommand());
    }
}
