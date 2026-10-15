package orion.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import orion.task.TaskList;

class ResponseFormatterTest {
    @Test
    public void getTaskListMessage_emptyList_returnsEmptyMessage() {
        ResponseFormatter formatter = new ResponseFormatter();
        TaskList tasks = new TaskList();
        assertEquals("You currently do not have any tasks!", formatter.getTaskListMessage(tasks));
    }

    @Test
    public void getWelcomeMessage_returnsCorrectMessage() {
        ResponseFormatter formatter = new ResponseFormatter();
        String expected = "Hello! I'm Orion, your friendly chatbot.\nWhat can I do for you?";
        assertEquals(expected, formatter.getWelcomeMessage());
    }
}
