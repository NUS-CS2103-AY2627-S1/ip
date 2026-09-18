package benjamin.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import benjamin.BenjaminException;
import benjamin.task.Deadline;
import benjamin.task.Event;
import benjamin.task.Task;
import benjamin.task.TaskDateTime;
import benjamin.task.TaskList;
import benjamin.task.Todo;

public class StorageTest {
    @TempDir
    Path root;

    private Storage storage() {
        return new Storage(root.toString(), "data", "benjamin.txt");
    }

    private void writeSaveFile(String... lines) throws IOException {
        Path file = root.resolve("data").resolve("benjamin.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, List.of(lines));
    }

    @Test
    public void load_fileDoesNotExist_emptyListAndNoWarnings() throws BenjaminException {
        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertEquals(0, storage.getLoadWarnings().size());
    }

    @Test
    public void save_folderDoesNotExist_folderIsCreated() throws BenjaminException {
        storage().save(new TaskList());

        assertTrue(Files.exists(root.resolve("data").resolve("benjamin.txt")));
    }

    @Test
    public void saveThenLoad_mixedTasks_roundTripsUnchanged() throws BenjaminException {
        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", TaskDateTime.parse("2/12/2019 1800")));
        original.add(new Event("camp",
                TaskDateTime.parse("2019-08-06 1400"),
                TaskDateTime.parse("2019-08-08 1600")));

        Storage storage = storage();
        storage.save(original);
        TaskList reloaded = new TaskList(storage.load());

        assertEquals(0, storage.getLoadWarnings().size());
        assertEquals(original.toSaveFormat(), reloaded.toSaveFormat());
        assertEquals("[T][X] read book", reloaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", reloaded.get(1).toString());
    }

    @Test
    public void save_emptyList_loadsBackAsEmpty() throws BenjaminException {
        Storage storage = storage();
        storage.save(new TaskList());

        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_blankLines_areIgnored() throws BenjaminException, IOException {
        writeSaveFile("T | 0 | read book", "", "   ", "T | 1 | join sports club");

        Storage storage = storage();
        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals(0, storage.getLoadWarnings().size());
    }

    @Test
    public void load_unknownTaskType_lineSkippedAndWarningNamesTheType()
            throws BenjaminException, IOException {
        writeSaveFile("T | 0 | read book", "X | 0 | mystery");

        Storage storage = storage();
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("line 2"));
        assertTrue(storage.getLoadWarnings().get(0).contains("not a known task type"));
    }

    @Test
    public void load_wrongFieldCount_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("D | 0 | missing the by field", "E | 0 | only one end | 2019-08-06");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertEquals(2, storage.getLoadWarnings().size());
    }

    @Test
    public void load_badDoneMarker_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("T | 2 | read book");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("done marker"));
    }

    @Test
    public void load_emptyDescription_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("T | 0 | ");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("description is empty"));
    }

    @Test
    public void load_unreadableDate_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("D | 0 | return book | June 6th");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("could not read the date"));
    }

    @Test
    public void load_tooFewFields_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("garbage line");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("not have enough fields"));
    }

    @Test
    public void load_goodLinesAmongBadOnes_goodLinesSurvive()
            throws BenjaminException, IOException {
        writeSaveFile(
                "T | 1 | read book",
                "X | 0 | mystery",
                "D | 0 | return book | 2019-06-06",
                "rubbish",
                "E | 1 | camp | 2019-08-06 1400 | 2019-08-08 1600");

        Storage storage = storage();
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals(2, storage.getLoadWarnings().size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[E][X] camp (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                tasks.get(2).toString());
    }

    @Test
    public void load_calledAgainAfterFixingFile_warningsAreCleared()
            throws BenjaminException, IOException {
        Storage storage = storage();

        writeSaveFile("X | 0 | mystery");
        storage.load();
        assertEquals(1, storage.getLoadWarnings().size());

        writeSaveFile("T | 0 | read book");
        storage.load();
        assertEquals(0, storage.getLoadWarnings().size());
    }
}
