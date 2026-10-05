package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.testutil.RecordingUi;

/** Tests listing both empty and populated task lists. */
class ListCommandTest extends AbstractCommandTest {

    /** Verifies the friendly response for an empty list. */
    @Test
    void execute_emptyList_reportsNothingToList() {
        RecordingUi ui = recordingUi();

        new ListCommand().execute(new TaskList(), ui);

        assertEquals(List.of(" Here are the tasks in your list:",
                " There's nothing here yet! Go ahead and add any tasks you'd like! :)"),
                ui.messages());
    }

    /** Verifies that populated lists are printed with one-based numbering. */
    @Test
    void execute_populatedList_printsNumberedTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));
        RecordingUi ui = recordingUi();

        new ListCommand().execute(tasks, ui);

        assertEquals(List.of(" Here are the tasks in your list:",
                " 1. [T][ ] first", " 2. [T][ ] second"), ui.messages());
    }
}
