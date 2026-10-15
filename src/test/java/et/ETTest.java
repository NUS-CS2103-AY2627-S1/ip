package et;

import et.parser.Parser;
import et.storage.Storage;
import et.task.Deadline;
import et.task.Task;
import et.ui.Ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the application boundary used by ET's graphical interface.
 */
class ETTest {
    @Test
    void getResponse_addThenList_returnsResponsesAndPersistsTask() {
        RecordingStorage storage = new RecordingStorage();
        ET et = new ET(new Ui(message -> { }), storage, new Parser());

        String addResponse = et.getResponse("todo read notes");
        String listResponse = et.getResponse("list");

        assertTrue(addResponse.contains("I've tucked it safely into the list"));
        assertTrue(addResponse.contains("[T][ ] read notes"));
        assertTrue(addResponse.contains("My list now holds 1 task."));
        assertTrue(listResponse.contains("1.[T][ ] read notes"));
        assertEquals(1, storage.savedTasks.size());
        assertEquals("read notes", storage.savedTasks.get(0).getDescription());
    }

    @Test
    void getResponse_invalidCommand_returnsParserError() {
        ET et = new ET(new Ui(message -> { }), new RecordingStorage(), new Parser());

        String response = et.getResponse("remind me");

        assertEquals("Hmm... my Earth decoder doesn't know that command. Try todo, deadline, event, list, sort, "
                + "find, mark, unmark, delete, or bye.", response);
    }

    @Test
    void getResponse_sort_returnsCanonicalNumbersWithoutSavingOrReordering() {
        Deadline laterDeadline = new Deadline("later", LocalDateTime.of(2027, 1, 10, 0, 0), false);
        Deadline earlierDeadline = new Deadline("earlier", LocalDateTime.of(2027, 1, 3, 0, 0), false);
        RecordingStorage storage = new RecordingStorage(List.of(laterDeadline, earlierDeadline));
        ET et = new ET(new Ui(message -> { }), storage, new Parser());

        String sortResponse = et.getResponse("sort");
        String listResponse = et.getResponse("list");

        assertEquals("I lined up your tasks by Earth time:\n"
                + "     2.[D][ ] earlier (by: Jan 03 2027)\n"
                + "     1.[D][ ] later (by: Jan 10 2027)", sortResponse);
        assertEquals("I found these in our little Earth mission:\n"
                + "     1.[D][ ] later (by: Jan 10 2027)\n"
                + "     2.[D][ ] earlier (by: Jan 03 2027)", listResponse);
        assertEquals(0, storage.saveCallCount);

        et.getResponse("mark 2");

        assertTrue(earlierDeadline.isDone());
        assertEquals(1, storage.saveCallCount);
    }

    @Test
    void getCommandResult_bye_returnsFarewellAndExitSignal() {
        ET et = new ET(new Ui(message -> { }), new RecordingStorage(), new Parser());

        ET.CommandResult result = et.getCommandResult("bye");

        assertEquals("Bye for now, Earth friend. Keep looking up!", result.response());
        assertTrue(result.shouldExit());
    }

    @Test
    void getWelcomeMessage_returnsEtPersonalityGreeting() {
        ET et = new ET(new Ui(message -> { }), new RecordingStorage(), new Parser());

        String welcomeMessage = et.getWelcomeMessage();

        assertEquals("Oh! Hello, Earth friend. I'm ET, your slightly homesick task helper.\n"
                + "I collect tasks, deadlines, and curious little plans while I wait for my ride home.\n"
                + "What shall we remember together?", welcomeMessage);
    }

    /** Stores tasks in memory so tests do not modify the application's data file. */
    private static class RecordingStorage extends Storage {
        private final List<Task> tasksToLoad;
        private List<Task> savedTasks = List.of();
        private int saveCallCount;

        /** Creates storage that loads an empty task list. */
        RecordingStorage() {
            this(List.of());
        }

        /**
         * Creates storage that loads the supplied tasks.
         *
         * @param tasksToLoad the initial tasks to return when ET starts
         */
        RecordingStorage(List<Task> tasksToLoad) {
            this.tasksToLoad = List.copyOf(tasksToLoad);
        }

        @Override
        public List<Task> load() {
            return new ArrayList<>(tasksToLoad);
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            savedTasks = List.copyOf(tasks);
            saveCallCount++;
        }
    }
}
