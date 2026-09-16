package walter.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import walter.DukeException;
import walter.place.Place;
import walter.task.Deadline;
import walter.task.Event;
import walter.task.Task;
import walter.task.Todo;

/**
 * Tests persistence using an isolated temporary save file.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_emptyListReturned() throws IOException, DukeException {
        Storage storage = createStorage();

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_unreadablePath_applicationExceptionRetainsCause() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("walter.txt");
        Files.createDirectories(saveFile);

        DukeException exception = assertThrows(DukeException.class, () -> createStorage().load());

        assertEquals("Walter could not load saved tasks.", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    public void saveLoad_todo_roundTripPreserved() throws IOException, DukeException {
        Storage storage = createStorage();
        assertFalse(Files.exists(temporaryDirectory.resolve("data")));

        storage.save(List.of(new Todo("read book")));

        List<Task> loaded = storage.load();

        assertTrue(Files.isDirectory(temporaryDirectory.resolve("data")));
        assertEquals(1, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("read book", loaded.get(0).getDescription());
    }

    @Test
    public void saveLoad_deadline_dateRoundTripPreserved() throws IOException, DukeException {
        Storage storage = createStorage();
        LocalDate date = LocalDate.of(2026, 8, 30);
        storage.save(List.of(new Deadline("submit report", date)));

        Deadline loaded = assertInstanceOf(Deadline.class, storage.load().get(0));

        assertEquals("submit report", loaded.getDescription());
        assertEquals(date, loaded.getBy());
    }

    @Test
    public void saveLoad_atEvent_roundTripPreserved() throws IOException, DukeException {
        Storage storage = createStorage();
        storage.save(List.of(new Event("meeting", "3pm")));

        Event loaded = assertInstanceOf(Event.class, storage.load().get(0));

        assertTrue(loaded.isAtFormat());
        assertEquals("meeting", loaded.getDescription());
        assertEquals("3pm", loaded.getAt());
    }

    @Test
    public void saveLoad_fromToEvent_roundTripPreserved() throws IOException, DukeException {
        Storage storage = createStorage();
        storage.save(List.of(new Event("workshop", "2pm", "4pm")));

        Event loaded = assertInstanceOf(Event.class, storage.load().get(0));

        assertFalse(loaded.isAtFormat());
        assertEquals("workshop", loaded.getDescription());
        assertEquals("2pm", loaded.getFrom());
        assertEquals("4pm", loaded.getTo());
    }

    @Test
    public void saveLoad_markedAndUnmarkedTasks_statusPreserved()
            throws IOException, DukeException {
        Storage storage = createStorage();
        Task marked = new Todo("marked");
        marked.markAsDone();
        Task unmarked = new Todo("unmarked");
        storage.save(List.of(marked, unmarked));

        List<Task> loaded = storage.load();

        assertTrue(loaded.get(0).isDone());
        assertFalse(loaded.get(1).isDone());
    }

    @Test
    public void saveLoad_multipleTasks_originalOrderPreserved() throws IOException, DukeException {
        Storage storage = createStorage();
        storage.save(List.of(
                new Todo("first"),
                new Deadline("second", LocalDate.of(2026, 8, 30)),
                new Event("third", "3pm")));

        List<Task> loaded = storage.load();

        assertEquals(List.of("first", "second", "third"), loaded.stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void saveLoad_escapedControlCharacters_roundTripPreserved()
            throws IOException, DukeException {
        Storage storage = createStorage();
        Todo todo = new Todo("tab\tnewline\nbackslash\\carriage\rreturn café 中文 🚀");
        Event event = new Event("event\\description 😀", "from\tline", "to\nline");
        storage.save(List.of(todo, event));

        List<Task> loaded = storage.load();
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(1));

        assertEquals(todo.getDescription(), loaded.get(0).getDescription());
        assertEquals(event.getDescription(), loadedEvent.getDescription());
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
    }

    @Test
    public void saveReloadSaveReload_allEntityFieldsRemainStable() throws DukeException {
        Storage storage = createStorage();
        Task markedTodo = new Todo("  todo \\ \t\n\r | /by /at /from /to 中文 🚀  ");
        markedTodo.markAsDone();
        List<Task> originalTasks = List.of(
                markedTodo,
                new Deadline("deadline \\ | 中文", LocalDate.of(9999, 12, 31)),
                new Event("event /at text", "Monday 2:30 PM | 中文"),
                new Event("event range", "start\t\\ /from", "end\n\r /to 🚀"));
        List<Place> originalPlaces = List.of(
                new Place("  place \\ \t 中文  ", "  address\n\r | /at 🚀  "),
                new Place("duplicate", "same"),
                new Place("duplicate", "same"));

        storage.save(originalTasks);
        storage.savePlaces(originalPlaces);
        List<Task> firstTasks = storage.load();
        List<Place> firstPlaces = storage.loadPlaces();
        storage.save(firstTasks);
        storage.savePlaces(firstPlaces);
        List<Task> secondTasks = storage.load();
        List<Place> secondPlaces = storage.loadPlaces();

        assertEquals(originalTasks.stream().map(Task::toString).toList(),
                secondTasks.stream().map(Task::toString).toList());
        assertEquals(originalPlaces.stream().map(Place::toString).toList(),
                secondPlaces.stream().map(Place::toString).toList());
    }

    @Test
    public void load_malformedRecord_exceptionThrown() throws IOException, DukeException {
        writeSaveFile("");
        assertTrue(createStorage().load().isEmpty());

        String[] malformedRecords = {
            "\n", "   ", "not-a-task", "t\t0\tdescription", "T\t0",
            "T\t\tdescription", "T\t0\tdescription\textra",
            "E\t0\tAT\tdescription", "E\t0\tFROM_TO\tdescription\tstart",
            "E\t0\tUNKNOWN\tdescription\ttime", "T\t0\tdangling\\",
            "T\t0\tbad\\q", "T\t0\tembedded\ttab",
            "T\t0\tvalid\ncorrupt", "corrupt\nT\t0\tvalid"
        };
        for (String record : malformedRecords) {
            writeSaveFile(record);
            assertThrows(DukeException.class, () -> createStorage().load());
        }
    }

    @Test
    public void load_unknownTaskType_exceptionThrown() throws IOException {
        writeSaveFile("X\t0\tread book");

        DukeException exception = assertThrows(DukeException.class, () -> createStorage().load());

        assertEquals("Unknown saved task record.", exception.getMessage());
    }

    @Test
    public void load_emptyRequiredTaskText_exceptionThrown() throws IOException {
        writeSaveFile("T\t0\t");

        DukeException exception = assertThrows(DukeException.class, () -> createStorage().load());

        assertEquals("Saved task text cannot be empty.", exception.getMessage());
    }

    @Test
    public void load_malformedCompletionStatus_exceptionThrown() throws IOException {
        writeSaveFile("T\t2\tread book");

        assertThrows(DukeException.class, () -> createStorage().load());
    }

    @Test
    public void load_malformedStoredDate_exceptionThrown() throws IOException {
        writeSaveFile("D\t0\tsubmit report\t2026-02-30");

        assertThrows(DukeException.class, () -> createStorage().load());
    }

    @Test
    public void load_malformedEscapedText_exceptionThrown() throws IOException {
        writeSaveFile("T\t0\tbad\\q");

        assertThrows(DukeException.class, () -> createStorage().load());
    }

    @Test
    public void saveLoadPlaces_multiplePlaces_roundTripPreserved() throws DukeException {
        Storage storage = createStorage();
        storage.savePlaces(List.of(
                new Place("Alex's home", "123 Clementi Ave 3"),
                new Place("NUS\tLibrary\\中文", "12 Computing Drive\nLevel 1\r🚀")));

        List<Place> loaded = storage.loadPlaces();

        assertEquals(2, loaded.size());
        assertEquals("Alex's home", loaded.get(0).getName());
        assertEquals("123 Clementi Ave 3", loaded.get(0).getAddress());
        assertEquals("NUS\tLibrary\\中文", loaded.get(1).getName());
        assertEquals("12 Computing Drive\nLevel 1\r🚀", loaded.get(1).getAddress());
    }

    @Test
    public void loadPlaces_malformedRecord_exceptionThrown() throws IOException {
        Path placeFile = temporaryDirectory.resolve("data").resolve("places.txt");
        Files.createDirectories(placeFile.getParent());
        String[] malformedRecords = {
            "\n", "   ", "P\\tmissing-address", "p\tname\taddress",
            "P\tname", "P\tname\taddress\textra", "P\tname\tdangling\\",
            "P\tname\tbad\\q", "P\tname\tembedded\ttab",
            "P\tvalid\taddress\ncorrupt", "corrupt\nP\tvalid\taddress"
        };
        for (String record : malformedRecords) {
            Files.writeString(placeFile, record, StandardCharsets.UTF_8);
            assertThrows(DukeException.class, () -> createStorage().loadPlaces());
        }
    }

    @Test
    public void loadPlaces_emptyOrMalformedEscapedText_exceptionThrown() throws IOException {
        Path placeFile = temporaryDirectory.resolve("data").resolve("places.txt");
        Files.createDirectories(placeFile.getParent());
        Files.writeString(placeFile, "P\t\taddress", StandardCharsets.UTF_8);
        assertThrows(DukeException.class, () -> createStorage().loadPlaces());

        Files.writeString(placeFile, "P\tname\tbad\\q", StandardCharsets.UTF_8);
        assertThrows(DukeException.class, () -> createStorage().loadPlaces());
    }

    @Test
    public void save_taskAndPlacePathsAreDirectories_applicationExceptionsThrown()
            throws IOException {
        Path taskPath = temporaryDirectory.resolve("tasks");
        Path placePath = temporaryDirectory.resolve("places");
        Files.createDirectories(taskPath);
        Files.createDirectories(placePath);
        Storage storage = new Storage(taskPath, placePath);
        List<Task> tasks = List.of(new Todo("read book"));
        List<Place> places = List.of(new Place("home", "Clementi"));

        DukeException taskException = assertThrows(
                DukeException.class, () -> storage.save(tasks));
        DukeException placeException = assertThrows(
                DukeException.class, () -> storage.savePlaces(places));

        assertEquals("Walter could not save your tasks.", taskException.getMessage());
        assertInstanceOf(IOException.class, taskException.getCause());
        assertEquals("Walter could not save your places.", placeException.getMessage());
        assertInstanceOf(IOException.class, placeException.getCause());
    }

    /**
     * Returns storage bound to this test's temporary directory.
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data").resolve("walter.txt"));
    }

    /**
     * Writes one raw record for malformed-file tests.
     */
    private void writeSaveFile(String record) throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("walter.txt");
        Files.createDirectories(saveFile.getParent());
        Files.writeString(saveFile, record, StandardCharsets.UTF_8);
    }
}
