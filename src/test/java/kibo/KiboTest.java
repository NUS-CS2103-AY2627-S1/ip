package kibo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Test;

import kibo.support.StorageTestSupport;

/**
 * Tests command dispatch, persistence, rollback, and console lifecycle with isolated task files.
 */
public class KiboTest extends StorageTestSupport {
    @Test
    void getCommandResponse_addAllTypes_savesAndReloadsTasks() throws Exception {
        Kibo kibo = new Kibo();
        assertFalse(kibo.getCommandResponse("todo read book").isError());
        assertFalse(kibo.getCommandResponse("deadline submit report /by 2024-02-29").isError());
        assertFalse(kibo.getCommandResponse("event meeting /from 2024-02-29 2pm /to 4pm").isError());
        assertEquals(List.of("T | 0 | read book", "D | 0 | submit report | 2024-02-29",
                "E | 0 | meeting | 2024-02-29 2pm | 4pm"), Files.readAllLines(SAVE_PATH));
        assertEquals(kibo.getResponse("list"), new Kibo().getResponse("list"));
    }

    @Test
    void getCommandResponse_markUnmarkAndDelete_updatesStoredList() throws Exception {
        writeFixture("T | 0 | first\nT | 0 | second\n");
        Kibo kibo = new Kibo();
        assertFalse(kibo.getCommandResponse("mark 2").isError());
        assertEquals("T | 1 | second", Files.readAllLines(SAVE_PATH).get(1));
        assertFalse(kibo.getCommandResponse("unmark 2").isError());
        assertEquals("T | 0 | second", Files.readAllLines(SAVE_PATH).get(1));
        assertTrue(kibo.getResponse("delete 1").contains("[T][ ] first"));
        assertEquals(List.of("T | 0 | second"), Files.readAllLines(SAVE_PATH));
        assertEquals("Here's your lineup. One step at a time!\n1.[T][ ] second", kibo.getResponse("list"));
    }

    @Test
    void getCommandResponse_readOnlyQueries_returnsMatchesWithoutSaving() throws Exception {
        String fixture = "T | 0 | read book\nD | 1 | return book | 2024-02-29\n"
                + "E | 0 | meeting | 2024-02-29 2pm | 4pm\n";
        writeFixture(fixture);
        Kibo kibo = new Kibo();
        blockSaving();

        assertEquals("Let's see what matches your search:\n1.[T][ ] read book\n"
                + "2.[D][X] return book (by: Feb 29 2024)", kibo.getResponse("find BOOK"));
        assertEquals("Let's plan your day! Schedule for Feb 29 2024:\n"
                + "1.[D][X] return book (by: Feb 29 2024)\n"
                + "2.[E][ ] meeting (from: 2024-02-29 2pm to: 4pm)", kibo.getResponse("schedule 2024-02-29"));
        assertFalse(kibo.getCommandResponse("list").isError());
        assertEquals(fixture, Files.readString(SAVE_PATH));
    }

    @Test
    void getCommandResponse_invalidCommands_reportsErrorsWithoutChangingTasks() throws Exception {
        writeFixture("T | 0 | keep me\n");
        Kibo kibo = new Kibo();
        String original = kibo.getResponse("list");
        String[] inputs = {"", "blah", "todo", "deadline", "event", "find", "schedule tomorrow",
            "mark one", "unmark 0", "delete 2", "list extra", "bye later", "todo a | b"};
        for (String input : inputs) {
            CommandResponse response = kibo.getCommandResponse(input);
            assertTrue(response.isError(), input);
            assertFalse(response.getMessage().isBlank(), input);
            assertEquals(original, kibo.getResponse("list"));
            assertEquals("T | 0 | keep me\n", Files.readString(SAVE_PATH));
            assertFalse(kibo.isExitRequested());
        }
    }

    @Test
    void getCommandResponse_saveFailure_restoresEveryMutationAndOriginalStatus() throws Exception {
        String[] commands = {"todo new task", "mark 1", "unmark 1", "delete 1", "delete 2", "delete 3"};
        for (String status : List.of("0", "1")) {
            for (String command : commands) {
                Files.deleteIfExists(TEMPORARY_PATH);
                String fixture = "T | " + status + " | first\nT | 0 | second\nT | 0 | third\n";
                writeFixture(fixture);
                Kibo kibo = new Kibo();
                String original = kibo.getResponse("list");
                blockSaving();

                assertTrue(kibo.getCommandResponse(command).isError(), command + " with status " + status);
                assertEquals(original, kibo.getResponse("list"));
                assertEquals(fixture, Files.readString(SAVE_PATH));
            }
        }
    }

    @Test
    void getCommandResponse_errorThenBye_returnsIndependentOutcomes() {
        Kibo kibo = new Kibo();
        CommandResponse error = kibo.getCommandResponse("blah");
        CommandResponse goodbye = kibo.getCommandResponse("bye");
        assertTrue(error.isError());
        assertFalse(goodbye.isError());
        assertEquals("Catch you soon! Keep taking those little steps.", goodbye.getMessage());
        assertTrue(kibo.isExitRequested());
        assertEquals("", kibo.getLoadingErrorMessage());
        assertTrue(kibo.getWelcomeMessage().contains("Kibo"));
    }

    @Test
    void main_errorThenValidCommandAndBye_continuesUntilBye() throws Exception {
        String output = runConsole("  todo  \n  todo read book  \nbye\ntodo ignored\n");
        assertTrue(output.contains("The description of a todo cannot be empty."));
        assertTrue(output.contains("On the list! Let's make it happen:"));
        assertTrue(output.contains("Catch you soon! Keep taking those little steps."));
        assertFalse(output.contains("ignored"));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(SAVE_PATH));
    }

    @Test
    void main_endOfInput_exitsWithoutInventingGoodbye() throws Exception {
        String output = runConsole("list\n");
        assertTrue(output.contains("Hey! I'm Kibo"));
        assertTrue(output.contains("Here's your lineup."));
        assertFalse(output.contains("Catch you soon!"));
        assertFalse(Files.exists(SAVE_PATH));
    }

    @Test
    void main_malformedStorage_stopsBeforeAcceptingCommands() throws Exception {
        writeFixture("T | 2 | broken\n");
        assertEquals("The saved task on line 1 has an invalid format.", new Kibo().getLoadingErrorMessage());
        String output = runConsole("todo should not be added\nbye\n");
        assertTrue(output.contains("The saved task on line 1 has an invalid format."));
        assertFalse(output.contains("On the list!"));
        assertEquals("T | 2 | broken\n", Files.readString(SAVE_PATH));
    }

    private String runConsole(String input) throws Exception {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            Kibo.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return captured.toString(StandardCharsets.UTF_8);
    }
}
