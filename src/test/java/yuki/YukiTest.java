package yuki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Deadline;
import yuki.task.Task;
import yuki.time.TaskDateTime;
import yuki.ui.Ui;

/** Tests the command-processing interface shared by Yuki's graphical UI. */
class YukiTest {

    @Test
    void getWelcomeResponse_applicationStarts_welcomeReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String response = yuki.getWelcomeResponse();

        assertEquals("...Hello. This is Yuki. What do you need?", response);
        assertFalse(yuki.isLastResponseError());
    }

    @Test
    void getResponse_supportedCommands_actualResponsesReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String addResponse = yuki.getResponse("todo read book");
        boolean isAddResponseError = yuki.isLastResponseError();
        String listResponse = yuki.getResponse("list");

        assertTrue(addResponse.contains("I've added it"));
        assertFalse(isAddResponseError);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(yuki.isLastResponseError());
    }

    @Test
    void getResponse_unknownCommand_errorResponseReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String response = yuki.getResponse("unknown command");

        assertTrue(response.contains("I couldn't process that"));
        assertTrue(yuki.isLastResponseError());
    }

    @Test
    void getResponse_exitCommand_goodbyeReturnedAndExitRequested() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String response = yuki.getResponse("bye");

        assertEquals("...Goodbye.", response);
        assertTrue(yuki.isExitRequested());
        assertFalse(yuki.isLastResponseError());
    }

    @Test
    void getResponse_saveFails_additionRolledBack() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FailingSaveStorage());

        String response = yuki.getResponse("todo read book");
        String listResponse = yuki.getResponse("list");

        assertTrue(response.contains("couldn't save"));
        assertFalse(listResponse.contains("read book"));
    }

    @Test
    void getStartupReminderResponse_loadingFails_errorReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FailingLoadStorage());

        String response = yuki.getStartupReminderResponse();

        assertTrue(response.contains("couldn't load"));
        assertTrue(yuki.isLastResponseError());
    }

    @Test
    void getStartupReminderResponse_upcomingDeadline_reminderReturned() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-09T01:00:00Z"), ZoneOffset.ofHours(8));
        List<Task> storedTasks = List.of(new Deadline("submit report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 10, 9, 0))));
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FixedStorage(storedTasks), clock);

        String response = yuki.getStartupReminderResponse();

        assertTrue(response.contains("1.[D][ ] submit report"));
    }

    @Test
    void getStartupReminderResponse_noUpcomingTask_emptyResponseReturned() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-09T01:00:00Z"), ZoneOffset.ofHours(8));
        List<Task> storedTasks = List.of(new Deadline("later report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 11, 9, 1))));
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FixedStorage(storedTasks), clock);

        assertTrue(yuki.getStartupReminderResponse().isEmpty());
    }

    @Test
    void run_commandsIncludeErrorAndExit_loopStopsAfterExit() {
        ScriptedUi ui = new ScriptedUi(List.of(
                "todo read book", "unknown", "bye", "todo ignored"));
        NoOpStorage storage = new NoOpStorage();
        Yuki yuki = new Yuki(ui, storage);

        yuki.run();

        assertTrue(ui.isWelcomeShown);
        assertTrue(ui.isGoodbyeShown);
        assertTrue(ui.errors.get(0).contains("isn't familiar"));
        assertEquals("read book", storage.savedTasks.get(0).getDescription());
        assertEquals(1, ui.commands.size());
    }

    @Test
    void run_inputAlreadyEnded_welcomeShownWithoutReadingCommand() {
        ScriptedUi ui = new ScriptedUi(List.of());

        new Yuki(ui, new NoOpStorage()).run();

        assertTrue(ui.isWelcomeShown);
        assertFalse(ui.isGoodbyeShown);
    }

    /** Storage test double that avoids reading and writing the user's data file. */
    private static class NoOpStorage extends Storage {
        private List<Task> savedTasks = new ArrayList<>();

        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>();
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            savedTasks = new ArrayList<>(tasks);
        }
    }

    /** UI test double that supplies a fixed command sequence to the command loop. */
    private static class ScriptedUi extends Ui {
        private final Deque<String> commands;
        private final List<String> errors = new ArrayList<>();
        private boolean isWelcomeShown;
        private boolean isGoodbyeShown;

        ScriptedUi(List<String> commands) {
            this.commands = new ArrayDeque<>(commands);
        }

        @Override
        public boolean hasNextCommand() {
            return !commands.isEmpty();
        }

        @Override
        public String readCommand() {
            return commands.removeFirst();
        }

        @Override
        public void showWelcome() {
            isWelcomeShown = true;
        }

        @Override
        public void showGoodbye() {
            isGoodbyeShown = true;
        }

        @Override
        public void showError(String message) {
            errors.add(message);
        }

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            // The test observes the stored task instead of console output.
        }
    }

    /** Storage test double that returns a fixed set of tasks. */
    private static class FixedStorage extends NoOpStorage {
        private final List<Task> storedTasks;

        FixedStorage(List<Task> storedTasks) {
            this.storedTasks = storedTasks;
        }

        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>(storedTasks);
        }
    }

    /** Storage test double that simulates an unavailable data file during saving. */
    private static class FailingSaveStorage extends NoOpStorage {
        @Override
        public void saveTasks(List<Task> tasks) {
            throw new YukiException("I couldn't save the tasks.");
        }
    }

    /** Storage test double that simulates an unreadable data file during loading. */
    private static class FailingLoadStorage extends NoOpStorage {
        @Override
        public ArrayList<Task> loadTasks() {
            throw new YukiException("The saved data file is unreadable.");
        }
    }
}
