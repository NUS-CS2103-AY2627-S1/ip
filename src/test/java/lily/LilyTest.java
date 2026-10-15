package lily;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Lily's GUI-facing command-processing method. */
class LilyTest {

    @Test
    void getResponseResult_unknownCommand_marksReplyAsError() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        Lily.Response response = lily.getResponseResult("water plants");

        assertTrue(response.isError());
        assertFalse(response.shouldExit());
        assertTrue(response.message().contains("not sure"));
    }

    @Test
    void getResponse_blankCommand_returnsGardenThemedPrompt() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("What would you like to tend to? Enter a command to get started.",
                lily.getResponse("   "));
    }

    @Test
    void getResponseResult_validCommand_marksReplyAsNonError() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        Lily.Response response = lily.getResponseResult("list");

        assertFalse(response.isError());
        assertFalse(response.shouldExit());
    }
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoThenList_includesAddedTask() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        String addResponse = lily.getResponse("todo read GUI tutorial");
        String listResponse = lily.getResponse("list");

        assertTrue(addResponse.contains("Planted it on your list"));
        assertTrue(listResponse.contains("read GUI tutorial"));
    }

    @Test
    void getResponse_explicitMidnight_remainsVisibleAfterRestart() {
        Path saveFile = temporaryDirectory.resolve("lily.txt");
        Lily lily = new Lily(saveFile.toString());
        lily.getResponse("deadline midnight deployment /by 2026-09-20 0000");

        Lily restartedLily = new Lily(saveFile.toString());

        assertTrue(restartedLily.getResponse("list").contains("Sep 20 2026, 12:00AM"));
    }

    @Test
    void getResponse_unknownCommand_returnsGardenThemedGuidance() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("I’m not sure how to tend to that yet. "
                        + "Try a command such as list, todo, deadline, or event.",
                lily.getResponse("water plants"));
    }

    @Test
    void getStartupMessage_malformedSaveRecord_warningShownToGuiAndCliCallers() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lily.txt");
        Files.write(saveFile, java.util.List.of("T | 0 | valid task", "broken record"),
                StandardCharsets.UTF_8);

        Lily lily = new Lily(saveFile.toString());

        assertTrue(lily.getStartupMessage().startsWith(Lily.WELCOME_MESSAGE));
        assertTrue(lily.getStartupMessage().contains("Startup notice:"));
        assertTrue(lily.getStartupMessage().contains("Skipped malformed entry on line 2"));
        assertTrue(lily.getResponse("list").contains("valid task"));
    }

    @Test
    void getResponse_mixedCaseCommand_commandRecognizedAndDescriptionCasePreserved() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        lily.getResponse("ToDo Read Java Book");

        assertTrue(lily.getResponse("LIST").contains("Read Java Book"));
    }

    @Test
    void getResponse_outerSpacesIgnoredAndRepeatedDescriptionSpacesPreserved() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("Your list is clear—a peaceful patch of soil.", lily.getResponse("  list  "));
        lily.getResponse("todo read  book");
        assertTrue(lily.getResponse("list").contains("read  book"));
    }

    @Test
    void getResponse_noArgumentCommandWithArgument_rejected() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("The list command is ready as it is—it doesn’t need anything after it.",
                lily.getResponse("list now"));
        assertEquals("The bye command is ready as it is—it doesn’t need anything after it.",
                lily.getResponse("bye now"));
    }

    @Test
    void getResponse_indexCommandsWithInvalidIndexes_rejectedBeforeListAccess() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());
        String expected = "I need one positive whole-number task number. Check list to find the right one.";

        assertEquals(expected, lily.getResponse("mark 0"));
        assertEquals(expected, lily.getResponse("unmark -1"));
        assertEquals(expected, lily.getResponse("delete +2"));
        assertEquals(expected, lily.getResponse("mark 2.0"));
        assertEquals(expected, lily.getResponse("delete 2 3"));
    }

    @Test
    void getResponse_missingOrUnknownTaskNumber_returnsGardenThemedGuidance() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("Which task should I mark? Give me its number, e.g. mark 2.",
                lily.getResponse("mark"));
        assertEquals("Which task should I unmark? Give me its number, e.g. unmark 2.",
                lily.getResponse("unmark"));
        assertEquals("Which task should I delete? Give me its number, e.g. delete 2.",
                lily.getResponse("delete"));
        assertEquals("I couldn’t find that task in your garden. Check list and try again.",
                lily.getResponse("mark 1"));
    }

    @Test
    void getResponse_findWithoutKeyword_returnsGardenThemedGuidance() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("What should I look for? Add one keyword after find.", lily.getResponse("find"));
    }

    @Test
    void getResponse_findAndScheduleWithTooManyArguments_rejected() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("The 'find' command accepts exactly one argument.",
                lily.getResponse("find read book"));
        assertEquals("I can show one day at a time. Give schedule one date, today, or tomorrow.",
                lily.getResponse("schedule today extra"));
    }

    @Test
    void getResponseResult_caseInsensitiveBye_returnsGoodbyeAndRequestsExit() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        Lily.Response response = lily.getResponseResult("BYE");

        assertEquals(Lily.GOODBYE_MESSAGE, response.message());
        assertFalse(response.isError());
        assertTrue(response.shouldExit());
    }

    @Test
    void getResponseResult_byeWithArgument_returnsErrorWithoutRequestingExit() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        Lily.Response response = lily.getResponseResult("bye now");

        assertTrue(response.isError());
        assertFalse(response.shouldExit());
    }

    @Test
    void getResponse_schedule_ordersAndGroupsDatedTasksWithOriginalNumbers() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());
        lily.getResponse("todo undated task");
        lily.getResponse("event morning meeting /from 2026-09-16 0900 /to 2026-09-16 1000");
        lily.getResponse("deadline submit report /by 2026-09-16");
        lily.getResponse("deadline call client /by 2026-09-16 1400");
        lily.getResponse("mark 4");

        String response = lily.getResponse("schedule 2026-09-16");

        assertEquals("Here is your schedule for Sep 16 2026:\n"
                + "Not completed:\n"
                + "3. [D][ ] submit report (by: Sep 16 2026)\n"
                + "2. [E][ ] morning meeting (from: Sep 16 2026, 9:00AM to: Sep 16 2026, 10:00AM)\n"
                + "Completed:\n"
                + "4. [D][X] call client (by: Sep 16 2026, 2:00PM)", response);
    }

    @Test
    void getResponse_scheduleMultiDayEvent_includesBothEndpointDates() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());
        lily.getResponse("todo keeps event task number at two");
        lily.getResponse("event conference /from 2026-09-16 /to 2026-09-18");

        assertTrue(lily.getResponse("schedule 2026-09-16").contains("2. [E][ ] conference"));
        assertTrue(lily.getResponse("schedule 2026-09-17").contains("2. [E][ ] conference"));
        assertTrue(lily.getResponse("schedule 2026-09-18").contains("2. [E][ ] conference"));
    }

    @Test
    void getResponse_scheduleEntriesAtSameTime_usesOriginalTaskNumberAsTieBreaker() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());
        lily.getResponse("deadline first /by 2026-09-16 0900");
        lily.getResponse("deadline second /by 2026-09-16 0900");

        String response = lily.getResponse("schedule 2026-09-16");

        assertTrue(response.indexOf("1. [D][ ] first") < response.indexOf("2. [D][ ] second"));
    }

    @Test
    void getResponse_scheduleWithNoMatches_returnsDatedEmptyMessage() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("There are no scheduled tasks for Sep 16 2026.",
                lily.getResponse("schedule 2026-09-16"));
    }

    @Test
    void getResponse_invalidScheduleDate_returnsDateError() {
        Lily lily = new Lily(temporaryDirectory.resolve("lily.txt").toString());

        assertEquals("I couldn't understand the date '2026-02-30'. "
                        + "Accepted formats: yyyy-MM-dd or d/M/yyyy.",
                lily.getResponse("schedule 2026-02-30"));
    }

    @Test
    void getResponse_schedule_doesNotRewriteSaveFile() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lily.txt");
        Lily lily = new Lily(saveFile.toString());
        lily.getResponse("deadline submit report /by 2026-09-16");
        String savedBeforeSchedule = Files.readString(saveFile);

        lily.getResponse("schedule 2026-09-16");

        assertEquals(savedBeforeSchedule, Files.readString(saveFile));
    }
}
