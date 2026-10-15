package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests command-level format validation and application-boundary error handling.
 */
public class ParserTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void handleInput_generalWhitespace_parsesCommand() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));
        assertEquals(Ui.Message.HELP.getMessage(), ubis.getResponse("  \t help \t "));
    }

    @Test
    public void handleInput_argumentForArgumentlessCommand_returnsError() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));
        assertEquals("The \"list\" command does not accept any arguments.",
                ubis.getResponse("list unexpected"));
        assertEquals("The \"help\" command does not accept any arguments.",
                ubis.getResponse("help me"));
        assertEquals("The \"bye\" command does not accept any arguments.",
                ubis.getResponse("bye now"));
    }

    @Test
    public void handleInput_malformedTaskNumbers_returnsError() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));
        String[] invalidCommands = {
            "mark -1", "mark 1 2", "unmark 1.5", "delete +1", "delete task"
        };

        for (String command : invalidCommands) {
            assertTrue(ubis.getResponse(command).contains("Please enter one positive whole number."));
        }
        assertEquals("That task number is too large. Please enter a task number shown by \"list\".",
                ubis.getResponse("mark 999999999999999999999999"));
    }

    @Test
    public void handleInput_invalidTodo_returnsSpecificMessage() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));
        assertEquals("Please provide a name for the todo.\nExample: todo read a book",
                ubis.getResponse("todo"));
    }

    @Test
    public void handleInput_invalidDeadline_returnsSpecificMessage() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));

        assertTrue(ubis.getResponse("deadline report").contains("needs a \"/by\" parameter"));
        assertEquals("Please provide a task name before \"/by\".",
                ubis.getResponse("deadline /by 2026-09-30"));
        assertTrue(ubis.getResponse("deadline report /by").contains("provide a deadline date"));
        assertTrue(ubis.getResponse("deadline report /by 2026-02-30").contains("date is invalid"));
        assertTrue(ubis.getResponse(
                "deadline report /by 2026-09-29 /by 2026-09-30").contains("exactly one \"/by\""));
    }

    @Test
    public void handleInput_invalidEventParameters_returnsSpecificMessage() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));

        assertTrue(ubis.getResponse("event camp").contains("exactly one \"/from\""));
        assertTrue(ubis.getResponse("event camp /from 2026-09-01").contains("exactly one \"/to\""));
        assertEquals("The \"/from\" parameter must appear before \"/to\".",
                ubis.getResponse("event camp /to 2026-09-03 /from 2026-09-01"));
        assertTrue(ubis.getResponse(
                "event camp /from 2026-09-01 /from 2026-09-02 /to 2026-09-03")
                .contains("exactly one \"/from\""));
        assertTrue(ubis.getResponse("event /from 2026-09-01 /to 2026-09-03")
                .contains("provide an event name"));
    }

    @Test
    public void handleInput_invalidEventDates_returnsSpecificMessage() {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));

        assertTrue(ubis.getResponse("event camp /from bad-date /to 2026-09-03")
                .contains("start date is invalid"));
        assertTrue(ubis.getResponse("event camp /from 2026-09-01 /to bad-date")
                .contains("end date is invalid"));
        assertEquals("The event start date must be earlier than the end date.",
                ubis.getResponse("event camp /from 2026-09-03 /to 2026-09-01"));
        assertEquals("The event start date must be earlier than the end date.",
                ubis.getResponse("event camp /from 2026-09-03 /to 2026-09-03"));
    }

    @Test
    public void receiveInput_endOfStream_returnsNull() {
        Scanner emptyScanner = new Scanner(new ByteArrayInputStream(new byte[0]));
        Parser parser = new Parser(new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt")), emptyScanner);
        assertNull(parser.receiveInput());
        parser.cleanup();
    }

    @Test
    public void getResponse_unexpectedRuntimeFailure_returnsFriendlyMessage() {
        TaskList failingTaskList = new TaskList() {
            @Override
            public String listTasks() {
                throw new IllegalStateException("Deliberate test failure");
            }
        };
        Ubis ubis = new Ubis(failingTaskList, temporaryDirectory.resolve("tasks.txt"));

        assertEquals(Ui.Message.UNEXPECTED_ERROR.getMessage(), ubis.getResponse("list"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void handleInput_emptyInput_returnsPromptWithoutSaving(String input) {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(new TaskList(), savePath);
        assertEquals(Ui.Message.EMPTY_INPUT.getMessage(), ubis.getResponse(input));
        assertEquals("", ubis.getTaskList().toString());
        assertFalse(Files.exists(savePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo {book", "todo book}", "{", "}", "find {book}"})
    void handleInput_reservedCharacters_rejectsWithoutSaving(String input) {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(new TaskList(), savePath);
        assertEquals(Ui.Message.ILLEGAL_INPUT.getMessage(), ubis.getResponse(input));
        assertEquals("", ubis.getTaskList().toString());
        assertFalse(Files.exists(savePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "TODO", "BYE"})
    void handleInput_unknownCommand_returnsHelpHint(String command) {
        Ubis ubis = new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt"));
        assertEquals("Unknown command \"" + command + "\". Type \"help\" for commands!",
                ubis.getResponse(command));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "unmark", "delete"})
    void handleInput_missingOrOverflowingNumber_returnsSpecificError(String command) {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(new TaskList(), savePath);
        assertEquals("Please add the task number you want to " + command + "!\n"
                + "Example: \"" + command + " 4\" if you want to " + command + " the fourth task.",
                ubis.getResponse(command));
        assertEquals("That task number is too large. Please enter a task number shown by \"list\".",
                ubis.getResponse(command + " 2147483648"));
        for (String number : new String[] {"-1", "+1", "1.5", "1 2", "abc"}) {
            assertEquals("\"" + number + "\" is not a valid task number. Please enter one positive whole number.",
                    ubis.getResponse(command + " " + number));
        }
        assertFalse(Files.exists(savePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark 0", "mark 2", "unmark 0", "unmark 2", "delete 0", "delete 2",
        "todo", "deadline report /by bad", "event trip", "todo {bad}"})
    void handleInput_invalidMutation_preservesTasksAndSavedData(String input) throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(savePath);
        ubis.getResponse("todo read book");
        String before = Files.readString(savePath);
        ubis.getResponse(input);
        assertEquals(before, ubis.getTaskList().toString());
        assertEquals(before, Files.readString(savePath));
    }

    @Test
    public void handleInput_successfulCommands_returnsExpectedResponsesAndSaves() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(new TaskList(), savePath);
        assertEquals(Ui.Message.HELP.getMessage(), ubis.getResponse("help"));
        assertEquals(Ui.Message.GOODBYE.getMessage(), ubis.getResponse("bye"));
        assertEquals("No tasks to show", ubis.getResponse("list"));
        assertEquals("Please provide a keyword to find.\nExample: find book", ubis.getResponse("find"));
        assertFalse(Files.exists(savePath));
        assertEquals("added: [T][ ] read book", ubis.getResponse("  todo\tread book  "));
        assertEquals("added: [E][ ] trip (from: Sep 1 2026 to: Sep 3 2026)",
                ubis.getResponse("event trip /from 2026-09-01 /to 2026-09-03"));
        assertEquals("Nice! I've marked this task as DONE:\n  [T][X] read book", ubis.getResponse("mark 01"));
        assertEquals("{T}{1}{read book}\n{E}{0}{trip}{2026-09-01}{2026-09-03}\n", Files.readString(savePath));
        assertEquals("1: [T][X] read book", ubis.getResponse("find read book"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark 1", "unmark 1", "delete 1", "todo second",
        "deadline report /by 2026-09-30", "event trip /from 2026-09-01 /to 2026-09-03"})
    void handleInput_saveFailure_retainsMutationAndAppendsWarning(String input) throws IOException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("read book"));
        if (input.startsWith("unmark")) {
            tasks.markTask(1);
        }
        Path savePath = Files.createDirectory(temporaryDirectory.resolve("directory"));
        Ubis ubis = new Ubis(tasks, savePath);
        String response = ubis.getResponse(input);
        assertTrue(response.endsWith("\nWarning: Your change is available for this session, "
                + "but it could not be saved to disk."));
        switch (input.split(" ")[0]) {
            case "mark":
                assertEquals("{T}{1}{read book}\n", tasks.toString());
                break;
            case "unmark":
                assertEquals("{T}{0}{read book}\n", tasks.toString());
                break;
            case "delete":
                assertEquals("", tasks.toString());
                break;
            default:
                assertTrue(tasks.listTasks().contains("\n2: "));
        }
        assertTrue(Files.isDirectory(savePath));
    }

    @Test
    public void receiveInput_multipleLinesAndCleanup_readsThenClosesScanner() {
        Scanner scanner = new Scanner("help\n\nlist\n");
        Parser parser = new Parser(new Ubis(new TaskList(), temporaryDirectory.resolve("tasks.txt")), scanner);
        assertEquals("help", parser.receiveInput());
        assertEquals("", parser.receiveInput());
        assertEquals("list", parser.receiveInput());
        assertNull(parser.receiveInput());
        parser.cleanup();
        assertThrows(IllegalStateException.class, scanner::hasNextLine);
    }
}
