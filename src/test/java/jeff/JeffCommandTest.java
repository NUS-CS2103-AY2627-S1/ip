package jeff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JeffCommandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void taskCommands_completeWorkflow_updatesAndPersistsTasks() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Jeff jeff = new Jeff(filePath.toString());

        assertTrue(
                jeff.getResponse("todo read book")
                        .contains("I've added this task:"));
        String deadlineResponse = jeff.getResponse(
                "deadline submit report /by 2026-09-30");
        assertTrue(deadlineResponse.contains("I've added this task:"));
        assertTrue(deadlineResponse.contains("submit report"));
        assertTrue(
                jeff.getResponse("event meeting /from 1400 /to 1600")
                        .contains("from: 1400 to: 1600"));

        String listResponse = jeff.getResponse("list");
        assertTrue(listResponse.contains("1.[T]"));
        assertTrue(listResponse.contains("2.[D]"));
        assertTrue(listResponse.contains("3.[E]"));

        assertTrue(
                jeff.getResponse("mark 2")
                        .contains("marked this task as done"));
        assertTrue(jeff.getResponse("list").contains("[D][X]"));

        assertTrue(
                jeff.getResponse("unmark 2")
                        .contains("marked this task as undone"));
        assertTrue(jeff.getResponse("list").contains("[D][ ]"));

        String findResponse = jeff.getResponse("find REPORT");
        assertTrue(findResponse.contains("submit report"));
        assertFalse(findResponse.contains("read book"));

        assertTrue(
                jeff.getResponse("delete 1")
                        .contains("I've removed this task"));
        assertFalse(jeff.getResponse("list").contains("read book"));

        Jeff reloadedJeff = new Jeff(filePath.toString());
        assertEquals(
                jeff.getResponse("list"),
                reloadedJeff.getResponse("list"));
    }

    @Test
    public void invalidTaskNumbers_eachNumberedCommand_reportsError() {
        Jeff jeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());
        jeff.getResponse("todo read book");

        assertTrue(
                jeff.getResponse("mark 0")
                        .contains("task number does not exist"));
        assertTrue(
                jeff.getResponse("unmark 2")
                        .contains("task number does not exist"));
        assertTrue(
                jeff.getResponse("delete -1")
                        .contains("task number does not exist"));
        assertTrue(
                jeff.getResponse("update 99 /description changed")
                        .contains("task number does not exist"));
    }

    @Test
    public void invalidCommands_doNotChangeTaskList() {
        Jeff jeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());
        jeff.getResponse("todo read book");
        String originalList = jeff.getResponse("list");

        assertEquals(
                "Invalid command." + System.lineSeparator(),
                jeff.getResponse("unknown command"));
        assertEquals(
                "Please enter a task number." + System.lineSeparator(),
                jeff.getResponse("delete"));
        assertEquals(
                "Please enter a valid task number." + System.lineSeparator(),
                jeff.getResponse("mark abc"));
        assertEquals(originalList, jeff.getResponse("list"));
    }

    @Test
    public void byeCommand_returnsGoodbyeMessage() {
        Jeff jeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "Bye. Hope to see you again soon!"
                        + System.lineSeparator(),
                jeff.getResponse("bye"));
    }
}
