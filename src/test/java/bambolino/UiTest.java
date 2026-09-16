package bambolino;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import bambolino.task.TaskList;
import bambolino.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks console messages and input boundaries independently of a running application. */
class UiTest {
    @Test
    void readCommand_whitespaceAndEndOfInput_trimAndExit() {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  list  \n\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui(message -> { });
            assertEquals("list", ui.readCommand());
            assertEquals("", ui.readCommand());
            assertEquals("bye", ui.readCommand());
        } finally {
            System.setIn(original);
        }
    }

    @Test
    void output_greetingErrorsAndEmptyList_areReadable() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        ui.showWelcome();
        assertTrue(output.stream().anyMatch(line -> line.contains("Hello! I'm Bambolino.")));
        output.clear();
        ui.showTaskList(new TaskList());
        ui.showError("invalid command.");
        ui.showGoodbye();
        assertEquals(List.of("No tasks added yet.", "_".repeat(60), "Sorry, invalid command.",
                "_".repeat(60), "Bye. Hope to see you again soon!"), output);
    }
}
