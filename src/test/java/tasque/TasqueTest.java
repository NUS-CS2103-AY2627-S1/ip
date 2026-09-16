package tasque;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TasqueTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void getCommandResponse_errorsThenSuccess_usesExplicitStatus() {
        Tasque tasque = new Tasque(this.tempDirectory.resolve("tasks.txt").toString());

        TasqueResponse invalid = tasque.getCommandResponse("unknown");
        assertTrue(invalid.isError());
        assertEquals("OOPS!!! I do not recognize that command.", invalid.getText());
        assertTrue(tasque.getCommandResponse("todo").isError());

        TasqueResponse added = tasque.getCommandResponse("todo OOPS!!! review error handling");
        assertFalse(added.isError());
        assertEquals("Got it. I've added this task:\n"
                + "[T][ ] OOPS!!! review error handling\n"
                + "Now you have 1 tasks in the list", added.getText());

        TasqueResponse duplicate = tasque.getCommandResponse("t OOPS!!! review error handling");
        assertTrue(duplicate.isError());
        assertEquals("OOPS!!! That task already exists in your task list.", duplicate.getText());
        TasqueResponse listed = tasque.getCommandResponse("list");
        assertFalse(listed.isError());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] OOPS!!! review error handling",
                listed.getText());
        assertTrue(invalid.isError());
        assertFalse(added.isError());
    }

    @Test
    public void getCommandResponse_delete_executesExactlyOnce() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | first\nT | 0 | second\nT | 0 | third\n");
        Tasque tasque = new Tasque(storagePath.toString());

        TasqueResponse deleted = tasque.getCommandResponse("delete 1");
        assertFalse(deleted.isError());
        assertEquals("Noted. I've removed this task:\n[T][ ] first\nNow you have 2 tasks in the list",
                deleted.getText());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] second\n2.[T][ ] third",
                new Tasque(storagePath.toString()).getResponse("list"));

        assertEquals("Noted. I've removed this task:\n[T][ ] second\nNow you have 1 tasks in the list",
                tasque.getResponse("delete 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] third",
                new Tasque(storagePath.toString()).getResponse("list"));
    }

    @Test
    public void getCommandResponse_stringApi_returnsSameTextAndStorage() throws Exception {
        Path typedPath = this.tempDirectory.resolve("typed.txt");
        Path stringPath = this.tempDirectory.resolve("string.txt");
        Tasque typedSession = new Tasque(typedPath.toString());
        Tasque stringSession = new Tasque(stringPath.toString());
        String[] commands = {"  t read  notes  ", "d report /by 2026-09-18",
            "e meeting /from 2026-09-17 /to 2026-09-17", "m 1", "u 1", "f report", "del 1",
            "d invalid /by tomorrow", "unknown", "l", "b"};

        for (String command : commands) {
            assertEquals(stringSession.getResponse(command), typedSession.getCommandResponse(command).getText(),
                    command);
            assertEquals(Files.readString(stringPath), Files.readString(typedPath), command);
        }
    }

    @Test
    public void getWelcomeResponse_healthySession_preservesGreeting() {
        Tasque tasque = new Tasque(this.tempDirectory.resolve("tasks.txt").toString());

        TasqueResponse welcome = tasque.getWelcomeResponse();
        assertFalse(welcome.isError());
        assertEquals("Hello! I'm Tasque.\nWhat can I do for you?", welcome.getText());
        assertEquals(welcome.getText(), tasque.getWelcomeMessage());
    }

    @Test
    public void getWelcomeResponse_failedLoad_marksCombinedMessageAndBlocksCommands() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0\n");
        byte[] original = Files.readAllBytes(storagePath);
        Tasque tasque = new Tasque(storagePath.toString());

        TasqueResponse welcome = tasque.getWelcomeResponse();
        assertTrue(welcome.isError());
        assertTrue(welcome.getText().startsWith("Hello! I'm Tasque.\nWhat can I do for you?\nOOPS!!! "));
        assertEquals(1, countOccurrences(welcome.getText(), "OOPS!!!"));
        assertEquals(welcome.getText(), tasque.getWelcomeMessage());
        for (String command : new String[] {"list", "todo blocked task", "unknown"}) {
            TasqueResponse blocked = tasque.getCommandResponse(command);
            assertTrue(blocked.isError());
            assertTrue(welcome.getText().endsWith("\n" + blocked.getText()));
            assertArrayEquals(original, Files.readAllBytes(storagePath));
        }
        for (String command : new String[] {"bye", "  b  "}) {
            TasqueResponse exit = tasque.getCommandResponse(command);
            assertFalse(exit.isError());
            assertEquals("Goodbye! See you again soon.", exit.getText());
        }
        assertArrayEquals(original, Files.readAllBytes(storagePath));
    }

    @Test
    public void getCommandResponse_saveFailure_reportsErrorAndRollsBack() throws Exception {
        Path dataDirectory = Files.createDirectory(this.tempDirectory.resolve("data"));
        Path storagePath = dataDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | first\nT | 1 | second\n");
        Tasque tasque = new Tasque(storagePath.toString());
        Files.delete(storagePath);
        Files.delete(dataDirectory);
        Files.writeString(dataDirectory, "blocks the storage directory");

        for (String command : new String[] {"todo third", "mark 1", "unmark 2", "delete 1"}) {
            TasqueResponse failed = tasque.getCommandResponse(command);
            assertTrue(failed.isError());
            assertEquals("OOPS!!! I couldn't save your tasks.", failed.getText());
            TasqueResponse listed = tasque.getCommandResponse("list");
            assertFalse(listed.isError());
            assertEquals("Here are the tasks in your list:\n1.[T][ ] first\n2.[T][X] second", listed.getText());
        }
    }

    @Test
    public void getResponse_unicodeBlankDescriptions_rejectsWithoutBreakingRestart() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | retained task\n");
        byte[] original = Files.readAllBytes(storagePath);
        Tasque tasque = new Tasque(storagePath.toString());
        String[][] commands = {
            {"deadline \u2003 /by 2026-08-26", "a deadline"},
            {"event \u2003 /from 2026-08-26 /to 2026-08-27", "an event"},
            {"todo \u2003", "a todo"}
        };
        for (String[] command : commands) {
            assertEquals("OOPS!!! The description of " + command[1] + " cannot be empty.",
                    tasque.getResponse(command[0]));
            assertArrayEquals(original, Files.readAllBytes(storagePath));
            assertEquals("Here are the tasks in your list:\n1.[T][ ] retained task", tasque.getResponse("list"));
            assertEquals(tasque.getResponse("list"), new Tasque(storagePath.toString()).getResponse("list"));
        }
    }

    @Test
    public void getResponse_exactIncompleteDuplicates_rejectsWithoutSaving() throws Exception {
        String[][] cases = {
            {"T | 0 | read book\n", "todo read book", " t read book "},
            {"D | 0 | report | 2026-08-26\n", "deadline report /by 2026-08-26", "d report /by 2026-08-26"},
            {"E | 0 | meeting | 2026-08-26 | 2026-08-27\n",
                "event meeting /from 2026-08-26 /to 2026-08-27", "e meeting /from 2026-08-26 /to 2026-08-27"}
        };
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        for (String[] duplicateCase : cases) {
            Files.writeString(storagePath, duplicateCase[0]);
            byte[] original = Files.readAllBytes(storagePath);
            Tasque tasque = new Tasque(storagePath.toString());
            String originalList = tasque.getResponse("list");
            for (int i = 1; i < duplicateCase.length; i++) {
                assertEquals("OOPS!!! That task already exists in your task list.",
                        tasque.getResponse(duplicateCase[i]));
                assertEquals(originalList, tasque.getResponse("list"));
                assertArrayEquals(original, Files.readAllBytes(storagePath));
            }
        }
    }

    @Test
    public void getResponse_similarTasks_acceptsDistinctDetails() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Tasque tasque = new Tasque(storagePath.toString());
        String[] commands = {
            "todo read book", "todo read booklet", "todo Read book", "todo read  book",
            "deadline read book /by 2026-08-26", "deadline read book /by 2026-08-27",
            "deadline read booklet /by 2026-08-26",
            "event read book /from 2026-08-26 /to 2026-08-27",
            "event read book /from 2026-08-25 /to 2026-08-27",
            "event read book /from 2026-08-26 /to 2026-08-28"
        };
        for (String command : commands) {
            assertTrue(tasque.getResponse(command).startsWith("Got it. I've added this task:"), command);
        }
        assertEquals(commands.length, new Storage(storagePath.toString()).loadTasks().size());
    }

    @Test
    public void getResponse_completedMatch_allowsRepeatButIncompleteMatchStillBlocks() throws Exception {
        String[] commands = {"todo repeated task", "deadline repeated task /by 2026-08-26",
            "event repeated task /from 2026-08-26 /to 2026-08-27"};
        for (int i = 0; i < commands.length; i++) {
            Path storagePath = this.tempDirectory.resolve("tasks-" + i + ".txt");
            Tasque tasque = new Tasque(storagePath.toString());
            assertTrue(tasque.getResponse(commands[i]).startsWith("Got it."));
            assertTrue(tasque.getResponse("mark 1").startsWith("Nice!"));
            assertTrue(tasque.getResponse(commands[i]).startsWith("Got it."));
            String originalList = tasque.getResponse("list");
            byte[] original = Files.readAllBytes(storagePath);

            assertEquals("OOPS!!! That task already exists in your task list.", tasque.getResponse(commands[i]));
            assertEquals(originalList, tasque.getResponse("list"));
            assertArrayEquals(original, Files.readAllBytes(storagePath));
            assertTrue(tasque.getResponse("unmark 1").startsWith("OK,"));
            assertEquals(2, new Storage(storagePath.toString()).loadTasks().size());
            assertEquals(tasque.getResponse("list"), new Tasque(storagePath.toString()).getResponse("list"));
        }
    }

    @Test
    public void getResponse_reversedEvent_preservesTasksAndStorage() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | retained task\n");
        byte[] original = Files.readAllBytes(storagePath);
        Tasque tasque = new Tasque(storagePath.toString());
        for (String command : new String[] {"event", "   e"}) {
            assertEquals("OOPS!!! The end date of an event cannot be before its start date.",
                    tasque.getResponse(command + " meeting /from 2026-08-17 /to 2026-08-16   "));
            assertEquals("Here are the tasks in your list:\n1.[T][ ] retained task", tasque.getResponse("list"));
            assertArrayEquals(original, Files.readAllBytes(storagePath));
        }
    }

    @Test
    public void getResponse_failedLoad_blocksCommandsAndPreservesFileUntilRestart() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 1 | valuable task\nX | 0 | unexpected task\n");
        byte[] original = Files.readAllBytes(storagePath);
        Tasque tasque = assertDoesNotThrow(() -> new Tasque(storagePath.toString()));
        String error = tasque.getResponse("list");

        assertTrue(error.startsWith("OOPS!!! I couldn't load your tasks"));
        assertTrue(error.contains("line 2"));
        assertTrue(error.contains("has not changed the file"));
        assertTrue(error.contains("restart Tasque"));
        assertEquals(1, countOccurrences(error, "OOPS!!!"));
        assertTrue(tasque.getWelcomeMessage().contains(error));
        String[] commands = {"list", "l", "find valuable", "f valuable", "todo new task", "t new task",
            "deadline task /by 2026-09-16", "d task /by 2026-09-16",
            "event task /from 2026-09-16 /to 2026-09-17", "e task /from 2026-09-16 /to 2026-09-17",
            "mark 1", "m 1", "unmark 1", "u 1", "delete 1", "del 1", "unknown", "b extra"};
        for (String command : commands) {
            assertEquals(error, tasque.getResponse(command), command);
            assertEquals(error, tasque.getResponse(" \t" + command + "\t "), command);
            assertArrayEquals(original, Files.readAllBytes(storagePath));
        }
        for (String command : new String[] {"bye", "b", "   bye   ", "\t b \t"}) {
            assertEquals("Goodbye! See you again soon.", tasque.getResponse(command));
            assertTrue(tasque.isExitCommand(command));
        }

        String repaired = "T | 1 | valuable task\n";
        Files.writeString(storagePath, repaired);
        assertEquals(error, tasque.getResponse("todo still blocked"));
        assertEquals(repaired, Files.readString(storagePath));
        assertEquals("Here are the tasks in your list:\n1.[T][X] valuable task",
                new Tasque(storagePath.toString()).getResponse("list"));
    }

    @Test
    public void run_paddedExit_stopsBeforeLaterCommands() throws Exception {
        for (String exit : new String[] {"   bye   ", "\t b \t"}) {
            Path storagePath = this.tempDirectory.resolve("tasks.txt");
            InputStream originalInput = System.in;
            PrintStream originalOutput = System.out;
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            try {
                String input = exit + "\ntodo should not be added\nbye\n";
                System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
                System.setOut(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
                new Tasque(storagePath.toString()).run();
            } finally {
                System.setIn(originalInput);
                System.setOut(originalOutput);
            }
            String output = outputBytes.toString(StandardCharsets.UTF_8);
            assertFalse(output.contains("OOPS!!!"), output);
            assertFalse(output.contains("should not be added"), output);
            assertTrue(output.endsWith("Goodbye! See you again soon."));
            assertFalse(Files.exists(storagePath));
        }
    }

    @Test
    public void getResponse_readFailureAfterValidPrefix_doesNotOverwriteFile() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | retained task\n".repeat(4096) + "T | 0 | damaged ");
        Files.write(storagePath, new byte[] {(byte) 0xff, '\n'}, StandardOpenOption.APPEND);
        byte[] original = Files.readAllBytes(storagePath);

        Tasque tasque = assertDoesNotThrow(() -> new Tasque(storagePath.toString()));

        assertTrue(tasque.getWelcomeMessage().contains("I couldn't load your tasks"));
        assertTrue(tasque.getResponse("todo new task").contains("I couldn't load your tasks"));
        assertTrue(tasque.getResponse("list").contains("I couldn't load your tasks"));
        assertArrayEquals(original, Files.readAllBytes(storagePath));
    }

    @Test
    public void run_truncatedFile_showsLoadErrorAtStartupAndExitsNormally() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0\n");
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("b\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
            assertDoesNotThrow(() -> new Tasque(storagePath.toString()).run());
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("OOPS!!! I couldn't load your tasks"));
        assertTrue(output.contains("line 1"));
        assertEquals(1, countOccurrences(output, "OOPS!!!"));
        assertTrue(output.endsWith("Goodbye! See you again soon."));
        assertEquals("T | 0\n", Files.readString(storagePath));
    }

    @Test
    public void getResponse_supportedCommands_returnsExpectedResponses() {
        Path storagePath = this.tempDirectory.resolve("data").resolve("tasque.txt");
        Tasque tasque = new Tasque(storagePath.toString());

        assertEquals("Hello! I'm Tasque.\nWhat can I do for you?", tasque.getWelcomeMessage());
        assertEquals("Got it. I've added this task:\n"
                + "[T][ ] read book\n"
                + "Now you have 1 tasks in the list", tasque.getResponse("todo read book"));
        assertEquals("Got it. I've added this task:\n"
                + "[D][ ] submit report (by: Aug 26 2026)\n"
                + "Now you have 2 tasks in the list",
                tasque.getResponse("deadline submit report /by 2026-08-26"));
        assertEquals("Got it. I've added this task:\n"
                + "[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)\n"
                + "Now you have 3 tasks in the list",
                tasque.getResponse("event project meeting /from 2026-08-28 /to 2026-08-29"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] submit report (by: Aug 26 2026)\n"
                + "3.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)",
                tasque.getResponse("list"));
        assertEquals("Nice! I've marked this task as done:\n[T][X] read book",
                tasque.getResponse("mark 1"));
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] read book",
                tasque.getResponse("unmark 1"));
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)",
                tasque.getResponse("find meeting"));
        assertEquals("Noted. I've removed this task:\n"
                + "[D][ ] submit report (by: Aug 26 2026)\n"
                + "Now you have 2 tasks in the list", tasque.getResponse("delete 2"));
        assertEquals("Goodbye! See you again soon.", tasque.getResponse("bye"));
    }

    @Test
    public void getResponse_invalidInput_returnsErrorAndKeepsSessionUsable() {
        Path storagePath = this.tempDirectory.resolve("data").resolve("tasque.txt");
        Tasque tasque = new Tasque(storagePath.toString());

        assertEquals("OOPS!!! I do not recognize that command.", tasque.getResponse("unknown"));
        assertEquals("OOPS!!! Please enter the date as yyyy-MM-dd.",
                tasque.getResponse("deadline submit report /by tomorrow"));
        assertEquals("Got it. I've added this task:\n"
                + "[T][ ] session continues\n"
                + "Now you have 1 tasks in the list", tasque.getResponse("todo session continues"));
    }

    @Test
    public void getResponse_mutatingCommands_persistForNewSession() {
        Path storagePath = this.tempDirectory.resolve("data").resolve("tasque.txt");
        Tasque firstSession = new Tasque(storagePath.toString());
        firstSession.getResponse("todo persistent task");
        firstSession.getResponse("mark 1");

        Tasque secondSession = new Tasque(storagePath.toString());

        assertEquals("Here are the tasks in your list:\n1.[T][X] persistent task",
                secondSession.getResponse("list"));
    }

    @Test
    public void run_saveFails_doesNotReportSuccessOrKeepMutations() throws Exception {
        Path dataDirectory = Files.createDirectory(this.tempDirectory.resolve("data"));
        Path storagePath = dataDirectory.resolve("tasque.txt");
        Files.writeString(storagePath, "T | 0 | first task\nT | 1 | second task\n");

        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        String input = "mark 1\nunmark 2\ndelete 1\ntodo third task\nlist\nbye\n";
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
            Tasque tasque = new Tasque(storagePath.toString());

            Files.delete(storagePath);
            Files.delete(dataDirectory);
            Files.writeString(dataDirectory, "blocks the storage directory");

            tasque.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertEquals(4, countOccurrences(output, "OOPS!!! I couldn't save your tasks."));
        assertFalse(output.contains("Got it. I've added this task:"));
        assertFalse(output.contains("Nice! I've marked this task as done:"));
        assertFalse(output.contains("OK, I've marked this task as not done yet:"));
        assertFalse(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("1.[T][ ] first task"));
        assertTrue(output.contains("2.[T][X] second task"));
        assertFalse(output.contains("third task"));
    }

    @Test
    public void getResponse_repeatedCompletionCommandsSaveFails_preservesPreviousStates() throws Exception {
        Path dataDirectory = Files.createDirectory(this.tempDirectory.resolve("data"));
        Path storagePath = dataDirectory.resolve("tasque.txt");
        Files.writeString(storagePath, "T | 0 | first task\nT | 1 | second task\n");
        Tasque tasque = new Tasque(storagePath.toString());

        Files.delete(storagePath);
        Files.delete(dataDirectory);
        Files.writeString(dataDirectory, "blocks the storage directory");

        String expectedList = "Here are the tasks in your list:\n"
                + "1.[T][ ] first task\n2.[T][X] second task";
        assertEquals("OOPS!!! I couldn't save your tasks.", tasque.getResponse("mark 2"));
        assertEquals(expectedList, tasque.getResponse("list"));
        assertEquals("OOPS!!! I couldn't save your tasks.", tasque.getResponse("unmark 1"));
        assertEquals(expectedList, tasque.getResponse("list"));
    }

    private int countOccurrences(String text, String target) {
        return text.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }
}
