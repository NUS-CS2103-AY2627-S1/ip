package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the replies {@link Ui} writes once, for both faces, in terms of
 * {@link Ui#show}: a list under a heading, and an error.
 *
 * <p>They are reached through a {@link RecordingUi}, which writes nothing but
 * {@code show}, so what these cases see is exactly what the console and the
 * window are both handed. The greeting and the goodbye are fixed replies, and
 * are checked where the bot says them, in {@link PiplupBotTest}.</p>
 */
public class UiTest {

    /** Catches what is said. */
    private final RecordingUi ui = new RecordingUi();

    /**
     * The heading and the items arrive as one reply, heading first. The items
     * are copied in one place along from the heading, so a copy that started in
     * the wrong place would overwrite the heading.
     */
    @Test
    public void showList_headingAndItems_showsOneReplyWithHeadingFirst() {
        ui.showList("Here are the tasks in your list:", "1.[T][ ] read book", "2.[T][ ] write notes");

        assertEquals(List.of("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] write notes"), ui.getReplies());
    }

    /** With nothing to list, the heading is still shown, on its own. */
    @Test
    public void showList_noItems_showsTheHeadingAlone() {
        ui.showList("Here are the tasks in your list:");

        assertEquals(List.of("Here are the tasks in your list:"), ui.getReplies());
    }

    /**
     * Each line of an error becomes a line of one reply, in order. Showing the
     * error's {@code getMessage()} instead would run the lines together into
     * one.
     */
    @Test
    public void showError_errorWithTwoLines_showsBothLinesInOneReply() {
        ui.showError(new PiplupBotException("Pip... Something went wrong.", "Here is what to try."));

        assertEquals(List.of("Pip... Something went wrong.\nHere is what to try."), ui.getReplies());
    }
}
