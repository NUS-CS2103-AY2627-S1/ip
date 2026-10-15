package nexus;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the shared command execution used by both Nexus user interfaces. */
class NexusTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void executeCommand_addThenList_returnsUpdatedTaskOutput() {
        Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt").toString());

        String addResponse = nexus.executeCommand("todo read JavaFX tutorial");
        String listResponse = nexus.executeCommand("list");

        assertTrue(addResponse.contains("I’ve added this waypoint"));
        assertTrue(listResponse.contains("read JavaFX tutorial"));
    }

    @Test
    void executeCommand_snoozeTask_hidesItFromList() {
        Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt").toString());

        nexus.executeCommand("todo prepare presentation");
        String snoozeResponse = nexus.executeCommand("snooze 1 /until 2099-01-01");
        String listResponse = nexus.executeCommand("list");
        String findResponse = nexus.executeCommand("find presentation");

        assertTrue(snoozeResponse.contains("Waypoint placed in orbit until 2099-01-01"));
        assertTrue(!listResponse.contains("prepare presentation"));
        assertTrue(findResponse.contains("prepare presentation"));
    }

    @Test
    void executeCommand_invalidAndMutatingCommands_returnHelpfulResponses() {
        Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt").toString());

        assertTrue(nexus.executeCommand(null).startsWith("OOPS!!! Please enter"));
        assertTrue(nexus.executeCommand("  list").contains("leading"));
        assertTrue(nexus.executeCommand("unknown").contains("don't know"));
        assertTrue(nexus.executeCommand("delete nope").contains("valid waypoint"));
        assertTrue(nexus.executeCommand("todo keep").contains("Mission logged"));
        assertTrue(nexus.executeCommand("mark 2").contains("no task"));
        assertTrue(nexus.executeCommand("mark 1").contains("Waypoint secured"));
        assertTrue(nexus.executeCommand("unmark 1").contains("back on the active route"));
        assertTrue(nexus.executeCommand("delete 1").contains("Waypoint cleared"));
    }

    @Test
    void executeCommand_snoozeValidation_rejectsMalformedDatesAndCommands() {
        Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt").toString());

        assertTrue(nexus.executeCommand("snooze 1").contains("Nexus protocol"));
        assertTrue(nexus.executeCommand("snooze nope /until 2099-01-01").contains("valid waypoint"));
        nexus.executeCommand("todo task");
        assertTrue(nexus.executeCommand("snooze 1 /until 2020-01-01").contains("future"));
        assertTrue(nexus.executeCommand("snooze 1 /until tomorrow").contains("yyyy-MM-dd"));
    }
}
