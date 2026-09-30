package bean.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bean.task.BeanList;
import bean.task.Priority;

public class MemoryHandlerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void readMemory_invalidDate_skipsLineAndLoadsFollowingTasks() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        Files.writeString(memoryFile, "1|0|0|Invalid date|not-a-date\n"
                + "0|0|2|Valid task|\n");
        BeanList taskList = new BeanList();

        new MemoryHandler(memoryFile.toString()).readMemory(taskList);

        assertEquals(1, taskList.getSize());
        assertEquals("Valid task", taskList.getTaskName(1));
    }

    @Test
    public void readMemory_unknownType_skipsLineAndLoadsFollowingTasks() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        Files.writeString(memoryFile, "9|0|0|Unknown task|\n"
                + "0|0|0|Valid task|\n");
        BeanList taskList = new BeanList();

        new MemoryHandler(memoryFile.toString()).readMemory(taskList);

        assertEquals(1, taskList.getSize());
        assertEquals("Valid task", taskList.getTaskName(1));
    }

    @Test
    public void writeAndReadMemory_roundTrip_preservesTasksPrioritiesAndStatus() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        BeanList original = new BeanList();
        original.addTodoSilent("Prepare slides", Priority.HIGH);
        original.addDeadlineSilent("Submit report", "2026-08-28", Priority.MEDIUM);
        original.addEventSilent("Project meeting", "2026-09-01", "2026-09-02", Priority.LOW);
        original.markTask(2);

        MemoryHandler handler = new MemoryHandler(memoryFile.toString());
        handler.writeMemory(original);

        BeanList restored = new BeanList();
        handler.readMemory(restored);

        assertEquals(3, restored.getSize());
        assertEquals("0|0|2|Prepare slides|", restored.formatTask(0));
        assertEquals("1|1|1|Submit report|2026-08-28", restored.formatTask(1));
        assertEquals("2|0|0|Project meeting|2026-09-01|2026-09-02", restored.formatTask(2));
        assertTrue(restored.displayTasks().contains("MEDIUM [D][X] Submit report"));
    }

    @Test
    public void writeAndReadMemory_escapesPipesAndBackslashesInTaskNames() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("special-characters.txt");
        BeanList original = new BeanList();
        original.addTodoSilent("Call | bank \\\\ today", Priority.HIGH);

        MemoryHandler handler = new MemoryHandler(memoryFile.toString());
        handler.writeMemory(original);

        BeanList restored = new BeanList();
        handler.readMemory(restored);

        assertEquals("Call | bank \\\\ today", restored.getTaskName(1));
    }

    @Test
    public void readMemory_missingFile_createsEmptyMemoryFile() {
        Path memoryFile = temporaryDirectory.resolve("new-memory.txt");

        new MemoryHandler(memoryFile.toString()).readMemory(new BeanList());

        assertTrue(Files.exists(memoryFile));
    }

    @Test
    public void readMemory_invalidFields_skipsEachLineAndContinues() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        Files.writeString(memoryFile, "0|3|0|Bad status|\n"
                + "0|0|8|Bad priority|\n"
                + "0|0|0||\n"
                + "0|0|0|Too|many|fields\n"
                + "0|0|0|Valid task|\n");
        BeanList taskList = new BeanList();

        new MemoryHandler(memoryFile.toString()).readMemory(taskList);

        assertEquals(1, taskList.getSize());
        assertEquals("Valid task", taskList.getTaskName(1));
        assertFalse(taskList.displayTasks().contains("Bad"));
    }

    @Test
    public void readMemory_invalidEventDate_skipsLineAndLoadsFollowingTask() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        Files.writeString(memoryFile, "2|0|0|Invalid event|2026-09-01|not-a-date\n"
                + "0|0|0|Valid todo|\n");
        BeanList taskList = new BeanList();

        new MemoryHandler(memoryFile.toString()).readMemory(taskList);

        assertEquals(1, taskList.getSize());
        assertEquals("Valid todo", taskList.getTaskName(1));
    }

    @Test
    public void readMemory_blankLines_areIgnored() throws IOException {
        Path memoryFile = temporaryDirectory.resolve("memory.txt");
        Files.writeString(memoryFile, "\n\n0|0|0|Valid todo|\n");
        BeanList taskList = new BeanList();

        new MemoryHandler(memoryFile.toString()).readMemory(taskList);

        assertEquals(1, taskList.getSize());
    }
}
