package kaykay.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.model.Deadline;
import kaykay.model.Event;
import kaykay.model.Place;
import kaykay.model.Task;
import kaykay.model.TaskList;
import kaykay.model.Todo;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/** Tests command behavior that mutates task state and persists the result. */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    /** Checks that adding a task updates both memory and storage. */
    @Test
    void todoCommand_execute_addsAndSavesTask() throws IOException, KaykayException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        new TodoCommand("buy milk").execute(new ApplicationData(tasks), new RecordingUi(), storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] buy milk", tasks.getTask(0).toString());
        assertEquals("T | 0 | buy milk", storage.loadData().getTasks().getTask(0).toFileFormat());
    }

    /** Checks marking, unmarking, deletion, and rejection of an invalid task number. */
    @Test
    void statusAndDeleteCommands_execute_updateTaskList() throws IOException, KaykayException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ApplicationData data = new ApplicationData(tasks);
        new TodoCommand("revise notes").execute(data, new RecordingUi(), storage);

        new MarkCommand("1").execute(data, new RecordingUi(), storage);
        assertEquals("[T][X] revise notes", tasks.getTask(0).toString());
        new UnmarkCommand("1").execute(data, new RecordingUi(), storage);
        assertEquals("[T][ ] revise notes", tasks.getTask(0).toString());
        new DeleteCommand("1").execute(data, new RecordingUi(), storage);
        assertEquals(0, tasks.size());
        assertThrows(KaykayException.class, () ->
                new DeleteCommand("1").execute(data, new RecordingUi(), storage));
    }

    /** Checks that requesting an existing status reports it without trying to save. */
    @Test
    void changeStatusCommand_existingStatus_reportsNoChange() throws IOException, KaykayException {
        Todo openTask = new Todo("open task");
        Todo completedTask = new Todo("completed task");
        completedTask.mark();
        ApplicationData data = new ApplicationData(new TaskList(openTask, completedTask));
        RecordingUi ui = new RecordingUi();
        Storage invalidStorage = new Storage(temporaryDirectory.toString());

        new UnmarkCommand("1").execute(data, ui, invalidStorage);
        assertEquals(openTask, ui.getUnchangedTask());
        assertFalse(ui.isUnchangedTaskMarked());

        new MarkCommand("2").execute(data, ui, invalidStorage);
        assertEquals(completedTask, ui.getUnchangedTask());
        assertTrue(ui.isUnchangedTaskMarked());
    }

    /** Checks that identical task details are reported without being added or saved again. */
    @Test
    void addCommands_duplicateTasks_reportExistingTasks() throws IOException, KaykayException {
        LocalDateTime start = LocalDateTime.of(2026, 12, 26, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 26, 16, 0);
        Todo existingTodo = new Todo("buy milk");
        Deadline existingDeadline = new Deadline("submit report", start);
        Event existingEvent = new Event("project meeting", start, end);
        ApplicationData data = new ApplicationData(
                new TaskList(existingTodo, existingDeadline, existingEvent));
        RecordingUi ui = new RecordingUi();
        Storage invalidStorage = new Storage(temporaryDirectory.toString());

        new TodoCommand("buy milk").execute(data, ui, invalidStorage);
        assertEquals(existingTodo, ui.getDuplicateTask());
        assertEquals(1, ui.getDuplicateTaskNumber());

        new DeadlineCommand("submit report", start).execute(data, ui, invalidStorage);
        assertEquals(existingDeadline, ui.getDuplicateTask());
        assertEquals(2, ui.getDuplicateTaskNumber());

        new EventCommand("project meeting", start, end).execute(data, ui, invalidStorage);
        assertEquals(3, data.getTasks().size());
        assertEquals(existingEvent, ui.getDuplicateTask());
        assertEquals(3, ui.getDuplicateTaskNumber());
    }

    /** Checks that find delegates the ordered matches to the UI without changing task state. */
    @Test
    void findCommand_execute_displaysMatchingTasks() throws IOException, KaykayException {
        Task first = new Todo("read book");
        Task second = new Todo("buy milk");
        TaskList tasks = new TaskList(first, second);
        RecordingUi ui = new RecordingUi();

        new FindCommand("BOOK").execute(new ApplicationData(tasks), ui,
                new Storage(temporaryDirectory.resolve("tasks.txt").toString()));

        assertEquals(List.of(first), ui.getMatchingTasks());
        assertEquals(2, tasks.size());
    }

    /** Checks adding, editing, finding, and deleting places with persistence. */
    @Test
    void placeCommands_execute_manageAndSavePlaces() throws IOException, KaykayException {
        ApplicationData data = new ApplicationData();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage(temporaryDirectory.resolve("data.txt").toString());
        Place place = new Place("Burnt Ends", "restaurant", "Dempsey",
                LocalDate.of(2026, 9, 10), 5, "Great brisket");

        new AddPlaceCommand(place).execute(data, ui, storage);
        assertEquals(1, data.getPlaces().size());
        assertEquals(place.toString(), storage.loadData().getPlaces().getPlace(0).toString());

        new AddPlaceCommand(place).execute(data, ui, storage);
        assertEquals(1, data.getPlaces().size());
        assertEquals(place, ui.getDuplicatePlace());
        assertEquals(1, ui.getDuplicatePlaceNumber());

        new EditPlaceCommand("1", null, null, null, null, 4, "Worth revisiting")
                .execute(data, ui, storage);
        assertEquals(4, data.getPlaces().getPlace(0).getRating());
        assertEquals("Worth revisiting", data.getPlaces().getPlace(0).getNotes());

        new FindPlacesCommand("REVISITING").execute(data, ui, storage);
        assertEquals(List.of(data.getPlaces().getPlace(0)), ui.getMatchingPlaces());

        new DeletePlaceCommand("1").execute(data, ui, storage);
        assertEquals(0, data.getPlaces().size());
        assertEquals(0, storage.loadData().getPlaces().size());
        assertThrows(KaykayException.class, () ->
                new DeletePlaceCommand("1").execute(data, ui, storage));
    }

    /** Suppresses UI output while allowing commands to execute in isolation. */
    private static final class RecordingUi extends Ui {
        /** Most recent search results shown by this UI. */
        private List<Task> matchingTasks;

        /** Most recent place search results shown by this UI. */
        private List<Place> matchingPlaces;

        /** Most recent task reported as already having the requested status. */
        private Task unchangedTask;

        /** Whether the unchanged task was already marked. */
        private boolean isUnchangedTaskMarked;

        /** Existing task reported after a duplicate add command. */
        private Task duplicateTask;

        /** One-based mission number of the existing duplicate task. */
        private int duplicateTaskNumber;

        /** Existing place reported after a duplicate add command. */
        private Place duplicatePlace;

        /** One-based place number of the existing duplicate place. */
        private int duplicatePlaceNumber;

        /** Does nothing because these tests verify command state changes directly. */
        @Override
        public void showAddedTask(Task task, int taskCount) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        /** Does nothing because these tests verify command state changes directly. */
        @Override
        public void showDeletedTask(Task task, int taskCount) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        /** Does nothing because these tests verify command state changes directly. */
        @Override
        public void showMarkedTask(Task task, boolean isMarked) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        /** Records a redundant status request for command assertions. */
        @Override
        public void showUnchangedTaskStatus(Task task, boolean isMarked) {
            unchangedTask = task;
            isUnchangedTaskMarked = isMarked;
        }

        /** Records an existing task that prevented a duplicate addition. */
        @Override
        public void showDuplicateTask(Task task, int taskNumber) {
            duplicateTask = task;
            duplicateTaskNumber = taskNumber;
        }

        /** Records an existing place that prevented a duplicate addition. */
        @Override
        public void showDuplicatePlace(Place place, int placeNumber) {
            duplicatePlace = place;
            duplicatePlaceNumber = placeNumber;
        }

        /** Records search results for command assertions. */
        @Override
        public void showMatchingTasks(List<Task> matchingTasks) {
            this.matchingTasks = matchingTasks;
        }

        @Override
        public void showAddedPlace(Place place, int placeCount) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        @Override
        public void showEditedPlace(Place place) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        @Override
        public void showDeletedPlace(Place place, int placeCount) {
            // Intentionally empty: command tests assert state, not console formatting.
        }

        @Override
        public void showMatchingPlaces(List<Place> matchingPlaces) {
            this.matchingPlaces = matchingPlaces;
        }

        /** Returns the search results recorded by this UI. */
        private List<Task> getMatchingTasks() {
            return matchingTasks;
        }

        /** Returns the place search results recorded for command assertions. */
        private List<Place> getMatchingPlaces() {
            return matchingPlaces;
        }

        /** Returns the task most recently reported as unchanged. */
        private Task getUnchangedTask() {
            return unchangedTask;
        }

        /** Checks whether the unchanged task was already marked. */
        private boolean isUnchangedTaskMarked() {
            return isUnchangedTaskMarked;
        }

        /** Returns the existing task reported for a duplicate addition. */
        private Task getDuplicateTask() {
            return duplicateTask;
        }

        /** Returns the mission number reported for a duplicate task. */
        private int getDuplicateTaskNumber() {
            return duplicateTaskNumber;
        }

        /** Returns the existing place reported for a duplicate addition. */
        private Place getDuplicatePlace() {
            return duplicatePlace;
        }

        /** Returns the place number reported for a duplicate addition. */
        private int getDuplicatePlaceNumber() {
            return duplicatePlaceNumber;
        }
    }
}
