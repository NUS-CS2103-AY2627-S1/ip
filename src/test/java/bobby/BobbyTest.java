package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Bobby's response method used by the JavaFX controller. */
public class BobbyTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies a healthy instance returns the shared welcome message without a warning. */
    @Test
    public void getWelcomeMessage_validStorage_plainWelcomeReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("welcome.txt").toString());

        assertEquals(Ui.WELCOME_MESSAGE, bobby.getWelcomeMessage());
    }

    /** Verifies the public constructor creates a usable application instance. */
    @Test
    public void constructor_defaultPath_instanceCreated() {
        Bobby bobby = new Bobby();

        assertEquals(Bobby.class, bobby.getClass());
    }

    /** Verifies that a GUI response uses the existing add-command behavior. */
    @Test
    public void getResponse_addCommand_returnsExistingCommandResponse() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("bobby.txt").toString());

        String response = bobby.getResponse("todo learn Java");

        assertTrue(response.contains("Boop! I've tucked this task into the list:"));
        assertTrue(response.contains("[T][ ] learn Java"));
        assertEquals("AddCommand", bobby.getCommandType());
        assertFalse(bobby.wasLastResponseError());

        bobby.getResponse("mark 1");
        assertEquals("MarkCommand", bobby.getCommandType());

        bobby.getResponse("unmark 1");
        assertEquals("UnmarkCommand", bobby.getCommandType());

        bobby.getResponse("delete 1");
        assertEquals("DeleteCommand", bobby.getCommandType());

        bobby.getResponse("bye");
        assertEquals("ExitCommand", bobby.getCommandType());
    }

    /** Verifies priority aliases, exact output, clearing, and stable task order. */
    @Test
    public void getResponse_priorityCommands_updatePriorityWithoutReordering() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("priority.txt").toString());
        bobby.getResponse("todo first task");
        bobby.getResponse("todo second task");

        String updateResponse = bobby.getResponse("priority 2 1");

        assertEquals("Beep boop! I've updated this task's priority:" + System.lineSeparator()
                + "  [T][ ][P: HIGH] second task" + System.lineSeparator(), updateResponse);
        assertEquals("PriorityCommand", bobby.getCommandType());
        String listResponse = bobby.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] first task"));
        assertTrue(listResponse.contains("2.[T][ ][P: HIGH] second task"));

        String clearResponse = bobby.getResponse("priority 2 none");

        assertTrue(clearResponse.contains("[T][ ] second task"));
    }

    /** Verifies that invalid priority input does not change the task. */
    @Test
    public void getResponse_invalidPriority_errorReturnedAndTaskUnchanged() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("invalid-priority.txt").toString());
        bobby.getResponse("todo read book");

        String response = bobby.getResponse("priority 1 urgent");

        assertEquals("Error! The priority level must be high, medium, low, none, 1, 2, or 3."
                + System.lineSeparator() + System.lineSeparator()
                + "Format: priority TASK_NUMBER LEVEL" + System.lineSeparator()
                + "Example: priority 1 high" + System.lineSeparator()
                + "Levels: high, medium, low, none, 1, 2, or 3" + System.lineSeparator(), response);
        assertNull(bobby.getCommandType());
        assertTrue(bobby.wasLastResponseError());

        assertTrue(bobby.getResponse("list").contains("1.[T][ ] read book"));
        assertFalse(bobby.wasLastResponseError());
    }

    /** Verifies that unknown GUI commands return a concise list of valid commands. */
    @Test
    public void getResponse_unknownCommand_availableCommandsReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("unknown-command.txt").toString());

        String response = bobby.getResponse("blah");

        assertTrue(response.contains("Available commands: todo, deadline, event, list, mark, unmark,"));
        assertTrue(response.contains("delete, find, priority, bye"));
        assertTrue(bobby.wasLastResponseError());
    }

    /** Verifies that invalid deadlines return concrete date and date-time examples. */
    @Test
    public void getResponse_invalidDeadline_concreteExamplesReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("invalid-deadline.txt").toString());

        String response = bobby.getResponse("deadline return book /by");

        assertTrue(response.contains("Example: deadline return book /by 2026-09-20"));
        assertTrue(response.contains("With time: deadline call client /by 2026-09-20 1800"));
        assertTrue(bobby.wasLastResponseError());
    }

    /** Verifies every recognized invalid command receives command-specific usage guidance. */
    @Test
    public void getResponse_invalidRecognizedCommands_relevantGuidanceReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("guidance.txt").toString());
        Map<String, String> invalidInputsAndGuidance = new LinkedHashMap<>();
        invalidInputsAndGuidance.put("todo", "Format: todo DESCRIPTION");
        invalidInputsAndGuidance.put("deadline", "Format: deadline DESCRIPTION /by DATE");
        invalidInputsAndGuidance.put("event", "Format: event DESCRIPTION /from DATE /to DATE");
        invalidInputsAndGuidance.put("mark", "Format: mark TASK_NUMBER");
        invalidInputsAndGuidance.put("unmark", "Format: unmark TASK_NUMBER");
        invalidInputsAndGuidance.put("delete", "Format: delete TASK_NUMBER");
        invalidInputsAndGuidance.put("find", "Format: find KEYWORD");
        invalidInputsAndGuidance.put("priority", "Format: priority TASK_NUMBER LEVEL");
        invalidInputsAndGuidance.put("list unexpected", "Format: list");
        invalidInputsAndGuidance.put("bye unexpected", "Format: bye");

        for (Map.Entry<String, String> inputAndGuidance : invalidInputsAndGuidance.entrySet()) {
            String response = bobby.getResponse(inputAndGuidance.getKey());

            assertTrue(response.contains(inputAndGuidance.getValue()), inputAndGuidance.getKey());
            assertTrue(bobby.wasLastResponseError(), inputAndGuidance.getKey());
            assertNull(bobby.getCommandType(), inputAndGuidance.getKey());
        }
    }

    /** Verifies null and blank input return general guidance without throwing an exception. */
    @Test
    public void getResponse_nullAndBlankInput_availableCommandsReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("empty-input.txt").toString());

        String nullResponse = bobby.getResponse(null);
        String blankResponse = bobby.getResponse("   ");

        assertTrue(nullResponse.contains("Available commands:"));
        assertTrue(blankResponse.contains("Available commands:"));
        assertTrue(bobby.wasLastResponseError());
    }

    /** Verifies tasks loaded during construction are available to later commands. */
    @Test
    public void getResponse_existingStorage_loadedTaskListed() throws Exception {
        Path taskFile = temporaryDirectory.resolve("existing.txt");
        Files.writeString(taskFile, "T | 1 | saved task | HIGH", StandardCharsets.UTF_8);
        Bobby bobby = new Bobby(taskFile.toString());

        String response = bobby.getResponse("list");

        assertTrue(response.contains("1.[T][X][P: HIGH] saved task"));
        assertEquals("ListCommand", bobby.getCommandType());
        assertFalse(bobby.wasLastResponseError());
    }

    /** Verifies failed startup loading also protects the malformed file from later writes. */
    @Test
    public void getResponse_storageLoadFailed_additionRejectedAndFilePreserved() throws Exception {
        Path taskFile = temporaryDirectory.resolve("protected.txt");
        String malformedData = "invalid";
        Files.writeString(taskFile, malformedData, StandardCharsets.UTF_8);
        Bobby bobby = new Bobby(taskFile.toString());

        String response = bobby.getResponse("todo replacement");

        assertTrue(response.contains("Bobby will not overwrite task data that failed to load"));
        assertTrue(response.contains("Format: todo DESCRIPTION"));
        assertTrue(bobby.wasLastResponseError());
        assertEquals(malformedData, Files.readString(taskFile, StandardCharsets.UTF_8));
    }

    /** Verifies that equivalent tasks are rejected after whitespace and case normalization. */
    @Test
    public void getResponse_duplicateTask_errorReturnedAndOriginalPreserved() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("duplicates.txt").toString());
        bobby.getResponse("todo Read   Book");

        String response = bobby.getResponse("  todo read book  ");

        assertTrue(response.contains("Error! This task already exists in the list."));
        assertTrue(bobby.wasLastResponseError());
        String listResponse = bobby.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] Read Book"));
        assertFalse(listResponse.contains("2.[T]"));
    }

    /** Verifies that a GUI instance reports a malformed data file at startup. */
    @Test
    public void getWelcomeMessage_invalidStorage_warningIncluded() throws Exception {
        Path taskFile = temporaryDirectory.resolve("malformed.txt");
        java.nio.file.Files.writeString(taskFile, "not valid task data");

        Bobby bobby = new Bobby(taskFile.toString());

        assertTrue(bobby.getWelcomeMessage().contains(
                "Error! Could not load tasks because line 1 contains invalid data."));
    }
}
