package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KopiTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_supportedCommands_commandsExecuted() {
        Kopi kopi = new Kopi(tempDirectory.resolve("kopi.txt"));

        assertTrue(kopi.getResponse("todo read book").contains("read book"));
        assertTrue(kopi.getResponse("deadline return book /by 2026-09-06").contains("return book"));
        assertTrue(kopi.getResponse(
                "event project meeting /from 2026-09-07 /to 2026-09-08").contains("project meeting"));

        String taskList = kopi.getResponse("list");
        assertTrue(taskList.contains("1. [T][ ] read book"));
        assertTrue(taskList.contains("2. [D][ ] return book"));
        assertTrue(taskList.contains("3. [E][ ] project meeting"));

        String matches = kopi.getResponse("find BOOK");
        assertTrue(matches.contains("read book"));
        assertTrue(matches.contains("return book"));
        assertFalse(matches.contains("project meeting"));

        assertTrue(kopi.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(kopi.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(kopi.getResponse("delete 3").contains("project meeting"));
        assertEquals("Bye. Hope to see you again soon!", kopi.getResponse("bye"));
        assertEquals("OOPS!!! Use: list", kopi.getResponse("list later"));
        assertEquals("OOPS!!! Use: bye", kopi.getResponse("bye later"));
        assertEquals("OOPS!!! I don't understand that command.", kopi.getResponse("dance"));
        assertTrue(kopi.isExitCommand("bye"));
        assertFalse(kopi.isExitCommand("bye later"));
    }

    @Test
    void getResponse_savedTask_taskLoadedByNewInstance() {
        Path dataPath = tempDirectory.resolve("nested").resolve("kopi.txt");
        Kopi firstKopi = new Kopi(dataPath);
        firstKopi.getResponse("todo persist this task");

        Kopi secondKopi = new Kopi(dataPath);

        assertTrue(secondKopi.getResponse("list").contains("persist this task"));
    }

    @Test
    void getResponse_persistedCompletedDuplicate_errorReturnedAndListUnchanged() {
        Path dataPath = tempDirectory.resolve("kopi.txt");
        Kopi firstKopi = new Kopi(dataPath);
        firstKopi.getResponse("todo read book");
        firstKopi.getResponse("mark 1");

        Kopi secondKopi = new Kopi(dataPath);
        String duplicateResponse = secondKopi.getResponse("todo READ BOOK");
        String taskList = secondKopi.getResponse("list");

        assertEquals("OOPS!!! That task is already in the list.", duplicateResponse);
        assertTrue(taskList.contains("1. [T][X] read book"));
        assertFalse(taskList.contains("2. [T]"));
    }

    @Test
    void getResponse_corruptStorage_modifyingCommandRejectedAndFilePreserved() throws IOException {
        Path dataPath = tempDirectory.resolve("kopi.txt");
        Files.writeString(dataPath, "not a task");
        Kopi kopi = new Kopi(dataPath);

        String response = kopi.getResponse("todo do not overwrite data");

        assertEquals(
                "OOPS!!! I won't change tasks because the data file could not be loaded. "
                        + "Fix or remove it, then restart Kopi.",
                response);
        assertEquals("not a task", Files.readString(dataPath));
    }

    @Test
    void getResponse_saveFailure_taskListUnchanged() throws IOException {
        Path blockedParent = tempDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "file blocks directory creation");
        Path dataPath = blockedParent.resolve("kopi.txt");
        Kopi kopi = new Kopi(dataPath);

        String response = kopi.getResponse("todo should not remain in memory");

        assertEquals("OOPS!!! I couldn't save tasks to " + dataPath + ".", response);
        assertFalse(kopi.getResponse("list").contains("should not remain in memory"));
    }
}
