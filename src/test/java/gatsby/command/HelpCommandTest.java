package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.model.TaskList;
import gatsby.testutil.RecordingUi;

/** Tests the command reference displayed by {@link HelpCommand}. */
class HelpCommandTest extends AbstractCommandTest {

    /** Verifies that help lists the commands without changing the task list. */
    @Test
    void execute_emptyList_printsCommandReference() {
        TaskList tasks = new TaskList();
        RecordingUi ui = recordingUi();

        new HelpCommand().execute(tasks, ui);

        assertEquals(List.of(
                " Here are the commands I know:",
                "  todo <description> - add a task",
                "  deadline <description> /by <yyyy-MM-dd HHmm> - add a deadline",
                "  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm> - add an event",
                "  list - show all tasks",
                "  find <keyword> - find matching tasks",
                "  mark <number> / unmark <number> - update a task's status",
                "  delete <number> - remove a task",
                "  help or ? - show this help",
                "  bye - exit Gatsby"), ui.messages());
        assertEquals(0, tasks.size());
    }
}
