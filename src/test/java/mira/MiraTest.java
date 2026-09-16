package mira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mira.exception.MiraException;

class MiraTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_validCommands_updatesStateAndBuildsResponses() throws MiraException {
        Mira mira = new Mira(tempDirectory.resolve("mira.txt"));

        assertTrue(mira.getResponse("todo read a book").contains("I've added this task"));
        assertTrue(mira.getResponse("list").contains("[T][ ] read a book"));
        assertTrue(mira.getResponse("mark 1").contains("[T][X] read a book"));
        assertTrue(mira.getResponse("find BOOK").contains("[T][X] read a book"));
        assertTrue(mira.getResponse("delete 1").contains("I've removed this task"));
        assertEquals("Your task list is empty.", mira.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommand_returnsRecoverableError() throws MiraException {
        Mira mira = new Mira(tempDirectory.resolve("mira.txt"));

        assertEquals("OOPS!!! Please enter a command.", mira.getResponse(""));
        assertTrue(mira.getResponse("something else").startsWith("OOPS!!!"));
        assertEquals("Bye. Hope to see you again soon!", mira.getResponse("bye"));
    }

    @Test
    void constructor_existingDataFile_restoresTasksForNewSession() throws MiraException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Mira originalSession = new Mira(dataFile);
        originalSession.getResponse("todo persisted task");

        Mira restoredSession = new Mira(dataFile);

        assertTrue(restoredSession.getResponse("list").contains("persisted task"));
    }

    @Test
    void find_thenMarkAndDelete_usesNumbersFromFullList() throws MiraException {
        Mira mira = new Mira(tempDirectory.resolve("mira.txt"));
        mira.getResponse("todo buy groceries");
        mira.getResponse("todo read book");
        mira.getResponse("todo return book");

        String results = mira.getResponse("find book");
        assertTrue(results.contains("2. [T][ ] read book"));
        assertTrue(results.contains("3. [T][ ] return book"));
        assertTrue(mira.getResponse("mark 2").contains("[T][X] read book"));
        assertTrue(mira.getResponse("delete 3").contains("return book"));
        assertTrue(mira.getResponse("list").contains("1. [T][ ] buy groceries"));
    }

    @Test
    void mutation_saveFailure_keepsMemoryAndRecoversAfterPathIsFixed()
            throws MiraException, IOException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Mira mira = new Mira(dataFile);
        mira.getResponse("todo original");
        String before = mira.getResponse("list");
        byte[] savedData = Files.readAllBytes(dataFile);
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Path blocker = Files.writeString(dataFile.resolve("blocker"), "keep me");

        for (String command : new String[]{"todo new", "mark 1", "delete 1"}) {
            assertTrue(mira.getResponse(command).contains("Nothing was changed."), command);
            assertEquals(before, mira.getResponse("list"), command);
            assertEquals("keep me", Files.readString(blocker));
        }
        try (var siblings = Files.list(tempDirectory)) {
            assertEquals(1, siblings.count(), "Failed saves should clean up their temporary files");
        }

        Files.delete(blocker);
        Files.delete(dataFile);
        Files.write(dataFile, savedData);
        assertTrue(mira.getResponse("mark 1").contains("[T][X] original"));
        assertEquals(mira.getResponse("list"), new Mira(dataFile).getResponse("list"));
    }

    @Test
    void restart_afterEveryMutation_preservesAllTaskTypesAndCompletion() throws MiraException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Mira mira = new Mira(dataFile);
        for (String command : new String[]{"todo đọc sách | chương 1", "deadline report /by 2028-02-29",
            "event meeting /from Mon 9am /to Mon 10am", "mark 2", "mark 1", "unmark 1", "delete 3"}) {
            assertTrue(!mira.getResponse(command).startsWith("OOPS!!!"), command);
            String expected = mira.getResponse("list");
            mira = new Mira(dataFile);
            assertEquals(expected, mira.getResponse("list"), command);
        }
        assertTrue(mira.getResponse("list").contains("[T][ ] đọc sách | chương 1"));
        assertTrue(mira.getResponse("list").contains("[D][X] report"));
    }

    @Test
    void invalidCommands_leavePersistedTasksUnchanged() throws MiraException, IOException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Mira mira = new Mira(dataFile);
        mira.getResponse("todo keep me");
        String savedData = Files.readString(dataFile);
        String taskList = mira.getResponse("list");
        for (String command : new String[]{"delete 2", "mark 0", "unmark -1", "todo", "nonsense",
            "deadline invalid /by 2026-02-29", "event incomplete /from 9am"}) {
            assertTrue(mira.getResponse(command).startsWith("OOPS!!!"), command);
            assertEquals(taskList, mira.getResponse("list"), command);
            assertEquals(savedData, Files.readString(dataFile), command);
        }
    }
}
