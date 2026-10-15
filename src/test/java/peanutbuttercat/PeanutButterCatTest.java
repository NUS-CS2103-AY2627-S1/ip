package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests commands processed through the chatbot's reusable application interface. */
class PeanutButterCatTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getWelcomeMessage_newConversation_returnsPersonalityGreeting() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertEquals("Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper."
                        + System.lineSeparator()
                        + "Tell me what's on your plate, and I'll tuck it into the task jar.",
                peanutButterCat.getWelcomeMessage());
    }

    @Test
    void getResponse_addThenList_returnsFormattedResponses() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertEquals("Spread the word - this task is in the jar:" + System.lineSeparator()
                        + "[T][ ] buy cat food" + System.lineSeparator()
                        + "The task jar now holds 1 task.",
                peanutButterCat.getResponse("todo buy cat food"));
        assertEquals("Here's what's tucked in the task jar:" + System.lineSeparator()
                        + "1.[T][ ] buy cat food",
                peanutButterCat.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommand_returnsErrorAndKeepsTasks() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertEquals("My whiskers can't sort that command yet. Try another scoop, purr-lease!",
                peanutButterCat.getResponse("sleep"));
        assertEquals("Here's what's tucked in the task jar:", peanutButterCat.getResponse("list"));
    }

    @Test
    void getResponse_blankCommand_returnsSpecificError() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertEquals("I didn't catch a command. Type something for me to do, purr-lease!",
                peanutButterCat.getResponse("   "));
    }

    @Test
    void getResponse_duplicateTask_rejectsDuplicateAndKeepsSingleTask() {
        PeanutButterCat peanutButterCat = createChatbot();
        peanutButterCat.getResponse("todo read book");

        assertEquals("That task is already in the jar. Try adding something different!",
                peanutButterCat.getResponse("todo READ   book"));
        assertEquals("Here's what's tucked in the task jar:" + System.lineSeparator()
                        + "1.[T][ ] read book",
                peanutButterCat.getResponse("list"));
    }

    @Test
    void getResponse_statusChangesAndDelete_returnsPersonalityResponses() {
        PeanutButterCat peanutButterCat = createChatbot(fixedClock());
        peanutButterCat.getResponse("todo submit report");

        assertEquals("Paw-some! That's one smooth finish:" + System.lineSeparator()
                        + "  [T][X] submit report",
                peanutButterCat.getResponse("mark 1"));
        assertEquals("Back on the plate! This task is active again:" + System.lineSeparator()
                        + "  [T][ ] submit report",
                peanutButterCat.getResponse("unmark 1"));
        assertEquals("Scoop complete! I've removed this task from the jar:" + System.lineSeparator()
                        + "  [T][ ] submit report" + System.lineSeparator()
                        + "The task jar now holds 0 tasks.",
                peanutButterCat.getResponse("delete 1"));
    }

    @Test
    void getResponse_repeatedStatusChange_returnsSpecificError() {
        PeanutButterCat peanutButterCat = createChatbot(fixedClock());
        peanutButterCat.getResponse("todo submit report");

        assertEquals("That task is already active. No change needed!",
                peanutButterCat.getResponse("unmark 1"));
        peanutButterCat.getResponse("mark 1");
        assertEquals("That task is already marked as done. No change needed!",
                peanutButterCat.getResponse("mark 1"));
    }

    @Test
    void isExitCommand_byeInput_returnsTrue() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertTrue(peanutButterCat.isExitCommand(" bye "));
        assertFalse(peanutButterCat.isExitCommand("list"));
        assertFalse(peanutButterCat.isExitCommand(null));
    }

    @Test
    void getCommandType_knownAndUnknownInput_returnsMatchingType() {
        PeanutButterCat peanutButterCat = createChatbot();

        assertEquals(CommandType.TODO, peanutButterCat.getCommandType("todo buy cat food"));
        assertEquals(CommandType.UNKNOWN, peanutButterCat.getCommandType("sleep"));
    }

    @Test
    void getResponse_statsAfterCompletingTask_returnsRecentCompletionCount() {
        PeanutButterCat peanutButterCat = createChatbot(fixedClock());
        peanutButterCat.getResponse("todo submit report");
        peanutButterCat.getResponse("mark 1");

        assertEquals("In the last 7 calendar days, you finished 1 task. Nice spread!",
                peanutButterCat.getResponse("stats"));
    }

    @Test
    void getResponse_statsWithArguments_returnsSpecificError() {
        PeanutButterCat peanutButterCat = createChatbot(fixedClock());

        assertEquals("No extra toppings needed for statistics! Use: stats",
                peanutButterCat.getResponse("stats week"));
    }

    @Test
    void getResponse_statsWithLegacyCompletedTask_reportsUnknownDateWithoutSaving() throws IOException {
        Path saveFile = temporaryDirectory.resolve("legacy").resolve("tasks.txt");
        Files.createDirectories(saveFile.getParent());
        Files.write(saveFile, List.of("T | 1 | old completed task"));
        PeanutButterCat peanutButterCat = new PeanutButterCat(
                new Storage(saveFile.toString()), new Parser(), fixedClock());

        assertEquals("In the last 7 calendar days, you finished 0 tasks. Nice spread!" + System.lineSeparator()
                        + "Note: 1 completed task has an unknown completion date and was not counted.",
                peanutButterCat.getResponse("stats"));
        assertEquals(List.of("T | 1 | old completed task"), Files.readAllLines(saveFile));
    }

    @Test
    void getResponse_storageFailure_returnsErrorAndRevertsTaskChange() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "blocking file");
        PeanutButterCat peanutButterCat = new PeanutButterCat(
                new Storage(blockingFile.resolve("tasks.txt").toString()), new Parser());

        assertEquals("I couldn't save that change. Please check that PeanutButterCat can write "
                        + "to its data folder, then try again.",
                peanutButterCat.getResponse("todo buy cat food"));
        assertEquals("Here's what's tucked in the task jar:", peanutButterCat.getResponse("list"));
    }

    @Test
    void getWelcomeMessage_invalidSavedRecord_includesRecoveryWarning() throws IOException {
        Path saveFile = temporaryDirectory.resolve("invalid").resolve("tasks.txt");
        Files.createDirectories(saveFile.getParent());
        Files.writeString(saveFile, "this is not a task record");

        PeanutButterCat peanutButterCat = new PeanutButterCat(
                new Storage(saveFile.toString()), new Parser());

        assertTrue(peanutButterCat.getWelcomeMessage().contains(
                "Heads up: some saved task data could not be read. I recovered what I could"));
        assertEquals("Here's what's tucked in the task jar:", peanutButterCat.getResponse("list"));
    }

    private PeanutButterCat createChatbot() {
        return createChatbot(Clock.systemDefaultZone());
    }

    private PeanutButterCat createChatbot(Clock clock) {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        return new PeanutButterCat(new Storage(saveFile.toString()), new Parser(), clock);
    }

    private Clock fixedClock() {
        return Clock.fixed(Instant.parse("2026-09-09T12:00:00Z"), ZoneOffset.UTC);
    }
}
