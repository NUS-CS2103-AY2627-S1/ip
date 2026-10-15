package erina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Erina}'s replies, driven through {@link Erina#getResponse}.
 *
 * <p>Each test gets its own temporary save file, so the tests neither read
 * nor overwrite the real task list.
 */
public class ErinaTest {
    @TempDir
    Path tempDir;

    private Path saveFile() {
        return tempDir.resolve("erina.txt");
    }

    private Erina newErina() {
        return new Erina(saveFile());
    }

    @Test
    public void getResponse_help_mentionsEveryCommand() {
        String help = newErina().getResponse("help");

        // The help text is generated from the enum, so every command's usage
        // line must appear, including help itself.
        for (Command command : Command.values()) {
            assertTrue(help.contains(command.getUsage()),
                    "help text is missing: " + command.getUsage());
        }
    }

    @Test
    public void getResponse_unknownCommand_pointsToHelp() {
        String reply = newErina().getResponse("blah");
        assertTrue(reply.contains("help"));
    }

    @Test
    public void getResponse_sameTodoTwice_addsItOnlyOnce() {
        Erina erina = newErina();
        erina.getResponse("todo read book");

        // Different capitalisation is still the same task.
        String reply = erina.getResponse("todo Read Book");

        assertTrue(reply.contains("already"), reply);
        assertFalse(erina.getResponse("list").contains("2."));
    }

    @Test
    public void getResponse_markTaskAlreadyDone_saysSo() {
        Erina erina = newErina();
        erina.getResponse("todo read book");
        erina.getResponse("mark 1");

        assertTrue(erina.getResponse("mark 1").contains("already"));
    }

    @Test
    public void getResponse_listWithExtraText_isRejected() {
        Erina erina = newErina();
        erina.getResponse("todo read book");

        assertFalse(erina.getResponse("list all").contains("read book"));
    }

    @Test
    public void getResponse_blankInput_returnsEmptyReply() {
        assertEquals("", newErina().getResponse("   "));
    }

    @Test
    public void getResponse_tasksAdded_areStillThereAfterRestart() {
        Erina first = newErina();
        first.getResponse("todo read book");
        first.getResponse("deadline return book /by 2026-09-18");

        // A new Erina on the same file stands in for reopening the app.
        String list = newErina().getResponse("list");

        assertTrue(list.contains("1.[T][ ] read book"), list);
        assertTrue(list.contains("2.[D][ ] return book (by: Sep 18 2026)"), list);
    }

    @Test
    public void getResponse_deleteTask_removesOnlyThatTask() {
        Erina erina = newErina();
        erina.getResponse("todo read book");
        erina.getResponse("todo buy milk");

        erina.getResponse("delete 1");
        String list = erina.getResponse("list");

        assertFalse(list.contains("read book"), list);
        assertTrue(list.contains("1.[T][ ] buy milk"), list);
    }

    @Test
    public void getResponse_unmarkDoneTask_reopensIt() {
        Erina erina = newErina();
        erina.getResponse("todo read book");
        erina.getResponse("mark 1");

        String reply = erina.getResponse("unmark 1");

        assertTrue(reply.contains("[T][ ] read book"), reply);
    }

    @Test
    public void getResponse_findKeyword_showsOnlyMatchingTasks() {
        Erina erina = newErina();
        erina.getResponse("todo read book");
        erina.getResponse("todo buy milk");

        String reply = erina.getResponse("find BOOK");

        assertTrue(reply.contains("read book"), reply);
        assertFalse(reply.contains("buy milk"), reply);
    }

    @Test
    public void getResponse_bye_asksToExit() {
        Erina erina = newErina();
        assertFalse(erina.isExit());

        erina.getResponse("bye");

        assertTrue(erina.isExit());
    }

    @Test
    public void getResponse_byeWithExtraText_doesNotExit() {
        Erina erina = newErina();

        erina.getResponse("bye now");

        assertFalse(erina.isExit());
        assertTrue(erina.isLastReplyError());
    }

    @Test
    public void isLastReplyError_badThenGoodCommand_flagsOnlyTheBadReply() {
        Erina erina = newErina();

        erina.getResponse("mark 1");
        assertTrue(erina.isLastReplyError());

        erina.getResponse("todo read book");
        assertFalse(erina.isLastReplyError());
    }

    @Test
    public void constructor_unreadableSaveFile_startsEmptyAndKeepsBackup() throws IOException {
        Files.writeString(saveFile(), "this is not a task\n");

        Erina erina = newErina();

        assertNotNull(erina.getLoadError());
        Path backup = tempDir.resolve("erina.txt.bak");
        assertEquals("this is not a task\n", Files.readString(backup));
        assertFalse(erina.getResponse("list").contains("1."));
    }
}
