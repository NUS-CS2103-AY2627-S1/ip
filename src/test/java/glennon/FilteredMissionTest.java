package glennon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that filtered mission numbers safely identify the same tasks during later mutations.
 */
class FilteredMissionTest {
    private static final String FIND_COMMAND = "find matched";
    private static final String ON_COMMAND = "on 17/9/2026";
    private static final String DEADLINE = "[D][ ] matched deadline (by: Sep 17 2026, 6:00 PM)";
    private static final String COMPLETED_DEADLINE = "[D][X] matched deadline (by: Sep 17 2026, 6:00 PM)";
    private static final String OTHER_DEADLINE = "[D][ ] preserve earlier (by: Sep 16 2026, 9:00 AM)";
    private static final String EVENT =
            "[E][ ] matched event (from: Sep 17 2026, 9:00 AM to: Sep 18 2026, 10:00 AM)";
    private static final String COMPLETED_EVENT =
            "[E][X] matched event (from: Sep 17 2026, 9:00 AM to: Sep 18 2026, 10:00 AM)";
    private static final String INITIAL_LOG = "Mission log:\n1. [T][ ] preserve first\n2. " + DEADLINE
            + "\n3. " + OTHER_DEADLINE + "\n4. " + COMPLETED_EVENT;

    @TempDir
    private Path directory;

    @Test
    void getResponse_findThenMark_changesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(FIND_COMMAND, "mark 2");
    }

    @Test
    void getResponse_findThenUnmark_changesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(FIND_COMMAND, "unmark 4");
    }

    @Test
    void getResponse_findThenDelete_removesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(FIND_COMMAND, "delete 2");
    }

    @Test
    void getResponse_onThenMark_changesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(ON_COMMAND, "mark 2");
    }

    @Test
    void getResponse_onThenUnmark_changesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(ON_COMMAND, "unmark 4");
    }

    @Test
    void getResponse_onThenDelete_removesDisplayedMissionAndPersistsIt() {
        assertFilteredMutation(ON_COMMAND, "delete 2");
    }

    @Test
    void getResponse_filterAfterSortAndDeletion_usesCurrentFullLogPositions() {
        Glennon glennon = createSession(directory.resolve("missions.txt"));
        for (String filter : new String[] {FIND_COMMAND, ON_COMMAND}) {
            assertEquals(initialFilterResponse(filter), glennon.getResponse(filter));
        }

        assertFalse(glennon.getCommandResponse("sort").isError());
        for (String filter : new String[] {FIND_COMMAND, ON_COMMAND}) {
            assertEquals(filterResponse(filter, "2", COMPLETED_EVENT, "3", DEADLINE),
                    glennon.getResponse(filter));
        }

        assertFalse(glennon.getCommandResponse("delete 1").isError());
        for (String filter : new String[] {FIND_COMMAND, ON_COMMAND}) {
            assertEquals(filterResponse(filter, "1", COMPLETED_EVENT, "2", DEADLINE),
                    glennon.getResponse(filter));
        }
        assertEquals("Mission marked complete:\n  " + COMPLETED_DEADLINE, glennon.getResponse("mark 2"));
        assertEquals("Mission log:\n1. " + COMPLETED_EVENT + "\n2. " + COMPLETED_DEADLINE
                + "\n3. [T][ ] preserve first", glennon.getResponse("list"));
    }

    @Test
    void getResponse_invalidNumbersAfterFiltering_preservesMemoryAndSavedFile() throws IOException {
        Path data = directory.resolve("missions.txt");
        Glennon glennon = createSession(data);
        String saved = Files.readString(data);
        for (String filter : new String[] {FIND_COMMAND, ON_COMMAND}) {
            for (String operation : new String[] {"mark", "unmark", "delete"}) {
                for (String number : new String[] {"0", "-1", "5", "2147483648", "oops"}) {
                    assertEquals(initialFilterResponse(filter), glennon.getResponse(filter));
                    String command = operation + " " + number;
                    assertTrue(glennon.getCommandResponse(command).isError(), command);
                    assertEquals(INITIAL_LOG, glennon.getResponse("list"), command);
                    assertEquals(saved, Files.readString(data), command);
                }
            }
        }
    }

    @Test
    void getResponse_emptyFilterAfterMatches_doesNotRemapLaterCommands() {
        Glennon glennon = createSession(directory.resolve("missions.txt"));
        assertEquals(initialFilterResponse(FIND_COMMAND), glennon.getResponse(FIND_COMMAND));
        assertEquals("Matching missions located:", glennon.getResponse("find missing"));
        assertEquals("Missions on Sep 20 2026:", glennon.getResponse("on 20/9/2026"));
        assertEquals(INITIAL_LOG, glennon.getResponse("list"));
        assertEquals("Mission marked complete:\n  " + COMPLETED_DEADLINE, glennon.getResponse("mark 2"));
        assertEquals(expectedLogAfter("mark 2"), glennon.getResponse("list"));
    }

    @Test
    void getResponse_failedFilteredMutationThenRetry_preservesSelectionAndSavedState() throws IOException {
        for (String filter : new String[] {FIND_COMMAND, ON_COMMAND}) {
            for (String command : new String[] {"mark 2", "unmark 4", "delete 2"}) {
                Path data = directory.resolve(filter.startsWith("find") ? "find-" + command : "on-" + command);
                Path backup = directory.resolve(data.getFileName() + ".backup");
                Glennon glennon = createSession(data);
                String saved = Files.readString(data);
                assertEquals(initialFilterResponse(filter), glennon.getResponse(filter));
                Files.move(data, backup);
                Files.createDirectory(data);

                assertTrue(glennon.getCommandResponse(command).isError(), filter + ": " + command);
                assertEquals(INITIAL_LOG, glennon.getResponse("list"));
                assertEquals(initialFilterResponse(filter), glennon.getResponse(filter));
                assertEquals(saved, Files.readString(backup));

                Files.delete(data);
                Files.move(backup, data);
                assertFalse(glennon.getCommandResponse(command).isError());
                assertEquals(expectedLogAfter(command), glennon.getResponse("list"));
                assertEquals(expectedLogAfter(command), new Glennon(data.toString()).getResponse("list"));
            }
        }
    }

    /**
     * Verifies both the mutation response and the complete persisted log after selecting a displayed number.
     */
    private void assertFilteredMutation(String filter, String command) {
        Path data = directory.resolve("missions.txt");
        Glennon glennon = createSession(data);
        assertEquals(initialFilterResponse(filter), glennon.getResponse(filter));

        String expectedResponse = switch (command) {
            case "mark 2" -> "Mission marked complete:\n  " + COMPLETED_DEADLINE;
            case "unmark 4" -> "Mission marked incomplete:\n  " + EVENT;
            case "delete 2" -> "Mission removed:\n  " + DEADLINE + "\nMission log now has 3 missions.";
            default -> throw new AssertionError("Unexpected test command: " + command);
        };
        CommandResponse response = glennon.getCommandResponse(command);
        assertFalse(response.isError());
        assertEquals(expectedResponse, response.text());
        assertEquals(expectedLogAfter(command), glennon.getResponse("list"));

        Glennon restored = new Glennon(data.toString());
        assertEquals(expectedLogAfter(command), restored.getResponse("list"));
        if (command.equals("delete 2")) {
            String expected = filter.equals(FIND_COMMAND)
                    ? "Matching missions located:\n3. " + COMPLETED_EVENT
                    : "Missions on Sep 17 2026:\n3. " + COMPLETED_EVENT;
            assertEquals(expected, restored.getResponse(filter));
        }
    }

    /**
     * Creates nonadjacent filter matches so subset numbering cannot accidentally select the right mission.
     */
    private Glennon createSession(Path data) {
        Glennon glennon = new Glennon(data.toString());
        for (String command : new String[] {
            "todo preserve first",
            "deadline matched deadline /by 17/9/2026 1800",
            "deadline preserve earlier /by 16/9/2026 0900",
            "event matched event /from 17/9/2026 0900 /to 18/9/2026 1000",
            "mark 4"
        }) {
            assertFalse(glennon.getCommandResponse(command).isError(), command);
        }
        assertEquals(INITIAL_LOG, glennon.getResponse("list"));
        return glennon;
    }

    /**
     * Returns the expected complete log, including the missions that filtering hides.
     */
    private String expectedLogAfter(String command) {
        return switch (command) {
            case "mark 2" -> "Mission log:\n1. [T][ ] preserve first\n2. " + COMPLETED_DEADLINE
                    + "\n3. " + OTHER_DEADLINE + "\n4. " + COMPLETED_EVENT;
            case "unmark 4" -> "Mission log:\n1. [T][ ] preserve first\n2. " + DEADLINE
                    + "\n3. " + OTHER_DEADLINE + "\n4. " + EVENT;
            case "delete 2" -> "Mission log:\n1. [T][ ] preserve first\n2. " + OTHER_DEADLINE
                    + "\n3. " + COMPLETED_EVENT;
            default -> throw new AssertionError("Unexpected test command: " + command);
        };
    }

    /**
     * Returns the exact initial filtered response while preserving the full log's second and fourth positions.
     */
    private String initialFilterResponse(String filter) {
        return filterResponse(filter, "2", DEADLINE, "4", COMPLETED_EVENT);
    }

    /**
     * Formats two explicitly numbered expectations with consistent spacing for both filters.
     */
    private String filterResponse(String filter, String firstNumber, String firstTask,
            String secondNumber, String secondTask) {
        String heading = filter.equals(FIND_COMMAND) ? "Matching missions located:" : "Missions on Sep 17 2026:";
        return heading + "\n" + firstNumber + ". " + firstTask + "\n" + secondNumber + ". " + secondTask;
    }
}
