package discitrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.task.Deadline;
import discitrack.task.Event;
import discitrack.task.Task;

/**
 * Verifies complete user workflows across application restarts.
 */
public class PersistenceWorkflowTest {
    @TempDir
    public Path directory;

    @Test
    public void taskLifecycle_restartAfterEachChange_preservesState() {
        DisciTrack app = restart();
        runSuccessfully(app, "todo revise /tag school");
        app = restart();
        assertEquals("revise", app.getTasks().getFirst().getActivity());
        assertEquals(List.of("school"), app.getTasks().getFirst().getTags());
        assertFalse(app.getTasks().getFirst().isDone());

        runSuccessfully(app, "mark 1");
        app = restart();
        assertTrue(app.getTasks().getFirst().isDone());
        runSuccessfully(app, "unmark 1");
        app = restart();
        assertFalse(app.getTasks().getFirst().isDone());
        runSuccessfully(app, "delete 1");
        assertTrue(restart().getTasks().isEmpty());
    }

    @Test
    public void deleteMiddleThenMark_usesUpdatedNumberAfterRestart() {
        DisciTrack app = restart();
        runSuccessfully(app, "todo sleep");
        runSuccessfully(app, "todo revise");
        runSuccessfully(app, "todo exercise");
        runSuccessfully(app, "delete 2");
        runSuccessfully(app, "mark 2");

        List<Task> tasks = restart().getTasks();
        assertEquals(List.of("sleep", "exercise"), tasks.stream().map(Task::getActivity).toList());
        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void mixedTasks_restart_preservesDatesOrderTagsAndCompletion() {
        DisciTrack app = restart();
        runSuccessfully(app, "todo groceries /tag home");
        runSuccessfully(app, "deadline report /by 2026-09-30 /tag School /tag urgent");
        runSuccessfully(app, "event camp /from 2026-10-01 /to 2026-10-03 /tag outdoors");
        runSuccessfully(app, "mark 2");
        List<Task> tasks = restart().getTasks();

        assertEquals(List.of("groceries", "report", "camp"), tasks.stream().map(Task::getActivity).toList());
        assertEquals(List.of("home"), tasks.get(0).getTags());
        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals(LocalDate.of(2026, 9, 30), deadline.getTime());
        assertEquals(List.of("School", "urgent"), deadline.getTags());
        assertTrue(deadline.isDone());
        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals(LocalDate.of(2026, 10, 1), event.getFrom());
        assertEquals(LocalDate.of(2026, 10, 3), event.getTo());
        assertEquals(List.of("outdoors"), event.getTags());
        assertFalse(event.isDone());
    }

    @Test
    public void deleteAllThenAdd_restart_doesNotResurrectDeletedTasks() {
        DisciTrack app = restart();
        runSuccessfully(app, "todo first");
        runSuccessfully(app, "todo second");
        runSuccessfully(app, "delete 1");
        runSuccessfully(app, "delete 1");
        app = restart();
        assertTrue(app.getTasks().isEmpty());
        runSuccessfully(app, "todo fresh start");
        assertEquals(List.of("fresh start"), restart().getTasks().stream().map(Task::getActivity).toList());
    }

    private DisciTrack restart() {
        DisciTrack app = new DisciTrack(directory.resolve("tasks.txt").toString());
        assertFalse(app.hasLoadError());
        return app;
    }

    private void runSuccessfully(DisciTrack app, String command) {
        String response = app.getResponse(command);
        assertFalse(app.hasResponseError(), command + ": " + response);
    }
}
