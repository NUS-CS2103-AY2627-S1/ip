package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import gnaix.task.Todo;

class GuiResponseTest {

    @Test
    void plain_text_plainResponseReturned() {
        GuiResponse response = GuiResponse.plain("Hello.");

        assertEquals("Hello.", response.getText());
        assertEquals("Hello.", response.getHeading());
        assertTrue(response.getFooter().isEmpty());
        assertTrue(response.getTasks().isEmpty());
        assertFalse(response.hasTasks());
        assertFalse(response.isError());
    }

    @Test
    void error_text_errorResponseReturned() {
        GuiResponse response = GuiResponse.error("No.");

        assertEquals("No.", response.getText());
        assertEquals("No.", response.getHeading());
        assertTrue(response.getTasks().isEmpty());
        assertFalse(response.hasTasks());
        assertTrue(response.isError());
    }

    @Test
    void withTasks_taskData_structuredResponseReturned() {
        TaskDisplay taskDisplay = new TaskDisplay(1, new Todo("buy milk"));

        GuiResponse response = GuiResponse.withTasks(
                "Text",
                "Heading",
                List.of(taskDisplay),
                "Footer");

        assertEquals("Text", response.getText());
        assertEquals("Heading", response.getHeading());
        assertEquals("Footer", response.getFooter());
        assertTrue(response.hasTasks());
        assertFalse(response.isError());
        assertSame(taskDisplay, response.getTasks().get(0));
    }

    @Test
    void withTasks_mutableInput_tasksDefensivelyCopied() {
        TaskDisplay taskDisplay = new TaskDisplay(1, new Todo("buy milk"));
        List<TaskDisplay> tasks = new ArrayList<>();
        tasks.add(taskDisplay);

        GuiResponse response = GuiResponse.withTasks(
                "Text",
                "Heading",
                tasks,
                "Footer");
        tasks.add(new TaskDisplay(2, new Todo("buy eggs")));

        assertEquals(1, response.getTasks().size());
        assertSame(taskDisplay, response.getTasks().get(0));
    }

    @Test
    void getTasks_modifyReturnedList_exceptionThrown() {
        TaskDisplay extraTask = new TaskDisplay(2, new Todo("buy eggs"));
        GuiResponse response = GuiResponse.withTasks(
                "Text",
                "Heading",
                List.of(new TaskDisplay(1, new Todo("buy milk"))),
                "Footer");

        assertThrows(UnsupportedOperationException.class, () -> response.getTasks().add(extraTask));
    }
}
