package harold;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import harold.storage.Storage;
import harold.task.Task;
import harold.task.TaskList;
import harold.task.Todo;

class HaroldTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void respond_addAndListTask_returnsExpectedMessages() {
        Harold harold = createHarold();

        CommandResult addResult = harold.respond("todo give Harold a treat");
        CommandResult listResult = harold.respond("list");

        assertEquals(
                "nomnomnom ball! I've fetched this task:\n"
                        + "  [T][ ] give Harold a treat\n"
                        + "Now you have 1 task in the list.",
                addResult.message()
        );
        assertEquals(
                "Here are the tasks in your list:\n1.[T][ ] give Harold a treat",
                listResult.message()
        );
        assertFalse(addResult.isExit());
        assertFalse(listResult.isExit());
    }

    @Test
    void constructor_existingData_loadsPersistedTasks() {
        Harold firstSession = createHarold();
        firstSession.respond("deadline vet visit /by 2026-09-10");

        Harold secondSession = createHarold();
        CommandResult result = secondSession.respond("list");

        assertEquals(
                "Here are the tasks in your list:\n"
                        + "1.[D][ ] vet visit (by: Sep 10 2026)",
                result.message()
        );
    }

    @Test
    void respond_invalidCommand_returnsErrorWithoutExiting() {
        CommandResult result = createHarold().respond("fetch ball");

        assertEquals(
                "i pooped on myself... I don't know what 'fetch ball' means. "
                        + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                result.message()
        );
        assertFalse(result.isExit());
    }

    @Test
    void respond_deadlineWithMalformedByMarker_returnsSyntaxError() {
        CommandResult result = createHarold().respond(
                "deadline return book /byTomorrow 2026-09-11"
        );

        assertEquals(
                "i pooped on myself... A deadline needs '/by <date or time>'. "
                        + "Try: deadline <description> /by <date or time>",
                result.message()
        );
        assertFalse(result.isExit());
    }

    @Test
    void respond_eventWithMalformedFromMarker_returnsSyntaxError() {
        CommandResult result = createHarold().respond(
                "event conference /fromTomorrow 2026-09-11 /to 2026-09-12"
        );

        assertEquals(
                "i pooped on myself... An event needs '/from <start>'. "
                        + "Try: event <description> /from <start> /to <end>",
                result.message()
        );
        assertFalse(result.isExit());
    }

    @Test
    void respond_eventWithMalformedToMarker_returnsSyntaxError() {
        CommandResult result = createHarold().respond(
                "event conference /from 2026-09-11 /toTomorrow 2026-09-12"
        );

        assertEquals(
                "i pooped on myself... An event needs '/to <end>'. "
                        + "Try: event <description> /from <start> /to <end>",
                result.message()
        );
        assertFalse(result.isExit());
    }

    @Test
    void respond_bye_returnsExitResult() {
        CommandResult result = createHarold().respond("bye");

        assertEquals("i miss my owner, hope hes back soon, SIKE!", result.message());
        assertTrue(result.isExit());
    }

    @Test
    void respond_irregularWhitespace_normalizesCommand() {
        Harold harold = createHarold();

        CommandResult addResult = harold.respond("  todo   brush\tHarold   ");
        CommandResult listResult = harold.respond("  list  ");

        assertEquals(
                "nomnomnom ball! I've fetched this task:\n"
                        + "  [T][ ] brush Harold\n"
                        + "Now you have 1 task in the list.",
                addResult.message()
        );
        assertEquals(
                "Here are the tasks in your list:\n1.[T][ ] brush Harold",
                listResult.message()
        );
    }

    @Test
    void respond_nullCommand_returnsMissingCommandError() {
        CommandResult result = createHarold().respond(null);

        assertEquals("i pooped on myself... Please enter a command pls.", result.message());
        assertFalse(result.isExit());
    }

    @Test
    void respond_repeatedDateMarkers_returnsSpecificErrors() {
        Harold harold = createHarold();

        CommandResult deadlineResult = harold.respond(
                "deadline submit /by 2026-09-20 /by 2026-09-21"
        );
        CommandResult fromResult = harold.respond(
                "event trip /from 2026-09-20 /from 2026-09-21 /to 2026-09-22"
        );
        CommandResult toResult = harold.respond(
                "event trip /from 2026-09-20 /to 2026-09-21 /to 2026-09-22"
        );

        assertEquals(
                "i pooped on myself... Use /by only once in a deadline command.",
                deadlineResult.message()
        );
        assertEquals(
                "i pooped on myself... Use /from only once in an event command.",
                fromResult.message()
        );
        assertEquals("i pooped on myself... Use /to only once in an event command.", toResult.message());
    }

    @Test
    void respond_eventEndBeforeStart_returnsRangeError() {
        CommandResult result = createHarold().respond(
                "event holiday /from 2026-09-20 /to 2026-09-19"
        );

        assertEquals(
                "i pooped on myself... The event end date cannot be before its start date.",
                result.message()
        );
    }

    @Test
    void respond_duplicateTask_returnsErrorWithoutAddingTask() {
        Harold harold = createHarold();
        harold.respond("todo feed Harold");

        CommandResult duplicateResult = harold.respond("todo feed Harold");
        CommandResult listResult = harold.respond("list");

        assertEquals("i pooped on myself... That task is already in your list.", duplicateResult.message());
        assertEquals(
                "Here are the tasks in your list:\n1.[T][ ] feed Harold",
                listResult.message()
        );
    }

    @Test
    void respond_addWhenSaveFails_rollsBackAddition() {
        Harold harold = new Harold(new FailingStorage(List.of()));

        CommandResult addResult = harold.respond("todo feed Harold");
        CommandResult listResult = harold.respond("list");

        assertEquals(saveErrorMessage(), addResult.message());
        assertEquals("Here are the tasks in your list:", listResult.message());
    }

    @Test
    void respond_markWhenSaveFails_restoresCompletionState() {
        Harold harold = new Harold(new FailingStorage(List.of(new Todo("feed Harold"))));

        CommandResult markResult = harold.respond("mark 1");
        CommandResult listResult = harold.respond("list");

        assertEquals(saveErrorMessage(), markResult.message());
        assertEquals(
                "Here are the tasks in your list:\n1.[T][ ] feed Harold",
                listResult.message()
        );
    }

    @Test
    void respond_unmarkWhenSaveFails_restoresCompletionState() {
        Todo completedTask = new Todo("feed Harold");
        completedTask.markAsDone();
        Harold harold = new Harold(new FailingStorage(List.of(completedTask)));

        CommandResult unmarkResult = harold.respond("unmark 1");
        CommandResult listResult = harold.respond("list");

        assertEquals(saveErrorMessage(), unmarkResult.message());
        assertEquals(
                "Here are the tasks in your list:\n1.[T][X] feed Harold",
                listResult.message()
        );
    }

    @Test
    void respond_deleteWhenSaveFails_restoresTaskAtOriginalIndex() {
        Harold harold = new Harold(new FailingStorage(List.of(
                new Todo("first"),
                new Todo("second")
        )));

        CommandResult deleteResult = harold.respond("delete 1");
        CommandResult listResult = harold.respond("list");

        assertEquals(saveErrorMessage(), deleteResult.message());
        assertEquals(
                "Here are the tasks in your list:\n"
                        + "1.[T][ ] first\n"
                        + "2.[T][ ] second",
                listResult.message()
        );
    }

    private Harold createHarold() {
        return new Harold(temporaryDirectory.resolve("data/harold.txt"));
    }

    private static String saveErrorMessage() {
        return "i pooped on myself... I couldn't save your tasks. Please try again.";
    }

    private static class FailingStorage extends Storage {
        private final List<Task> initialTasks;

        FailingStorage(List<Task> initialTasks) {
            super(Path.of("unused"));
            this.initialTasks = initialTasks;
        }

        @Override
        public LoadResult load() {
            return new LoadResult(initialTasks, 0);
        }

        @Override
        public void save(TaskList tasks) throws IOException {
            throw new IOException("Simulated save failure");
        }
    }
}
