package kaya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks command replies and their effects on saved tasks.
 */
public class KayaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedReplies() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String addResponse = kaya.getResponse("todo read book");
        String listResponse = kaya.getResponse("list");

        assertEquals("Added to your plate:\n  [T][ ] read book\nYou have 1 task on your plate.", addResponse);
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_taskCommands_preserveRepliesAndSavedChanges() {
        Path dataFile = temporaryDirectory.resolve("kaya.txt");
        Kaya kaya = new Kaya(dataFile);
        kaya.getResponse("todo read book");

        assertEquals("Settled! You've finished:\n  [T][X] read book",
                kaya.getResponse("mark 1"));
        assertEquals("Here's what's on your plate:\n1.[T][X] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("No rush. I've marked this task as not done yet:\n  [T][ ] read book",
                kaya.getResponse("unmark 1"));
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book",
                new Kaya(dataFile).getResponse("list"));
        assertEquals("Here's what I found on your plate:\n1.[T][ ] read book",
                kaya.getResponse("find BOOK"));
        assertEquals("No matching tasks on your plate. Try another keyword.", kaya.getResponse("find missing"));
        assertEquals("Taken off your plate:\n  [T][ ] read book\nYou have 0 tasks on your plate.",
                kaya.getResponse("delete 1"));
        assertEquals("Your task list is empty. Time for a kopi break?", new Kaya(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_invalidTaskArguments_returnsErrorsWithoutChangingTasks() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("kaya.txt"));
        kaya.getResponse("todo read book");

        for (String command : new String[] {"mark 2", "unmark 0", "delete abc", "find"}) {
            assertTrue(kaya.getResponse(command).startsWith("Hmm. "));
            assertEquals("Here's what's on your plate:\n1.[T][ ] read book", kaya.getResponse("list"));
        }
    }

    @Test
    public void getResponse_invalidCommand_returnsUserFriendlyError() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("data").resolve("kaya.txt"));

        String response = kaya.getResponse("nonsense");

        assertTrue(response.startsWith("Hmm. I don't recognise that command."));
        assertTrue(response.contains("Try todo, deadline, event, list, find, mark, unmark, delete, update, or bye."));
    }

    @Test
    public void getResponse_saveFailure_rollsBackEveryTaskChange() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Kaya kaya = new Kaya(file);
        kaya.getResponse("todo read book");
        kaya.getResponse("todo return book");
        kaya.getResponse("deadline report /by 2026-09-20");
        kaya.getResponse("event meeting /from 2026-09-20 /to 2026-09-21");
        kaya.getResponse("mark 2");
        kaya.getResponse("mark 3");
        String originalList = kaya.getResponse("list");
        String originalFile = Files.readString(file);
        Path backup = temporaryDirectory.resolve("backup.txt");
        Files.move(file, backup);
        Files.createDirectory(file);
        Path blocker = file.resolve("keep.txt");
        Files.writeString(blocker, "Do not overwrite this directory.");

        for (String command : List.of("todo extra", "deadline report /by 2026-09-20",
                "event meeting /from 2026-09-20 /to 2026-09-21", "mark 1", "unmark 2", "unmark 3", "mark 4",
                "delete 1", "update 1 /description changed", "update 3 /by 2026-09-25")) {
            assertTrue(kaya.getResponse(command).startsWith("Sorry, I couldn't save"), command);
            assertEquals(originalList, kaya.getResponse("list"), command);
            assertEquals(originalFile, Files.readString(backup), command);
        }

        Files.delete(blocker);
        Files.delete(file);
        Files.move(backup, file);
        assertTrue(kaya.getResponse("mark 1").startsWith("Settled!"));
        assertEquals(kaya.getResponse("list"), new Kaya(file).getResponse("list"));
    }

    @Test
    public void getResponse_failedStartupLoad_doesNotOverwriteRecoveredFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(file);
        Kaya kaya = new Kaya(file);
        assertTrue(kaya.getStartupWarning().contains("Changes are disabled"));
        Files.delete(file);
        String recovered = "T | 0 | cmVjb3ZlcmVk\n";
        Files.writeString(file, recovered);

        assertTrue(kaya.getResponse("todo new task").startsWith("Sorry, I couldn't save"));
        assertEquals(recovered, Files.readString(file));
        assertEquals("Your task list is empty. Time for a kopi break?", kaya.getResponse("list"));
    }

    @Test
    public void getResponse_corruptedStartupData_allowsReadingButProtectsTheFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | cmVhZCBib29r\nmalformed record\n";
        Files.writeString(file, original);
        Kaya kaya = new Kaya(file);

        assertTrue(kaya.getStartupWarning().contains("1 unreadable task record"));
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", kaya.getResponse("list"));
        assertTrue(kaya.getResponse("mark 1").startsWith("Sorry, I couldn't save"));
        assertEquals(original, Files.readString(file));
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", kaya.getResponse("list"));
    }

    @Test
    public void getResponse_blankInput_returnsGuidanceWithoutChangingTasks() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("tasks.txt"));
        for (String input : new String[] {null, "", " \t "}) {
            assertTrue(kaya.getResponse(input).contains("Enter a command"));
        }
        assertEquals("Your task list is empty. Time for a kopi break?", kaya.getResponse("list"));
    }

    @Test
    public void getResponse_listAndBye_requireExactCommandsWithoutChangingSavedTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Kaya kaya = new Kaya(file);
        kaya.getResponse("todo keep this task");
        String original = Files.readString(file);

        for (String command : List.of("list extra", "bye later")) {
            assertTrue(kaya.getResponse(command).contains("does not take any extra details"), command);
            assertEquals(original, Files.readString(file), command);
        }
        assertEquals("Here's what's on your plate:\n1.[T][ ] keep this task", kaya.getResponse("  list  "));
        assertEquals("See you! One thing at a time, okay?", kaya.getResponse("  bye  "));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void getResponse_duplicateDescriptions_preservesBothTasksAndDeletesOnlySelectedOne() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Kaya kaya = new Kaya(file);
        kaya.getResponse("todo read book");
        assertTrue(kaya.getResponse("todo read book").contains("2 tasks on your plate"));
        kaya.getResponse("mark 1");

        assertEquals("Here's what I found on your plate:\n1.[T][X] read book\n2.[T][ ] read book",
                kaya.getResponse("find BOOK"));
        assertEquals(kaya.getResponse("list"), new Kaya(file).getResponse("list"));
        kaya.getResponse("delete 1");
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", new Kaya(file).getResponse("list"));
    }
}
