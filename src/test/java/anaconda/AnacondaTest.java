package anaconda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import anaconda.testutil.ConsoleSession;

/**
 * Tests application orchestration with isolated console streams and temporary saved-task files.
 */
@Timeout(15)
@ResourceLock("SYSTEM_STREAMS")
public class AnacondaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getCommandResponse_pipeInDescription_preservesSavedTasksAndUndoHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo existing task");
        String saved = Files.readString(file);
        for (String command : List.of("todo read | book", "deadline report| /by 2026-09-20",
                "event |meeting /from 2026-09-20 /to 2026-09-21")) {
            Anaconda.CommandResponse response = anaconda.getCommandResponse(command);
            assertEquals(Anaconda.ResponseStatus.WARNING, response.status());
            assertTrue(response.text().contains("Task descriptions cannot contain '|'."));
            assertTrue(response.text().contains("Format: " + command.split(" ")[0]));
            assertEquals(saved, Files.readString(file));
            Anaconda restarted = new Anaconda(file);
            assertFalse(restarted.hasLoadingError());
            assertTrue(restarted.getResponse("list").contains("1.[T][ ] existing task"));
        }
        anaconda.getResponse("undo");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void run_pipeInDescription_reportsErrorAndAcceptsCorrectedTask() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "todo read | book\ntodo read book\nbye\n");
        assertTrue(output.contains("Task descriptions cannot contain '|'."));
        assertTrue(output.contains("Format: todo DESCRIPTION"));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_validCommands_returnsSuccessAndExecutesOnce() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        Anaconda.CommandResponse response = anaconda.getCommandResponse("  ToDo read book  ");
        assertEquals(Anaconda.ResponseStatus.SUCCESS, response.status());
        assertTrue(response.text().contains("Now you have 1 tasks in the list."));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(file));
        for (String input : List.of("list", "/BY 2026-09-13", "/from 2026-09-13 sharp", "bye")) {
            assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse(input).status(), input);
        }
    }

    @Test
    public void getCommandResponse_duplicateTasks_savesOnceAndSupportsUndoAndRedo() throws IOException {
        String[][] commands = {
            {"todo Read book", "todo read BOOK"},
            {"deadline report /by 2026-09-13", "deadline REPORT /by 13-09-2026"},
            {"event meeting /from 2026-09-13 /to 2026-09-14",
                "event MEETING /from 13-09-2026 /to 14-09-2026"}
        };
        for (int i = 0; i < commands.length; i++) {
            Path file = temporaryDirectory.resolve("duplicates-" + i + ".txt");
            Anaconda anaconda = new Anaconda(file);
            assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse(commands[i][0]).status());
            anaconda.getResponse("mark 1");
            List<String> original = Files.readAllLines(file);
            anaconda = new Anaconda(file);
            Anaconda.CommandResponse response = anaconda.getCommandResponse(commands[i][1]);
            assertEquals(Anaconda.ResponseStatus.DUPLICATE, response.status());
            assertTrue(response.text().contains("Now you have 2 tasks in the list."));
            assertTrue(response.text().contains("Did you forget? This task already exists."));
            assertTrue(response.text().contains("We can undo anyways. . ."));
            List<String> duplicated = Files.readAllLines(file);
            assertEquals(2, duplicated.size());
            assertEquals(original.getFirst(), duplicated.getFirst());
            assertTrue(duplicated.getLast().contains(" | 0 | "));
            assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse("undo").status());
            assertEquals(original, Files.readAllLines(file));
            assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse("undo undo").status());
            assertEquals(duplicated, Files.readAllLines(file));
        }
    }

    @Test
    public void getCommandResponse_duplicateSaveFailure_reportsOnlyErrorAndPreservesHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        Files.delete(file);
        Files.createDirectory(file);
        Anaconda.CommandResponse response = anaconda.getCommandResponse("todo book");
        assertEquals(Anaconda.ResponseStatus.ERROR, response.status());
        assertEquals("Hang on. I couldn't save your task list.", response.text());
        assertEquals("Here's what you've got:" + System.lineSeparator() + "1.[T][ ] book",
                anaconda.getResponse("list"));
        Files.delete(file);
        anaconda.getResponse("undo");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void run_duplicateTask_warnsAndLeavesRemovalToUser() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "todo book\ntodo book\nlist\nundo\nbye\n");
        assertTrue(output.contains("Did you forget? This task already exists."));
        assertTrue(output.contains("We can undo anyways. . ."));
        assertTrue(output.contains("Here's what you've got:\n1.[T][ ] book\n2.[T][ ] book\n"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_invalidArguments_showsCommandSpecificFormatAndExample() throws IOException {
        String[][] cases = {
            {"todo", "todo DESCRIPTION", "todo read book"},
            {"deadline", "deadline DESCRIPTION /by DATE", "deadline report /by 2026-09-20"},
            {"deadline report /by", "deadline DESCRIPTION /by DATE", "deadline report /by 2026-09-20"},
            {"deadline /by 2026-09-20", "deadline DESCRIPTION /by DATE", "deadline report /by 2026-09-20"},
            {"deadline report /by 2026-02-30", "deadline DESCRIPTION /by DATE", "deadline report /by 2026-09-20"},
            {"event", "event DESCRIPTION /from START_DATE /to END_DATE",
                "event meeting /from 2026-09-20 /to 2026-09-21"},
            {"event meeting /from 2026-09-21 /to 2026-09-20", "event DESCRIPTION /from START_DATE /to END_DATE",
                "event meeting /from 2026-09-20 /to 2026-09-21"},
            {"event meeting /from tomorrow /to today", "event DESCRIPTION /from START_DATE /to END_DATE",
                "event meeting /from 2026-09-20 /to 2026-09-21"},
            {"mark", "mark TASK_NUMBER", "mark 1"},
            {"MARK abc", "mark TASK_NUMBER", "mark 1"},
            {"mark 2", "mark TASK_NUMBER", "mark 1"},
            {"unmark", "unmark TASK_NUMBER", "unmark 1"},
            {"unmark 1 2", "unmark TASK_NUMBER", "unmark 1"},
            {"unmark 0", "unmark TASK_NUMBER", "unmark 1"},
            {"delete", "delete TASK_NUMBER", "delete 1"},
            {"delete 2147483648", "delete TASK_NUMBER", "delete 1"},
            {"delete -1", "delete TASK_NUMBER", "delete 1"},
            {"  FiNd  ", "find KEYWORD", "find book"},
            {"/by", "/by DATE [sharp]", "/by 2026-09-20"},
            {"BY tomorrow", "/by DATE [sharp]", "/by 2026-09-20"},
            {"/BY 2026-09-20 extra", "/by DATE [sharp]", "/by 2026-09-20"},
            {"/from", "/from DATE [sharp]", "/from 2026-09-20"},
            {"FROM 2026-02-30 sharp", "/from DATE [sharp]", "/from 2026-09-20"},
            {"/from 2026-09-20 sharp extra", "/from DATE [sharp]", "/from 2026-09-20"}
        };
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        String saved = Files.readString(file);
        for (String[] testCase : cases) {
            Anaconda.CommandResponse response = anaconda.getCommandResponse(testCase[0]);
            assertEquals(Anaconda.ResponseStatus.WARNING, response.status(), testCase[0]);
            String guidance = "Format: " + testCase[1] + System.lineSeparator() + "Example: " + testCase[2];
            assertTrue(response.text().contains(guidance), testCase[0]);
            if (testCase[1].contains("DATE")) {
                assertTrue(response.text().contains("Dates: yyyy-MM-dd or dd-MM-yyyy."), testCase[0]);
            }
            assertEquals(saved, Files.readString(file), testCase[0]);
            String consoleOutput = runSession(file, testCase[0] + "\nbye\n");
            assertTrue(consoleOutput.contains(guidance.replace("\r\n", "\n")), testCase[0]);
        }
        anaconda.getResponse("undo");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getCommandResponse_simpleCommandsAndSuccessfulInput_omitsArgumentGuidance() {
        Anaconda anaconda = new Anaconda(temporaryDirectory.resolve("tasks.txt"));
        for (String command : List.of("list extra", "clear extra", "bye extra", "undo extra", "undo",
                "undo undo", "unknown", "", "todo book", "todo book", "find book", "list", "mark 1")) {
            assertFalse(anaconda.getResponse(command).contains("Format:"), command);
        }
    }

    @Test
    public void getCommandResponse_recognizedInvalidCommands_returnsWarning() {
        Anaconda anaconda = new Anaconda(temporaryDirectory.resolve("tasks.txt"));
        for (String input : List.of("todo", "deadline essay", "event meeting /from 2026-09-13",
                "deadline essay /by 2026-02-30", "mark abc", "mark 1", "delete 0", "find", "list extra",
                "clear extra", "bye now", "undo nonsense", "undo", "undo undo", "/BY", "/from invalid")) {
            Anaconda.CommandResponse response = anaconda.getCommandResponse(input);
            assertEquals(Anaconda.ResponseStatus.WARNING, response.status(), input);
            assertTrue(response.text().startsWith("Hang on."), input);
        }
    }

    @Test
    public void getCommandResponse_reversedEvent_warnsWithoutSavingOrAddingUndoStep() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        String command = "event meeting /from 2026-09-14 /to 2026-09-13";
        Anaconda.CommandResponse response = anaconda.getCommandResponse(command);
        assertEquals(Anaconda.ResponseStatus.WARNING, response.status());
        assertTrue(response.text().startsWith("Hang on. An event's start date cannot be later than its end date."
                + System.lineSeparator() + "Format: event DESCRIPTION /from START_DATE /to END_DATE"));
        assertFalse(Files.exists(file));

        anaconda.getResponse("todo book");
        String saved = Files.readString(file);
        assertEquals(response, anaconda.getCommandResponse(command));
        assertEquals(saved, Files.readString(file));
        assertFalse(anaconda.getResponse("list").contains("meeting"));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void run_reversedEvent_reportsErrorAndAcceptsCorrectedEvent() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "event meeting /from 2026-09-14 /to 2026-09-13\n"
                + "event meeting /from 2026-09-13 /to 2026-09-13\nlist\nbye\n");
        assertTrue(output.contains("Hang on. An event's start date cannot be later than its end date."));
        assertFalse(output.contains("Now you have 2 tasks"));
        assertTrue(output.contains("Here's what you've got:\n1.[E][ ] meeting (from: Sep 13 2026 to: Sep 13 2026)"));
        assertEquals(List.of("E | 0 | meeting | 2026-09-13 | 2026-09-13"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_impossibleDates_warnsWithoutChangingTasksOrUndoHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        String saved = Files.readString(file);
        for (String date : List.of("2026-02-30", "30-02-2026", "2023-02-29", "29-02-2100")) {
            for (String command : List.of("deadline report /by " + date,
                    "event meeting /from " + date + " /to 2101-01-01",
                    "event meeting /from 2020-01-01 /to " + date, "/by " + date, "/from " + date + " sharp")) {
                Anaconda.CommandResponse response = anaconda.getCommandResponse(command);
                assertEquals(Anaconda.ResponseStatus.WARNING, response.status(), command);
                assertTrue(response.text().startsWith(
                        "Hang on. Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format."
                                + System.lineSeparator() + "Format: "), command);
                assertEquals(saved, Files.readString(file), command);
            }
        }
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void run_impossibleDate_reportsErrorAndAcceptsValidLeapDay() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "deadline report /by 2024-02-30\n"
                + "deadline report /by 2024-02-29\nlist\nbye\n");
        assertTrue(output.contains("Hang on. Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format."));
        assertTrue(output.contains("Here's what you've got:\n1.[D][ ] report (by: Feb 29 2024)"));
        assertEquals(List.of("D | 0 | report | 2024-02-29"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_help_listsCommandsWithoutSavingOrAddingUndoHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        Anaconda.CommandResponse help = anaconda.getCommandResponse("  HeLp  ");
        assertEquals(Anaconda.ResponseStatus.SUCCESS, help.status());
        assertTrue(help.text().startsWith("Available commands:"));
        assertTrue(help.text().contains("Help: help"));
        assertFalse(Files.exists(file));
        anaconda.getResponse("todo book");
        String saved = Files.readString(file);
        assertEquals(help, anaconda.getCommandResponse("help"));
        assertEquals(saved, Files.readString(file));
        Anaconda.CommandResponse invalid = anaconda.getCommandResponse("help extra");
        assertEquals(Anaconda.ResponseStatus.WARNING, invalid.status());
        assertEquals("Hang on. The help command does not take extra text.", invalid.text());
        anaconda.getResponse("undo");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
        String console = runSession(file, "help\nbye\n");
        assertTrue(console.contains(help.text().replace("\r\n", "\n")));
        assertFalse(console.contains("Hang on."));
    }

    @Test
    public void getCommandResponse_unknownOrBlankInput_returnsError() {
        Anaconda anaconda = new Anaconda(temporaryDirectory.resolve("tasks.txt"));
        for (String input : List.of("blah", "todoo read book", "/todo read book", "yes", "", "   ", "???")) {
            Anaconda.CommandResponse response = anaconda.getCommandResponse(input);
            assertEquals(Anaconda.ResponseStatus.ERROR, response.status(), input);
            assertTrue(response.text().startsWith("Hang on."), input);
            assertTrue(response.text().endsWith("You can ask for help."), input);
            assertFalse(response.text().contains("Available commands:"), input);
            assertEquals(2, response.text().lines().count(), input);
        }
    }

    @Test
    public void run_unknownInput_suggestsHelpAndContinuesWithoutChangingTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "nonsense\ntodo book\nbye\n");
        assertTrue(output.contains("Hang on. Yeah... I don't recognize that command.\n"
                + "You can ask for help.\n"));
        assertFalse(output.contains("Available commands:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_clear_executesImmediatelyAndDoesNotConsumeNextCommand() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getCommandResponse("todo book");
        Anaconda.CommandResponse response = anaconda.getCommandResponse("  ClEaR  ");
        assertEquals(Anaconda.ResponseStatus.SUCCESS, response.status());
        assertEquals("Fine. Everything's gone.", response.text());
        assertEquals("", Files.readString(file));
        for (String input : List.of("yes", "no", "blah")) {
            assertEquals(Anaconda.ResponseStatus.ERROR, anaconda.getCommandResponse(input).status(), input);
        }
        for (String input : List.of("list", "clear", "todo new task", "bye")) {
            assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse(input).status(), input);
        }
        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(file));
    }

    @Test
    public void getCommandResponse_saveFailure_returnsErrorAndRestoresTasks() throws IOException {
        Path parent = temporaryDirectory.resolve("blocked");
        Files.writeString(parent, "This file prevents creating the data directory.");
        Anaconda anaconda = new Anaconda(parent.resolve("tasks.txt"));
        Anaconda.CommandResponse response = anaconda.getCommandResponse("todo book");
        assertEquals(Anaconda.ResponseStatus.ERROR, response.status());
        assertEquals("Hang on. I couldn't save your task list.", response.text());
        assertEquals("Here's what you've got:", anaconda.getCommandResponse("list").text());
        assertEquals(Anaconda.ResponseStatus.ERROR, anaconda.getCommandResponse("clear").status());
        assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse("list").status());
    }

    @Test
    public void run_immediateBye_exitsWithoutCreatingDataFile() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, " BYE \n");
        assertTrue(output.contains("Yo, it's Anaconda.\nWhat do you need?\n"));
        assertTrue(output.contains("Alright, off you go. Try to get something done."));
        assertFalse(output.contains("Hang on."));
        assertFalse(Files.exists(file));
    }

    @Test
    public void run_taskCommands_persistsChangesAndLoadsThemOnRestart() throws IOException {
        Path file = temporaryDirectory.resolve("data/tasks.txt");
        String output = runSession(file, "todo read book\ndeadline report /by 2026-08-19\n"
                + "event meeting /from 2026-08-18 /to 2026-08-20\nmark 2\nunmark 2\n"
                + "mark 3\ndelete 1\nlist\nbye\n");
        assertTrue(output.contains("Now you have 3 tasks in the list."));
        assertTrue(output.contains("Fine, that's done now:\n  [D][X] report"));
        assertTrue(output.contains("Really? Unmarked? Alright . . .\n  [D][ ] report"));
        assertTrue(output.contains("Gone. Hope you didn't need that:\n  [T][ ] read book"));
        String expectedList = "Here's what you've got:\n1.[D][ ] report (by: Aug 19 2026)\n"
                + "2.[E][X] meeting (from: Aug 18 2026 to: Aug 20 2026)\n";
        assertTrue(output.contains(expectedList));
        assertEquals(List.of("D | 0 | report | 2026-08-19", "E | 1 | meeting | 2026-08-18 | 2026-08-20"),
                Files.readAllLines(file));
        assertTrue(runSession(file, "list\nbye\n").contains(expectedList));
    }

    @Test
    public void run_invalidCommand_continuesToProcessLaterCommands() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "nonsense\ntodo\nmark 1\ntodo book\nbye now\nlist\nbye\n");
        assertTrue(output.contains("Hang on. Yeah... I don't recognize that command."));
        assertTrue(output.contains("Hang on. The description of a todo cannot be empty."));
        assertTrue(output.contains("Hang on. Task 1 does not exist."));
        assertTrue(output.contains("Hang on. The bye command cannot have extra text."));
        assertTrue(output.contains("Here's what you've got:\n1.[T][ ] book\n"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void run_clear_clearsSavedTasksAndResumesNormalCommands() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "todo book\nclear\nlist\nbye\n");
        assertFalse(output.contains("You sure?"));
        assertFalse(output.contains("Hang on."));
        assertTrue(output.contains("Fine. Everything's gone."));
        assertTrue(output.contains("Here's what you've got:\n"
                + "____________________________________________________________"));
        assertEquals("", Files.readString(file));
        assertFalse(runSession(file, "list\nbye\n").contains("[T]"));
    }

    @Test
    public void run_clearThenAdd_executesNextCommandNormally() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | book\n");
        String output = runSession(file, "clear\ntodo new task\nlist\nbye\n");
        assertTrue(output.contains("Fine. Everything's gone."));
        assertTrue(output.contains("Here's what you've got:\n1.[T][ ] new task\n"));
        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(file));
        assertFalse(output.contains("Hang on."));
    }

    @Test
    public void run_dateFilters_dispatchesBothDirectionsAndSharpWithoutSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String saved = "D | 0 | before | 2026-08-18\nD | 0 | exact | 2026-08-19\n"
                + "E | 0 | after | 2026-08-18 | 2026-08-20\nT | 0 | undated\n";
        Files.writeString(file, saved);
        String output = runSession(file, "/by 19-08-2026\n/from 19-08-2026\n/by 19-08-2026 sharp\nbye\n");
        String line = "____________________________________________________________\n";
        assertTrue(output.contains("Found these:\n1.[D][ ] before (by: Aug 18 2026)\n"
                + "2.[D][ ] exact (by: Aug 19 2026)\n" + line));
        assertTrue(output.contains("Found these:\n1.[D][ ] exact (by: Aug 19 2026)\n"
                + "2.[E][ ] after (from: Aug 18 2026 to: Aug 20 2026)\n" + line));
        assertTrue(output.contains("Found these:\n1.[D][ ] exact (by: Aug 19 2026)\n" + line));
        assertFalse(output.contains("undated"));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void run_findCommand_matchesDescriptionsIgnoringCaseWithoutSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String saved = "T | 1 | Read Book\nD | 0 | return book | 2026-08-19\n"
                + "E | 0 | book launch | 2026-08-18 | 2026-08-20\nD | 0 | report | 2026-08-20\n";
        Files.writeString(file, saved);
        String output = runSession(file, "find BOOK\nfind 2026\nfind\nbye\n");
        String line = "____________________________________________________________\n";
        assertTrue(output.contains("Found these:\n"
                + "1.[T][X] Read Book\n2.[D][ ] return book (by: Aug 19 2026)\n"
                + "3.[E][ ] book launch (from: Aug 18 2026 to: Aug 20 2026)\n" + line));
        assertTrue(output.contains("Nothing. No matching tasks.\n" + line));
        assertTrue(output.contains("Hang on. Give me a keyword to look for."));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void getResponse_commandsAndClear_returnsExistingMessagesAndUpdatesStorage()
            throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        String lineSeparator = System.lineSeparator();

        assertEquals(String.join(lineSeparator,
                "Alright, added it:",
                "  [T][ ] book",
                "Now you have 1 tasks in the list."), anaconda.getResponse("todo book"));
        assertEquals(String.join(lineSeparator,
                "Here's what you've got:",
                "1.[T][ ] book"), anaconda.getResponse("list"));
        assertTrue(anaconda.getResponse("unknown").contains("You can ask for help."));
        assertEquals("Fine. Everything's gone.", anaconda.getResponse("clear"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Here's what you've got:", anaconda.getResponse("list"));
        assertEquals("Alright, off you go. Try to get something done.", anaconda.getResponse("bye"));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void getResponse_successAndError_keepsConsoleStreamsUsable() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        try (ConsoleSession session = new ConsoleSession("list\nbye\n")) {
            InputStream consoleInput = System.in;
            PrintStream consoleOutput = System.out;
            Anaconda anaconda = new Anaconda(file);

            assertTrue(anaconda.getResponse("todo café 读书").contains("[T][ ] café 读书"));
            assertTrue(anaconda.getResponse("unknown").contains("You can ask for help."));
            assertSame(consoleInput, System.in);
            assertSame(consoleOutput, System.out);
            assertEquals("", session.output());

            anaconda.run();
            System.out.println("Console output still open.");
            assertTrue(session.output().contains("Here's what you've got:\n1.[T][ ] café 读书\n"));
            assertTrue(session.output().endsWith("Console output still open.\n"));
            assertFalse(session.output().contains("Alright, added it:"));
            assertFalse(session.output().contains("Hang on."));
        }
    }

    @Test
    public void getResponse_taskUpdates_persistsOnlyTheSelectedTaskChange() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | first\nT | 0 | second\n");
        Anaconda anaconda = new Anaconda(file);

        assertTrue(anaconda.getResponse("mark 2").contains("[T][X] second"));
        assertEquals(List.of("T | 0 | first", "T | 1 | second"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("unmark 2").contains("[T][ ] second"));
        assertEquals(List.of("T | 0 | first", "T | 0 | second"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("delete 1").contains("[T][ ] first"));
        assertEquals(List.of("T | 0 | second"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_failedTaskMutationSave_reportsOnlyError() throws IOException {
        for (String command : new String[] {"todo book", "mark 1", "unmark 1", "delete 1", "clear"}) {
            Path file = temporaryDirectory.resolve(command.replace(' ', '-'));
            Files.writeString(file, "T | 1 | existing task\n");
            Anaconda anaconda = new Anaconda(file);
            Files.delete(file);
            Files.createDirectory(file);

            assertEquals("Hang on. I couldn't save your task list.", anaconda.getResponse(command), command);
            assertTrue(anaconda.getResponse("list").contains("[T][X] existing task"), command);
        }
    }

    @Test
    public void getResponse_undoAndRedoEveryMutation_restoresSavedTasksAndExhaustsHistory() throws IOException {
        List<String> original = List.of("T | 0 | book", "D | 1 | report | 2026-09-10",
                "E | 0 | meeting | 2026-09-09 | 2026-09-11");
        String[] commands = {"todo new book", "deadline new report /by 2026-09-12",
            "event new meeting /from 2026-09-12 /to 2026-09-13", "mark 1", "unmark 2", "delete 2", "clear"};
        for (int i = 0; i < commands.length; i++) {
            Path file = temporaryDirectory.resolve("tasks-" + i + ".txt");
            Files.write(file, original);
            Anaconda anaconda = new Anaconda(file);
            assertFalse(anaconda.getResponse(commands[i]).startsWith("Hang on."), commands[i]);
            List<String> changed = Files.readAllLines(file);

            assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                    + System.lineSeparator() + "Here's what you've got:"), commands[i]);
            assertEquals(original, Files.readAllLines(file), commands[i]);
            assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
            assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                    + System.lineSeparator() + "Here's what you've got:"), commands[i]);
            assertEquals(changed, Files.readAllLines(file), commands[i]);
            assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
            assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                    + System.lineSeparator() + "Here's what you've got:"));
            assertEquals(original, Files.readAllLines(file), commands[i]);
            assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
        }
    }

    @Test
    public void getResponse_repeatedUndoUndo_reappliesChangesInOrder() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("clear");
        anaconda.getResponse("undo");
        anaconda.getResponse("undo");
        anaconda.getResponse("undo");

        assertTrue(anaconda.getResponse("  UnDo\t UnDo  ").startsWith("Undid the previous undo."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 1 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
    }

    @Test
    public void getResponse_otherInputAfterUndo_endsRedoChainButPreservesUndoHistory() throws IOException {
        String[] inputs = {"list", "find book", "/by 2026-09-10", "/from 2026-09-10 sharp",
            "unknown", "", "mark 0", "delete 2", "todo", "undo extra", "undo undo undo", "list extra", "bye"};
        for (int i = 0; i < inputs.length; i++) {
            Path file = temporaryDirectory.resolve("tasks-" + i + ".txt");
            Anaconda anaconda = new Anaconda(file);
            anaconda.getResponse("todo book");
            anaconda.getResponse("mark 1");
            anaconda.getResponse("undo");
            anaconda.getResponse(inputs[i]);

            assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"), inputs[i]);
            assertEquals(List.of("T | 0 | book"), Files.readAllLines(file), inputs[i]);
            assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                    + System.lineSeparator() + "Here's what you've got:"), inputs[i]);
            assertTrue(Files.readAllLines(file).isEmpty(), inputs[i]);
            assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                    + System.lineSeparator() + "Here's what you've got:"), inputs[i]);
            assertEquals(List.of("T | 0 | book"), Files.readAllLines(file), inputs[i]);
            assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"), inputs[i]);
        }
    }

    @Test
    public void getResponse_newMutationAfterUndo_discardsOldRedoStates() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("undo");
        anaconda.getResponse("todo report");

        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
        assertEquals(List.of("T | 0 | book", "T | 0 | report"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book", "T | 0 | report"), Files.readAllLines(file));
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
    }

    @Test
    public void getResponse_clearAfterUndo_endsRedoChainAndCanBeUndone() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("undo");
        assertEquals("Fine. Everything's gone.", anaconda.getResponse("clear"));
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_undoUndoWithoutHistory_reportsErrorWithoutSaving() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
        assertFalse(Files.exists(file));
    }

    @Test
    public void getResponse_undoUndoAfterRestart_hasNoSessionHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("undo");
        Anaconda restarted = new Anaconda(file);
        assertEquals("Hang on. There is no undo to reverse.", restarted.getResponse("undo undo"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_failedRedoSave_keepsCurrentStateAndAllowsRetry() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("undo");
        Files.delete(file);
        Files.createDirectory(file);

        assertEquals("Hang on. I couldn't save your task list.", anaconda.getResponse("undo undo"));
        Files.delete(file);
        assertTrue(anaconda.getResponse("undo undo").startsWith("Undid the previous undo."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 1 | book"), Files.readAllLines(file));
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getResponse_undoAndRedo_showsCurrentListWithoutEndingChain() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | book", "D | 1 | report | 2026-09-10",
                "E | 0 | meeting | 2026-09-09 | 2026-09-11"));
        Anaconda anaconda = new Anaconda(file);
        String originalRows = "1.[T][ ] book\n2.[D][X] report (by: Sep 10 2026)\n"
                + "3.[E][ ] meeting (from: Sep 09 2026 to: Sep 11 2026)";
        anaconda.getResponse("delete 2");

        assertEquals("Undid the previous command.\nHere's what you've got:\n" + originalRows,
                anaconda.getResponse("undo").replace("\r\n", "\n"));
        assertEquals("Undid the previous undo.\nHere's what you've got:\n1.[T][ ] book\n"
                + "2.[E][ ] meeting (from: Sep 09 2026 to: Sep 11 2026)",
                anaconda.getResponse("undo undo").replace("\r\n", "\n"));
        assertEquals("Undid the previous command.\nHere's what you've got:\n" + originalRows,
                anaconda.getResponse("undo").replace("\r\n", "\n"));

        anaconda.getResponse("clear");
        assertEquals("Undid the previous command.\nHere's what you've got:\n" + originalRows,
                anaconda.getResponse("undo").replace("\r\n", "\n"));
        assertEquals("Undid the previous undo.\nHere's what you've got:",
                anaconda.getResponse("undo undo").replace("\r\n", "\n"));
        assertEquals("Undid the previous command.\nHere's what you've got:\n" + originalRows,
                anaconda.getResponse("undo").replace("\r\n", "\n"));
        Anaconda initiallyEmpty = new Anaconda(temporaryDirectory.resolve("empty.txt"));
        initiallyEmpty.getResponse("todo another");
        assertEquals("Undid the previous command.\nHere's what you've got:",
                initiallyEmpty.getResponse("undo").replace("\r\n", "\n"));
    }

    @Test
    public void run_undoUndo_restoresChangesUntilAnotherCommandEndsChain() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file,
                "todo book\nmark 1\nundo\nundo undo\nundo\nlist\nundo undo\nbye\n");
        assertTrue(output.contains("Undid the previous undo.\nHere's what you've got:\n1.[T][X] book\n"));
        assertTrue(output.contains("Undid the previous command.\nHere's what you've got:\n1.[T][ ] book\n"));
        assertTrue(output.contains("Hang on. There is no undo to reverse."));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_repeatedUndo_restoresEachEarlierState() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("clear");

        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 1 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getResponse_readOnlyAndInvalidCommands_preserveUndoHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
        assertFalse(Files.exists(file));
        anaconda.getResponse("todo book");
        for (String command : new String[] {"list", "find book", "/by 2026-09-10", "/from 2026-09-10 sharp",
            "unknown", "mark 0", "delete 2", "todo", "undo extra", "clear extra", "no"}) {
            anaconda.getResponse(command);
        }
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getResponse_noOpMutations_areSeparateUndoSteps() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("mark 1");
        anaconda.getResponse("undo");
        assertEquals(List.of("T | 1 | book"), Files.readAllLines(file));
        anaconda.getResponse("undo");
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        anaconda.getResponse("undo");
        anaconda.getResponse("clear");
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getResponse_newMutationAfterUndo_keepsRemainingEarlierHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo first");
        anaconda.getResponse("todo second");
        anaconda.getResponse("undo");
        anaconda.getResponse("deadline report /by 2026-09-10");
        anaconda.getResponse("undo");
        assertEquals(List.of("T | 0 | first"), Files.readAllLines(file));
        anaconda.getResponse("undo");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterClear_restoresClearedTasksImmediately() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("clear");
        assertTrue(Files.readAllLines(file).isEmpty());
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void getResponse_undoAfterRestart_hasNoSessionHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        new Anaconda(file).getResponse("todo book");
        Anaconda restarted = new Anaconda(file);
        assertEquals("Hang on. There is nothing to undo.", restarted.getResponse("undo"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_failedUndoSave_keepsCurrentStateAndAllowsRetry() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        anaconda.getResponse("mark 1");
        Files.delete(file);
        Files.createDirectory(file);

        assertEquals("Hang on. I couldn't save your task list.", anaconda.getResponse("undo"));
        assertEquals("Hang on. There is no undo to reverse.", anaconda.getResponse("undo undo"));
        assertTrue(anaconda.getResponse("list").contains("[T][X] book"));
        Files.delete(file);
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void getResponse_failedMutationSave_restoresStateAndPreservesUndoHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Anaconda anaconda = new Anaconda(file);
        anaconda.getResponse("todo book");
        Files.delete(file);
        Files.createDirectory(file);
        for (String command : new String[] {"todo another", "mark 1", "delete 1", "clear"}) {
            assertEquals("Hang on. I couldn't save your task list.", anaconda.getResponse(command), command);
        }
        assertTrue(anaconda.getResponse("list").contains("1.[T][ ] book"));
        assertFalse(anaconda.getResponse("list").contains("another"));
        Files.delete(file);
        assertTrue(anaconda.getResponse("undo").startsWith("Undid the previous command."
                + System.lineSeparator() + "Here's what you've got:"));
        assertTrue(Files.readAllLines(file).isEmpty());
        assertEquals("Hang on. There is nothing to undo.", anaconda.getResponse("undo"));
    }

    @Test
    public void run_undoClearAndDelete_restoresTasksAndRecoversFromEmptyHistory() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String output = runSession(file, "undo\ntodo book\ndelete 1\nundo\nclear\nundo\nlist\nbye\n");
        assertTrue(output.contains("Hang on. There is nothing to undo."));
        assertTrue(output.contains("Undid the previous command."));
        assertTrue(output.contains("Here's what you've got:\n1.[T][ ] book\n"));
        assertEquals(List.of("T | 0 | book"), Files.readAllLines(file));
    }

    @Test
    public void constructor_corruptedFile_discardsWholeListAndAllowsSavingNewTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        for (String malformed : List.of("garbage", "T | 0", "T | maybe | book", "T | 0 | book | extra",
                "D | 0 | report | 2026-02-30", "E | 0 | meeting | tomorrow | 2026-09-20", "")) {
            String saved = "T | 0 | valid task\n" + malformed + "\n";
            Files.writeString(file, saved);
            try (ConsoleSession session = new ConsoleSession("")) {
                Anaconda anaconda = new Anaconda(file);
                assertTrue(anaconda.hasLoadingError());
                assertTrue(session.output().contains("Starting with a new empty list."));
                assertEquals("Here's what you've got:", anaconda.getResponse("list"));
                assertEquals(saved, Files.readString(file));
                assertEquals(Anaconda.ResponseStatus.SUCCESS, anaconda.getCommandResponse("todo new task").status());
                assertEquals(List.of("T | 0 | new task"), Files.readAllLines(file));
                assertFalse(new Anaconda(file).hasLoadingError());
            }
        }
    }

    @Test
    public void constructor_unreadableDataFile_reportsErrorAndStartsEmpty() {
        String output = runSession(temporaryDirectory, "list\nbye\n");
        assertTrue(output.startsWith("Hang on. Your saved list was compromised or could not be read. "
                + "Starting with a new empty list.\n"));
        assertTrue(output.contains("Here's what you've got:\n"
                + "____________________________________________________________"));
        assertTrue(output.contains("Alright, off you go. Try to get something done."));
    }

    @Test
    public void run_saveFailure_reportsErrorAndContinuesWithoutSuccessMessage() throws IOException {
        Path parentFile = Files.createFile(temporaryDirectory.resolve("not-a-folder"));
        String output = runSession(parentFile.resolve("tasks.txt"), "todo book\nbye\n");
        assertTrue(output.contains("Hang on. I couldn't save your task list."));
        assertFalse(output.contains("Alright, added it:"));
        assertTrue(output.contains("Alright, off you go. Try to get something done."));
    }

    @Test
    public void main_newWorkingDirectory_usesRelativeDefaultDataPath() throws Exception {
        // Use a child JVM to test main's hard-coded relative path without changing
        // this JVM's working directory.
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path classes = Path.of(Anaconda.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path input = temporaryDirectory.resolve("input.txt");
        Path output = temporaryDirectory.resolve("output.txt");
        Files.writeString(input, "todo main task\nbye\n");
        Process process = new ProcessBuilder(java.toString(), "-cp", classes.toString(), "anaconda.Anaconda")
                .directory(temporaryDirectory.toFile())
                .redirectInput(input.toFile())
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Application did not exit after bye.");
            assertEquals(0, process.exitValue(), Files.readString(output));
            assertEquals(List.of("T | 0 | main task"),
                    Files.readAllLines(temporaryDirectory.resolve("data/anaconda.txt")));
            assertTrue(Files.readString(output).contains("Alright, off you go. Try to get something done."));
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(2, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Runs one chatbot session with a supplied storage path and restores console streams afterwards.
     */
    private String runSession(Path file, String input) {
        try (ConsoleSession session = new ConsoleSession(input)) {
            new Anaconda(file).run();
            return session.output();
        }
    }
}
