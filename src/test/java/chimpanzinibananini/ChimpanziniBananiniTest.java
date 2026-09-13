package chimpanzinibananini;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChimpanziniBananiniTest {
    private static final Clock REMINDER_CLOCK = Clock.fixed(
            Instant.parse("2026-09-09T07:00:00Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_find_formatsMatchesWithoutChangingTasksOrFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(dataFile, "T | 0 | write essay\nT | 1 | read book\nT | 0 | return book\n");
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile);
        String originalList = chatbot.getResponse("list");
        byte[] originalContents = Files.readAllBytes(dataFile);

        assertEquals("Here are the matching tasks in your list:\n"
                + "1. [T][X] read book\n2. [T][ ] return book", chatbot.getResponse("find book"));
        assertEquals("Here are the matching tasks in your list:\nYour task list is empty.",
                chatbot.getResponse("find missing"));
        assertEquals(originalList, chatbot.getResponse("list"));
        assertArrayEquals(originalContents, Files.readAllBytes(dataFile));
    }

    @Test
    void getResponse_deleteLastTask_persistsEmptyList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(dataFile, "T | 0 | read book\n");
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile);

        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\n"
                + "Now you have 0 tasks in the list.", chatbot.getResponse("delete 1"));
        assertEquals("Your task list is empty.", chatbot.getResponse("list"));
        assertEquals("", Files.readString(dataFile));
        assertEquals("Your task list is empty.", new ChimpanziniBananini(dataFile).getResponse("list"));
    }

    @Test
    void getResponse_invalidStoredDeadline_reportsLoadingErrorAndPreservesFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        String contents = "T | 0 | valid\nD | 0 | return book | 2023-02-29\n";
        Files.writeString(dataFile, contents);
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile);

        assertEquals("I couldn't load the task file: invalid data on line 2: "
                + "Use yyyy-MM-dd or d/M/yyyy HHmm (for example, 2/12/2019 1800)",
                chatbot.getResponse("todo new task"));
        assertEquals(contents, Files.readString(dataFile));
    }

    @Test
    void getResponse_failedSave_preservesTasksAndAllowsRetry() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        String contents = "T | 0 | first\nT | 1 | second\nT | 0 | third\n";
        for (String command : new String[] {"todo new task", "delete 2", "mark 1", "mark 2"}) {
            Files.writeString(dataFile, contents);
            ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile);
            String originalList = chatbot.getResponse("list");
            // A nonempty directory reliably prevents replacement on every supported operating system.
            Files.delete(dataFile);
            Files.createDirectory(dataFile);
            Path blocker = dataFile.resolve("blocker.txt");
            Files.writeString(blocker, "keep");

            assertTrue(chatbot.getResponse(command).startsWith("I couldn't save your tasks: "));
            assertEquals(originalList, chatbot.getResponse("list"));
            assertEquals("keep", Files.readString(blocker));

            Files.delete(blocker);
            Files.delete(dataFile);
            assertFalse(chatbot.getResponse(command).startsWith("I couldn't save your tasks: "));
            assertEquals(chatbot.getResponse("list"), new ChimpanziniBananini(dataFile).getResponse("list"));
            if (command.startsWith("todo")) {
                assertEquals(originalList + "\n4. [T][ ] new task", chatbot.getResponse("list"));
            }
        }
    }

    @Test
    void getResponse_markOutsideList_returnsValidationError() {
        ChimpanziniBananini chatbot = new ChimpanziniBananini(temporaryDirectory.resolve("duke.txt"));
        chatbot.getResponse("todo first");

        for (String command : new String[] {"mark 0", "mark -1", "mark 2", "mark 2147483647"}) {
            assertTrue(chatbot.getResponse(command).startsWith("That task number does not exist"));
        }
        assertEquals("1. [T][ ] first", chatbot.getResponse("list"));
    }

    @Test
    void getResponse_reminders_usesClockAndLeavesTasksAndFileUnchanged() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(dataFile, "D | 0 | later | 2026-09-16T15:00:00\n"
                + "\nD | 0 | now | 2026-09-09T15:00:00\n"
                + "D | 1 | completed | 2026-09-10T15:00:00\n"
                + "D | 0 | overdue | 2026-09-09T14:59:59\n"
                + "D | 0 | beyond | 2026-09-16T15:00:01\n");
        Files.setLastModifiedTime(dataFile, FileTime.from(Instant.parse("2020-01-01T00:00:00Z")));
        byte[] originalContents = Files.readAllBytes(dataFile);
        FileTime originalModifiedTime = Files.getLastModifiedTime(dataFile);
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile, REMINDER_CLOCK);
        String originalList = chatbot.getResponse("list");

        assertEquals("Here are your upcoming deadlines:\n"
                + "1. [D][ ] now (by: Sep 9 2026, 3:00PM)\n"
                + "2. [D][ ] later (by: Sep 16 2026, 3:00PM)", chatbot.getResponse("reminders"));
        assertEquals(originalList, chatbot.getResponse("list"));
        assertArrayEquals(originalContents, Files.readAllBytes(dataFile));
        assertEquals(originalModifiedTime, Files.getLastModifiedTime(dataFile));
    }

    @Test
    void getResponse_remindersWithoutTasks_returnsEmptyMessageWithoutCreatingFile() {
        Path dataFile = temporaryDirectory.resolve("missing.txt");
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile, REMINDER_CLOCK);

        assertEquals("You have no upcoming deadlines in the next 7 days.", chatbot.getResponse("reminders"));
        assertEquals("The reminders command takes no arguments.", chatbot.getResponse("reminders 7"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void getResponse_remindersWithLoadingError_preservesLoadingError() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(dataFile, "invalid task data");
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile, REMINDER_CLOCK);

        assertEquals(chatbot.getResponse("list"), chatbot.getResponse("reminders"));
        assertTrue(chatbot.getResponse("reminders").startsWith("I couldn't load the task file:"));
    }

    @Test
    void getResponse_addThenList_returnsResponsesAndPersistsTask() {
        Path dataFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        ChimpanziniBananini chatbot = new ChimpanziniBananini(dataFile);

        String addResponse = chatbot.getResponse("todo read book");
        String listResponse = chatbot.getResponse("list");

        assertTrue(addResponse.contains("read book"));
        assertEquals("1. [T][ ] read book", listResponse);
        assertEquals("1. [T][ ] read book", new ChimpanziniBananini(dataFile).getResponse("list"));
    }

    @Test
    void getResponse_invalidCommand_returnsParserError() {
        ChimpanziniBananini chatbot = new ChimpanziniBananini(
                temporaryDirectory.resolve("data").resolve("duke.txt"));

        String response = chatbot.getResponse("dance");

        assertTrue(response.contains("I don't know that command"));
    }
}
