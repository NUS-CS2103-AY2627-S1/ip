package fein;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Fein's command-processing responses used by the graphical interface. */
class FeinTest {
    /** Verifies that GUI responses use the same commands and task operations as the CLI. */
    @Test
    void getResponseProcessesExistingCliCommands(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertTrue(fein.getResponse("todo read book").contains("read book"));
        assertEquals("add", fein.getCommandType());
        assertTrue(fein.getResponse("list").contains("[T][ ] read book"));
        assertEquals("list", fein.getCommandType());
        assertTrue(fein.getResponse("mark 1").contains("[T][X] read book"));
        assertEquals("mark", fein.getCommandType());
        assertTrue(fein.getResponse("find BOOK").contains("read book"));
        assertEquals("find", fein.getCommandType());
        assertTrue(fein.getResponse("delete 1").contains("read book"));
        assertEquals("delete", fein.getCommandType());
    }

    /** Verifies that invalid commands return the existing CLI error and error style type. */
    @Test
    void getResponseReportsInvalidCommands(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertEquals("That command is not feining. Try `todo buy milk`, `list`, or `bye`.",
                fein.getResponse("unknown"));
        assertEquals("error", fein.getCommandType());
    }

    /** Verifies that empty input provides an in-app starting point. */
    @Test
    void getResponseExplainsEmptyInput(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertEquals("Tell me what you would like to remember. Try `todo buy milk` or `bye`.",
                fein.getResponse(" "));
        assertEquals("error", fein.getCommandType());
    }

    /** Verifies that an empty list guides the user towards the todo command. */
    @Test
    void getResponseExplainsHowToAddToAnEmptyList(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertEquals("Your list is empty! Try adding a task with `todo <description>`.\nExample: todo buy milk",
                fein.getResponse("list"));
        assertEquals("list", fein.getCommandType());
    }

    /** Verifies that repeated spaces between command words are rejected. */
    @Test
    void getResponseRejectsRepeatedWhitespace(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertEquals("Please use a single space between words, for example: `todo buy milk`.",
                fein.getResponse("todo buy    milk"));
        assertEquals("error", fein.getCommandType());
    }

    /** Verifies that help explains every available core command and its intended use. */
    @Test
    void getResponseExplainsAllCoreCommands(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        String helpResponse = fein.getResponse("help");

        assertTrue(helpResponse.contains("FEIN command list:"));
        assertTrue(helpResponse.contains("todo <description>"));
        assertTrue(helpResponse.contains("deadline <description> /by <due date>"));
        assertTrue(helpResponse.contains("event <description> /from <start> /to <end>"));
        assertTrue(helpResponse.contains("find <keyword>"));
        assertTrue(helpResponse.contains("mark <task number>"));
        assertTrue(helpResponse.contains("unmark <task number>"));
        assertTrue(helpResponse.contains("delete <task number>"));
        assertTrue(helpResponse.contains("Quick commands:\nlist"));
        assertTrue(helpResponse.contains("\nhelp\nPurpose: Show this command guide."));
        assertTrue(helpResponse.contains("\nbye\nPurpose: Exit Fein."));
        assertEquals("help", fein.getCommandType());
    }

    /** Verifies that the graphical command path reports duplicate tasks without adding them. */
    @Test
    void getResponseRejectsDuplicateTasks(@TempDir Path temporaryDirectory) {
        Fein fein = new Fein(temporaryDirectory.resolve("fein.txt").toString());

        assertTrue(fein.getResponse("todo buy milk").contains("buy milk"));
        assertEquals("That task is already on your list. Try `list` to check it.",
                fein.getResponse("todo Buy Milk"));
        assertEquals("error", fein.getCommandType());
    }
}
