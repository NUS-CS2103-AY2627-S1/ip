package zikiai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zikiai.storage.Storage;

/**
 * Tests the shared command-response boundary used by the GUI and console.
 */
class ZikiaiTest {
    @TempDir
    private Path directory;

    private Zikiai newSession() {
        return new Zikiai(new Storage(directory.resolve("tasks.txt")));
    }

    @Test
    void constructor_nullStorage_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Zikiai(null));
    }

    @Test
    void getResponse_allTaskTypes_savedAndLoaded() {
        Zikiai bot = newSession();
        assertTrue(bot.canAcceptCommands());
        assertEquals("Hello! I'm Zikiai, your task buddy.\nWhat do you need help with today ah?", bot.getWelcome());
        assertEquals("Can! I've added this task for you:\n    [T][ ] read book\n"
                + "You have 1 task in your list now.", bot.getResponse("todo read book"));
        assertTrue(bot.getResponse("deadline return book /by 2026-08-30")
                .contains("[D][ ] return book (by: Aug 30 2026)"));
        assertTrue(bot.getResponse("event meeting /from 2pm /to 4pm")
                .contains("[E][ ] meeting (from: 2pm to: 4pm)"));
        assertEquals(bot.getResponse("list"), newSession().getResponse("list"));
    }

    @Test
    void getResponse_statusAndDeletion_savedAcrossSessions() {
        Zikiai bot = newSession();
        bot.getResponse("todo first");
        bot.getResponse("todo second");
        assertEquals("Shiok! This task is done already:\n    [T][X] second", bot.getResponse("mark 2"));
        assertTrue(newSession().getResponse("list").contains("2.[T][X] second"));
        assertEquals("Okay can, this task is not done yet:\n    [T][ ] second", bot.getResponse("unmark 2"));
        assertEquals("Okay, removed this task already:\n    [T][ ] first\nYou have 1 task in your list now.",
                bot.getResponse("delete 1"));
        assertEquals("Here are your tasks:\n1.[T][ ] second", newSession().getResponse("list"));
    }

    @Test
    void getResponse_priorityChanges_savedAcrossSessions() {
        Zikiai bot = newSession();
        bot.getResponse("todo read book");

        assertEquals(
                "Steady! I've updated this task's priority:\n    [T][HIGH][ ] read book",
                bot.getResponse("priority 1 high"));
        assertEquals(
                "Here are your tasks:\n1.[T][HIGH][ ] read book",
                newSession().getResponse("list"));

        assertEquals(
                "Steady! I've updated this task's priority:\n    [T][ ] read book",
                bot.getResponse("priority 1 none"));
        assertEquals(
                "Here are your tasks:\n1.[T][ ] read book",
                newSession().getResponse("list"));
    }

    @Test
    void getResponse_invalidCommands_preserveStateAndRecover() {
        Zikiai bot = newSession();
        bot.getResponse("todo keep me");
        String original = bot.getResponse("list");
        String[] invalidInputs = {"todo", "deadline bad /by Sunday", "event bad", "mark 99",
            "unmark 0", "delete 99", "priority", "priority one high", "priority 1 urgent",
            "priority 99 high", "find", "unknown", ""};
        for (String input : invalidInputs) {
            assertTrue(bot.getResponse(input).startsWith("Aiyo! "), input);
            assertEquals(original, bot.getResponse("list"), input);
        }
        assertEquals("Aiyo, no matching tasks leh.", bot.getResponse("find missing"));
        assertEquals("Found these matching tasks for you:\n1.[T][ ] keep me", bot.getResponse("find keep"));
        assertEquals(original, newSession().getResponse("list"));
    }

    @Test
    void getResponse_harmlessWhitespace_commandsAcceptedAndSaved() {
        Zikiai bot = newSession();

        assertTrue(bot.getResponse("   todo     read book   ").contains("[T][ ] read book"));
        assertTrue(bot.getResponse("mark     1").contains("[T][X] read book"));
        assertEquals("Here are your tasks:\n1.[T][X] read book", bot.getResponse("   list   "));
        assertEquals(bot.getResponse("list"), newSession().getResponse("list"));
    }

    @Test
    void getResponse_malformedKnownCommands_actionableFormatsReturned() {
        Zikiai bot = newSession();

        assertEquals(
                "Aiyo! Use mark TASK_NUMBER, for example mark 1.",
                bot.getResponse("mark homework"));
        assertEquals(
                "Aiyo! Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                        + "deadline submit report /by 2026-09-30.",
                bot.getResponse("deadline submit report"));
        assertEquals(
                "Aiyo! Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                bot.getResponse("event meeting /to 4pm /from 2pm"));
        assertEquals("Here are your tasks:", bot.getResponse("list"));
    }

    @Test
    void getResponse_bye_blocksFurtherChanges() {
        Zikiai bot = newSession();
        assertEquals("Okay, bye bye! See you again lah.", bot.getResponse("bye"));
        assertFalse(bot.canAcceptCommands());
        assertEquals("Okay, bye bye! See you again lah.", bot.getResponse("todo should not be added"));
        assertEquals("Here are your tasks:", newSession().getResponse("list"));
    }

    @Test
    void constructor_corruptFile_blocksInputWithoutOverwriting() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "invalid saved data\n");
        Zikiai bot = newSession();
        assertFalse(bot.canAcceptCommands());
        assertEquals("Aiyo! I couldn't load the saved tasks because line 1 is invalid.", bot.getWelcome());
        assertEquals(bot.getWelcome(), bot.getResponse("todo do not overwrite"));
        assertEquals("invalid saved data\n", Files.readString(file));
    }

    @Test
    void getResponse_saveFailure_returnsErrorInsteadOfSuccess() throws IOException {
        Path parent = directory.resolve("blocked");
        Zikiai bot = new Zikiai(new Storage(parent.resolve("tasks.txt")));
        Files.writeString(parent, "not a directory");
        assertEquals("Aiyo! I couldn't save your tasks to the data file.", bot.getResponse("todo test"));
        assertTrue(bot.canAcceptCommands());
        assertEquals("not a directory", Files.readString(parent));
    }
}
