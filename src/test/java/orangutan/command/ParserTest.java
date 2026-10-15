package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orangutan.Storage;
import orangutan.chatlist.ChatList;

public class ParserTest {
    @TempDir
    public Path temporaryFolder;

    private ChatList list;
    private Context context;
    private Parser parser;

    @BeforeEach
    public void setUp() {
        list = new ChatList();
        context = new Context(list, true, temporaryFolder.resolve("orangutan.txt"), new Storage());
        parser = new Parser(context);
    }

    @Test
    public void runCommand_todoWithMultiwordTitle_addsTodo() {
        String result = parser.runCommand("todo read a book");

        assertEquals("Hark! A task hath been added to your list:\n [T] [ ] read a book", result);
        assertEquals("[T],false,read a book", list.toStream().findFirst().orElseThrow().toFile());
    }

    @Test
    public void runCommand_mixedCaseCommand_acceptsCommandCaseInsensitively() {
        parser.runCommand("ToDo read book");

        assertEquals(1, list.getLength());
    }

    @Test
    public void runCommand_deadlineWithByParameter_addsDeadline() {
        String result = parser.runCommand("deadline submit report /by 20260901 1700");

        assertEquals("Hark! A deadline hath been added to your list:\n"
                + " [D] [ ] submit report (by 2026-09-01 17:00)", result);
    }

    @Test
    public void runCommand_eventWithFromAndToParameters_addsEvent() {
        String result = parser.runCommand("event lecture /from 20260901 0900 /to 20260901 1100");

        assertEquals("Hark! An event hath been added to your list:\n"
                + " [E] [ ] lecture (from 2026-09-01 09:00 to 2026-09-01 11:00)", result);
    }

    @Test
    public void runCommand_indexCommands_updateTargetItem() {
        parser.runCommand("todo read book");

        assertEquals("My compliments, thou hast completed a task:\n [T] [X] read book",
                parser.runCommand("mark 1"));
        assertEquals("Take heed, this task hath not yet been completed:\n [T] [ ] read book",
                parser.runCommand("unmark 1"));
        assertEquals("The following task hath been purged from our records:\n [T] [ ] read book",
                parser.runCommand("delete 1"));
        assertEquals(0, list.getLength());
    }

    @Test
    public void runCommand_findWithMultiwordQuery_returnsMatch() {
        parser.runCommand("todo read a book");

        assertEquals("herein lie the items thou seekest:\n1. [T] [ ] read a book",
                parser.runCommand("find a book"));
    }

    @Test
    public void runCommand_sortWithMethod_sortsList() {
        parser.runCommand("todo zebra");
        parser.runCommand("todo alpha");

        parser.runCommand("sort completion");

        assertEquals("1. [T] [ ] alpha\n2. [T] [ ] zebra", list.toString());
        assertEquals(SortMethod.COMPLETION, list.getSortMethod());
    }

    @Test
    public void runCommand_missingTitle_returnsErrorMessage() {
        assertEquals("Alas! The todo hath been given no name.\n\nPray provide the todo name.",
                parser.runCommand("todo"));
    }

    @Test
    public void runCommand_missingDeadlineParameter_returnsErrorMessage() {
        assertEquals("Alas! The deadline's details remain incomplete.\n\n"
                + "Pray include /by, along with the due date and time.", parser.runCommand("deadline submit report"));
    }

    @Test
    public void runCommand_missingEventParameter_returnsErrorMessage() {
        assertEquals("Alas! The event's details remain incomplete.\n\n"
                        + "pray include /from and /to in your message, along with the start and end dates and times.",
                parser.runCommand("event lecture /from 20260901 0900"));
    }

    @Test
    public void runCommand_invalidDate_returnsErrorWithoutAddingItem() {
        String result = parser.runCommand("deadline submit report /by tomorrow");

        assertTrue(result.startsWith("Alas! I do not comprehend the dates and times"));
        assertEquals(0, list.getLength());
    }

    @Test
    public void runCommand_missingIndex_returnsErrorMessage() {
        String result = parser.runCommand("mark");

        assertEquals("Alas! I know not which item to mark.\n\n"
                + "Pray follow the mark command with an integer from 1 to 0, inclusive.", result);
    }

    @Test
    public void runCommand_missingFindKeyword_returnsErrorMessage() {
        assertEquals("Alas! I know not what to find.\n\nPray include the words you wish to find in the list.",
                parser.runCommand("find"));
    }

    @Test
    public void runCommand_missingSortMethod_returnsErrorMessage() {
        assertTrue(parser.runCommand("sort").startsWith("Alas! Thou hast not chosen a sorting method."));
    }

    @Test
    public void runCommand_unknownCommand_returnsErrorMessage() {
        assertTrue(parser.runCommand("dance").startsWith(
                "Alas! My simian mind is unable to comprehend your instructions."));
    }

    @Test
    public void runCommand_initWhenAlreadyRunning_returnsUnknownCommandMessage() {
        assertTrue(parser.runCommand("init").startsWith(
                "Alas! My simian mind is unable to comprehend your instructions."));
    }

    @Test
    public void runCommand_bye_savesListAndStopsContext() throws IOException {
        parser.runCommand("todo read book");

        String result = parser.runCommand("bye");

        assertEquals("Fare thee well, and may we meet again.", result);
        assertFalse(context.isRun());
        assertEquals("NONE\n[T],false,read book", Files.readString(context.getFilePath()));
    }
}
