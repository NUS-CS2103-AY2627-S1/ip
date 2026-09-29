package emma;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks the console conversation loop end to end: a whole session is fed in through
 * {@code System.in} and the printed transcript is read back.
 *
 * <p>This covers the part of {@link Emma} the other tests do not reach, since they call
 * {@code getResponse} one line at a time rather than letting Emma drive the loop.
 * {@code Emma.main} itself is left alone, as it would write to the real save file.
 */
public class EmmaRunTest {

    @TempDir
    Path tempDir;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private Path saveFile() {
        return tempDir.resolve("emma.json");
    }

    /** Runs a whole session with the given lines as the user's input. */
    private Emma runWith(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        Emma emma = new Emma(saveFile().toString());
        emma.run();
        return emma;
    }

    private String transcript() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_sessionEndingInBye_greetsAnswersAndSaysGoodbye() {
        Emma emma = runWith("todo read book\nbye\n");

        assertTrue(transcript().contains(Ui.getGreeting()), transcript());
        assertTrue(transcript().contains("Got it, I've added this:"), transcript());
        assertTrue(transcript().contains("Bye for now! Hope to see you again soon."), transcript());
        assertTrue(emma.isExit());
    }

    @Test
    public void run_always_printsTheBannerFirst() {
        runWith("bye\n");
        assertTrue(transcript().contains("_____"), transcript());
    }

    @Test
    public void run_inputRunningOutWithoutBye_stopsAtTheEndOfTheInput() {
        Emma emma = runWith("list\n");

        assertTrue(transcript().contains("You haven't given me anything to track yet!"), transcript());
        assertFalse(emma.isExit());
    }

    @Test
    public void run_noInputAtAll_stillGreets() {
        runWith("");
        assertTrue(transcript().contains(Ui.getGreeting()), transcript());
    }

    @Test
    public void run_linesAfterBye_areNotRead() {
        runWith("bye\nlist\n");
        assertFalse(transcript().contains("You haven't given me anything to track yet!"), transcript());
    }

    @Test
    public void run_saveFileThatCannotBeRead_saysSoBeforeTheConversationStarts() throws IOException {
        Files.writeString(saveFile(), "not json at all");

        runWith("bye\n");

        assertTrue(transcript().contains("looks wrong on line 1"), transcript());
        assertTrue(transcript().contains("I'll start with an empty list."), transcript());
    }

    @Test
    public void run_saveFileThatCanBeRead_saysNothingAboutLoading() {
        runWith("todo read book\nbye\n");
        printed.reset();

        runWith("list\nbye\n");

        assertFalse(transcript().contains("I'll start with an empty list."), transcript());
        assertTrue(transcript().contains("1. [T][ ] read book"), transcript());
    }

    @Test
    public void run_tasksAddedDuringTheSession_areOnDiskAfterwards() throws IOException {
        runWith("todo read book\nbye\n");
        assertTrue(Files.readString(saveFile()).contains("\"description\": \"read book\""));
    }

    @Test
    public void run_commandThatCannotBeCarriedOut_showsTheComplaintAndKeepsGoing() {
        runWith("blah\nlist\nbye\n");

        assertTrue(transcript().contains("Sorry, I don't know what that means!"), transcript());
        assertTrue(transcript().contains("You haven't given me anything to track yet!"), transcript());
        assertTrue(transcript().contains("Bye for now! Hope to see you again soon."), transcript());
    }

    @Test
    public void run_blankLine_isAnsweredLikeAnyUnknownCommand() {
        runWith("\nbye\n");
        assertTrue(transcript().contains("Sorry, I don't know what that means!"), transcript());
    }
}
