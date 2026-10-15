package amy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import amy.task.Deadline;
import amy.task.Event;
import amy.task.Task;
import amy.task.Todo;

/** Tests the command recognition and task construction logic in {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void isTaskCommand_supportedCommandsAreRecognized() {
        assertTrue(parser.isTaskCommand("todo buy milk"));
        assertTrue(parser.isTaskCommand("deadline return book /by 2/12/2019 1800"));
        assertTrue(parser.isTaskCommand("event meeting /from 2pm /to 3pm"));
    }

    @Test
    void isTaskCommand_unknownCommandIsRejected() {
        assertTrue(!parser.isTaskCommand("list"));
    }

    @Test
    void createTask_todoCommand_createsTodo() throws AmyException {
        Task task = parser.createTask("todo buy milk", "todo buy milk");

        assertInstanceOf(Todo.class, task);
        assertEquals("buy milk", task.getDescription());
    }

    @Test
    void createTask_untypedText_usesOriginalTextAsTodoDescription() throws AmyException {
        Task task = parser.createTask("buy milk", "  buy milk  ");

        assertInstanceOf(Todo.class, task);
        assertEquals("  buy milk  ", task.getDescription());
    }

    @Test
    void createTask_emptyTodo_throwsHelpfulError() {
        AmyException exception = assertThrows(AmyException.class,
                () -> parser.createTask("todo", "todo"));

        assertEquals("A todo description cannot be empty.", exception.getMessage());
    }

    @Test
    void createTask_deadlineWithByMarker_createsDeadline() throws AmyException {
        Task task = parser.createTask("deadline return book /by 2/12/2019 1800", "");

        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
        assertEquals("return book (by: Dec 2 2019, 6:00pm)", task.getDisplayText());
    }

    @Test
    void createTask_deadlineWithShorthandSlash_createsDeadline() throws AmyException {
        Task task = parser.createTask("deadline return book / 2/12/2019 1800", "");

        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
    }

    @Test
    void createTask_invalidDeadlineDate_throwsDateFormatError() {
        AmyException exception = assertThrows(AmyException.class,
                () -> parser.createTask("deadline return book /by tomorrow", ""));

        assertTrue(exception.getMessage().contains("Please use the date format"));
    }

    @Test
    void createTask_eventWithMarkers_createsEvent() throws AmyException {
        Task task = parser.createTask("event meeting /from 2pm /to 3pm", "");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("3pm", event.getTo());
    }

    @Test
    void createTask_incompleteEvent_throwsHelpfulError() {
        AmyException exception = assertThrows(AmyException.class,
                () -> parser.createTask("event meeting /from 2pm", ""));

        assertTrue(exception.getMessage().contains("Please specify an event"));
    }
}
