package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nubish.tasks.Todo;

/**
 * Tests storage behavior for missing and malformed save files.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that a missing save file is created during loading.
     */
    @Test
    public void load_missingFile_createsFile() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("nubish.txt");
        Storage storage = new Storage(saveFile.toString(), new TaskList());

        storage.load();

        assertTrue(Files.exists(saveFile));
    }

    /**
     * Verifies that malformed saved content is rejected with line information.
     */
    @Test
    public void load_malformedLine_throwsExceptionWithLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Files.writeString(saveFile, "T | 2 | read book\n");
        Storage storage = new Storage(saveFile.toString(), new TaskList());

        NubishException exception = assertThrows(NubishException.class, storage::load);

        assertEquals("Invalid save file at line 1: invalid task status", exception.getMessage());
    }

    /**
     * Verifies that valid saved tasks are loaded into the task list.
     */
    @Test
    public void load_validSavedTasks_loadsAllTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Files.writeString(saveFile, """
                T | 0 | read book

                D | 1 | submit assignment | 12/10/2020 1800
                E | 0 | meeting | 12/10/2020 1800 | 12/10/2020 1900
                """);
        TaskList taskList = new TaskList();
        Storage storage = new Storage(saveFile.toString(), taskList);

        storage.load();

        assertEquals(3, taskList.size());
        assertEquals("[T][ ] read book", taskList.get(0).toString());
        assertEquals("[D][X] submit assignment (by: 1800 Oct 12 2020)", taskList.get(1).toString());
        assertEquals("[E][ ] meeting (from: 1800 Oct 12 2020 to: 1900 Oct 12 2020)",
                taskList.get(2).toString());
    }

    /**
     * Verifies that saving writes all tasks in storage format.
     */
    @Test
    public void save_taskList_writesAllTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("nubish.txt");
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        Storage storage = new Storage(saveFile.toString(), taskList);

        storage.save();

        assertEquals("T | 0 | read book\n", Files.readString(saveFile));
    }

    /**
     * Verifies that unknown task types are rejected while loading.
     */
    @Test
    public void load_unknownTaskType_throwsExceptionWithLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Files.writeString(saveFile, "X | 0 | read book\n");
        Storage storage = new Storage(saveFile.toString(), new TaskList());

        NubishException exception = assertThrows(NubishException.class, storage::load);

        assertEquals("Invalid save file at line 1: unknown task type", exception.getMessage());
    }

    /**
     * Verifies that saved records with the wrong number of fields are rejected.
     */
    @Test
    public void load_wrongFieldCount_throwsExceptionWithLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nubish.txt");
        Files.writeString(saveFile, "D | 0 | read book\n");
        Storage storage = new Storage(saveFile.toString(), new TaskList());

        NubishException exception = assertThrows(NubishException.class, storage::load);

        assertEquals("Invalid save file at line 1: wrong number of fields", exception.getMessage());
    }
}
