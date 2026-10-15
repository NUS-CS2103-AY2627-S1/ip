package ayre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ayre.exceptions.TaskLogCorruptedException;
import ayre.storage.LogParser;
import ayre.storage.Storage;
import ayre.tasks.Deadline;
import ayre.tasks.Event;
import ayre.tasks.Task;
import ayre.tasks.Todo;

public class StorageTest {
    @TempDir
    Path tmpDir;

    @Test
    public void load_validSaveFile_success() throws IOException {
        Path testFile = tmpDir.resolve("test.txt");
        Files.writeString(testFile, """
                T 0 test
                T 1 hi
                D 0 abc 2003-09-09
                D 1 cba 2004-10-10
                E 0 bla 1999-10-01 2000-10-10
                E 1 crime 2000-10-01 2000-10-10
                """);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("test"));
        Task tmpTodo = new Todo("hi");
        tmpTodo.markComplete();
        tasks.addTask(tmpTodo);
        tasks.addTask(new Deadline("abc", "2003-09-09"));
        Task tmpDeadline = new Deadline("cba", "2004-10-10");
        tmpDeadline.markComplete();
        tasks.addTask(tmpDeadline);
        tasks.addTask(new Event("bla", "1999-10-01", "2000-10-10"));
        Task tmpEvent = new Event("crime", "2000-10-01", "2000-10-10");
        tmpEvent.markComplete();
        tasks.addTask(tmpEvent);
        Storage storage = new Storage(testFile.toString());
        assertEquals(tasks.toString(), storage.load().tasks().toString());
    }

    @Test
    public void load_invalidSaveFile_accumulatesWarnings() throws IOException {
        Path testFile = tmpDir.resolve("test.txt");
        Files.writeString(testFile, """
                T 0
                T 1 hi
                D 0 abc be
                lalalalalala
                F 1 cba 2004-10-10
                E 0 bla 1999-10-01
                E 1 crime GAGAGA 2000-10-10
                """);
        Storage storage = new Storage(testFile.toString());
        assertEquals("""
                <<Main System: Mission Log Corrupted>>
                Dropped log line at index 0: Missing data detected
                Dropped log line at index 2: Data corrupted
                Dropped log line at index 3: Data corrupted
                Dropped log line at index 4: Data corrupted
                Dropped log line at index 5: Missing data detected
                Dropped log line at index 6: Data corrupted
                """, storage.load().loadMessage());
    }

    @Test
    public void constructNewTask_missingData_exceptionThrown() {
        Path testFile = tmpDir.resolve("test.txt");
        Storage storage = new Storage(testFile.toString());
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "T"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "T", "0"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "D", "0", "hi"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "hi", "a"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0"));
    }

    @Test
    public void constructNewTask_tooManyArguments_exceptionThrown() {
        Path testFile = tmpDir.resolve("test.txt");
        Storage storage = new Storage(testFile.toString());
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "T", "0", "hi", "hi again"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "D", "0", "name", "2000-10-01", "2000-10-01"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "D", "0", "bleh", "2000-10-01", "testing!"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "boring", "2000-10-01", "2000-10-10", "2000-10-11"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "testing", "2000-10-01", "2000-10-10", "hmm..."));
    }

    @Test
    public void constructNewTask_corruptedData_exceptionThrown() {
        Path testFile = tmpDir.resolve("test.txt");
        Storage storage = new Storage(testFile.toString());
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "D", "0", "hi", "1"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "D", "0", "hi", "haha"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "hi", "1", "2"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "hi", "1999-10-01", "2"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "E", "0", "hi", "1", "1999-10-01"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, "testing testing!"));
        assertThrows(TaskLogCorruptedException.class, () ->
                LogParser.constructNewTask(0, ""));
    }
}
