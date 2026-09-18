package herta.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import herta.task.Todo;

/**
 * Tests the default task-display methods supplied by {@link UiOutput}.
 */
class UiOutputTest {

    @Test
    void showTask_defaultMethods_formatNumberedAndUnnumberedTasks() {
        RecordingOutput output = new RecordingOutput();
        Todo todo = new Todo("read book");

        output.showTask(todo);
        output.showTask(2, todo);

        assertEquals(List.of("  [T][ ] read book", "2. [T][ ] read book"), output.messages);
    }

    @Test
    void showTaskCount_defaultMethod_usesSingularAndPluralNouns() {
        RecordingOutput output = new RecordingOutput();

        output.showTaskCount(1);
        output.showTaskCount(2);

        assertEquals(List.of("That makes 1 active task. Try to keep up.",
                "That makes 2 active tasks. Try to keep up."), output.messages);
    }

    /** Captures output emitted by UiOutput default-method tests. */
    private static final class RecordingOutput implements UiOutput {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showGoodbye() {
            messages.add(GOODBYE_MESSAGE);
        }
    }
}
