package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the methods the graphical window relies on.
 *
 * <p>Each test gets its own folder, so a test writes a real save file without
 * touching anyone's tasks.</p>
 *
 * <p>Test names read as {@code method_condition_expectedResult}.</p>
 */
public class KenbotTest {

    @TempDir
    private Path folder;

    private Kenbot kenbotIn(Path where) {
        return new Kenbot(where.resolve("data").resolve("tasks.txt").toString());
    }

    @Test
    public void getResponse_todo_confirmsTheTask() {
        String reply = kenbotIn(folder).getResponse("todo read book");
        assertTrue(reply.startsWith("Got it, that's on the list:"), reply);
        assertTrue(reply.contains("[T][ ] read book"), reply);
    }

    @Test
    public void getResponse_addedThenListed_showsTheTask() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("todo read book");
        assertTrue(kenbot.getResponse("list").contains("read book"));
    }

    /**
     * The window has no error stream, so a refused command has to come back as
     * an ordinary reply rather than as a thrown exception.
     */
    @Test
    public void getResponse_unknownCommand_returnsTheMessageInsteadOfThrowing() {
        assertEquals("Don't know that one.",
                kenbotIn(folder).getResponse("sing a song"));
    }

    @Test
    public void getResponse_blank_asksForACommand() {
        assertEquals("Type something and I'll sort it.", kenbotIn(folder).getResponse("   "));
    }

    @Test
    public void isExit_beforeAnyCommand_isFalse() {
        assertFalse(kenbotIn(folder).isExit());
    }

    @Test
    public void isExit_afterBye_isTrue() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("bye");
        assertTrue(kenbot.isExit());
    }

    @Test
    public void isExit_afterAnOrdinaryCommand_isFalse() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("bye");
        kenbot.getResponse("list");
        assertFalse(kenbot.isExit(), "a later command must clear the exit flag");
    }

    @Test
    public void isExit_afterARefusedCommand_isFalse() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("nonsense");
        assertFalse(kenbot.isExit());
    }

    @Test
    public void getGreeting_always_introducesKenbot() {
        assertTrue(kenbotIn(folder).getGreeting().contains("Kenbot"));
    }

    @Test
    public void isLastResponseError_beforeAnyCommand_isFalse() {
        assertFalse(kenbotIn(folder).isLastResponseError());
    }

    @Test
    public void isLastResponseError_afterACommandThatWorked_isFalse() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("todo read book");
        assertFalse(kenbot.isLastResponseError());
    }

    @Test
    public void isLastResponseError_afterAnUnknownCommand_isTrue() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("nonsense");
        assertTrue(kenbot.isLastResponseError());
    }

    @Test
    public void isLastResponseError_afterARefusedArgument_isTrue() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("mark 99");
        assertTrue(kenbot.isLastResponseError());
    }

    @Test
    public void isLastResponseError_afterBlankInput_isTrue() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("   ");
        assertTrue(kenbot.isLastResponseError());
    }

    @Test
    public void isLastResponseError_errorThenSuccess_clearsTheFlag() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("nonsense");
        kenbot.getResponse("todo read book");
        assertFalse(kenbot.isLastResponseError(), "a later good command must clear the flag");
    }

    @Test
    public void isLastResponseError_successThenError_setsTheFlag() {
        Kenbot kenbot = kenbotIn(folder);
        kenbot.getResponse("todo read book");
        kenbot.getResponse("delete 99");
        assertTrue(kenbot.isLastResponseError());
    }

    @Test
    public void getStartupNotice_nothingWrong_isEmpty() {
        assertEquals("", kenbotIn(folder).getStartupNotice());
    }

    @Test
    public void getStartupNotice_noSaveFileYet_isEmptyBecauseThatIsNormal() {
        // A first run on a new machine is not a problem worth reporting.
        assertEquals("", kenbotIn(folder).getStartupNotice());
    }

    @Test
    public void getStartupNotice_lineThatCannotBeRead_saysHowManyWereSkipped()
            throws Exception {
        Path file = folder.resolve("data").resolve("tasks.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, List.of("T | 0 | good one", "GARBAGE", "T | 0 | good two"));

        Kenbot kenbot = kenbotIn(folder);
        assertTrue(kenbot.getStartupNotice().contains("1 line(s)"),
                "the user must be told something was skipped");
        assertTrue(kenbot.getResponse("list").contains("good one"),
                "the lines that could be read must still load");
    }

    @Test
    public void getStartupNotice_savePathIsAFolder_explainsTheProblem() throws Exception {
        Path file = folder.resolve("data").resolve("tasks.txt");
        Files.createDirectories(file);

        Kenbot kenbot = kenbotIn(folder);
        assertFalse(kenbot.getStartupNotice().isEmpty(),
                "an unreadable save file must not look like an empty list");

        // Writing fails for the same reason reading did, so the reply is that
        // failure rather than the list. What matters is that it answers at all:
        // the app stays usable and says what is wrong.
        String reply = kenbot.getResponse("list");
        assertFalse(reply.isBlank(), "the app must still answer, not die");
        assertTrue(kenbot.isLastResponseError(), "and must mark it as a problem");
    }
}
