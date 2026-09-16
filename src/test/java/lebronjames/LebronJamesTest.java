package lebronjames;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the part of {@link LebronJames} the graphical interface depends on:
 * whether a reply was an answer or a complaint.
 *
 * <p>This matters because the GUI colours the two differently. A flag that
 * stayed set after a good command would paint an ordinary answer as an error,
 * and one that never got set would hide a typo, so both directions are checked
 * as well as the switch back.
 *
 * <p>Every test writes inside a JUnit {@code @TempDir}, so the real save file is
 * never touched.
 */
public class LebronJamesTest {
    @TempDir
    private Path tempFolder;

    private LebronJames lebronJames;

    @BeforeEach
    public void createChatbot() {
        lebronJames = new LebronJames(tempFolder.toString(), "tasks.txt");
    }

    @Test
    public void isErrorReply_freshSaveFile_noError() {
        // Nothing has gone wrong yet, so the greeting must not be painted red.
        assertFalse(lebronJames.isErrorReply());
    }

    @Test
    public void isErrorReply_validCommand_noError() {
        lebronJames.getResponse("todo read book");

        assertFalse(lebronJames.isErrorReply());
    }

    @Test
    public void isErrorReply_unknownCommand_reportsError() {
        lebronJames.getResponse("fly to the moon");

        assertTrue(lebronJames.isErrorReply());
    }

    @Test
    public void isErrorReply_todoWithoutDescription_reportsError() {
        lebronJames.getResponse("todo");

        assertTrue(lebronJames.isErrorReply());
    }

    @Test
    public void isErrorReply_validCommandAfterError_flagIsCleared() {
        lebronJames.getResponse("fly to the moon");
        lebronJames.getResponse("todo read book");

        // Without the reset at the top of getResponse, every reply after the
        // first mistake would keep the error styling for the rest of the run.
        assertFalse(lebronJames.isErrorReply());
    }

    @Test
    public void getResponse_eachReplyStandsAlone_previousReplyNotRepeated() {
        lebronJames.getResponse("todo read book");
        String secondReply = lebronJames.getResponse("todo return book");

        assertFalse(secondReply.contains("read book"), "a reply must not repeat the previous one");
    }

    @Test
    public void isExit_byeCommand_reportsExit() {
        assertFalse(lebronJames.isExit());

        lebronJames.getResponse("bye");

        assertTrue(lebronJames.isExit());
    }
}
