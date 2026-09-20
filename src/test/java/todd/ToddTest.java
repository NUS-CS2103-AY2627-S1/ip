package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing shared by Todd's text and graphical interfaces. */
public class ToddTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addThenList_persistsAndDisplaysTask() {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);

        String addedResponse = todd.getResponse("todo read JavaFX guide");
        String listResponseAfterReload = new Todd(dataPath).getResponse("list");

        assertTrue(addedResponse.contains("read JavaFX guide"));
        assertTrue(listResponseAfterReload.contains("[T][ ] read JavaFX guide"));
    }

    @Test
    public void getResponse_invalidCommand_returnsHelpfulError() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));

        String response = todd.getResponse("dance");

        assertEquals("What does that mean dawg? Sorry I am a little dumb.\n\n"
                + "You can type help for all the commands I know", Ui.formatForGui(response));
    }

    @Test
    public void getResponse_markThenDelete_updatesStoredTaskList() {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo read JavaFX guide");

        String markedResponse = todd.getResponse("mark 1");
        String savedMarkedTask = new Todd(dataPath).getResponse("list");
        String deletedResponse = todd.getResponse("delete 1");
        String savedEmptyList = new Todd(dataPath).getResponse("list");

        assertTrue(markedResponse.contains("[T][X] read JavaFX guide"));
        assertTrue(savedMarkedTask.contains("[T][X] read JavaFX guide"));
        assertTrue(deletedResponse.contains("read JavaFX guide"));
        assertTrue(savedEmptyList.contains("I think the list is empty gang"));
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));

        String response = todd.getResponse("help");

        assertTrue(response.contains("deadline <description> /by <date> [HHmm]"));
        assertTrue(response.contains("ADD TASKS\n"));
        assertTrue(response.contains("\nVIEW & FIND\n"));
        assertTrue(response.contains("\nUPDATE TASKS\n"));
        assertTrue(response.contains("Example: todo read notes"));
        assertTrue(response.contains("[HHmm] means the time is optional"));
        assertTrue(response.contains("today, tomorrow, or a weekday"));
    }
    @Test
    public void getResponse_blankOrUnexpectedArguments_returnsGuidance() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        assertTrue(todd.getResponse("  ").contains("Type a command first"));
        assertTrue(todd.getResponse(null).contains("Type a command first"));
        for (String command : new String[]{"list", "help", "reminders", "bye", "hi", "hello"}) {
            assertTrue(todd.getResponse(command + " extra").contains("Use just " + command));
        }
    }

    @Test
    public void getResponse_invalidAdd_doesNotChangeMemoryOrFile() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo original");
        String original = Files.readString(dataPath);
        todd.getResponse("todo a | b");
        todd.getResponse("deadline report /by 2026-02-30");
        todd.getResponse("event meeting /from today /to today");
        assertEquals(original, Files.readString(dataPath));
        assertEquals(new Todd(dataPath).getResponse("list"), todd.getResponse("list"));
    }

    @Test
    public void getResponse_corruptSave_preservesFileAndBlocksChanges() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        String original = "T | 0 | original\ninvalid data\n";
        Files.writeString(dataPath, original);
        Todd todd = new Todd(dataPath);
        assertTrue(todd.getWelcomeMessage().contains("line 2"));
        for (String command : new String[]{"todo new", "mark 1", "unmark 1", "delete 1",
            "deadline report /by today", "event meeting /from today /to tomorrow"}) {
            assertTrue(todd.getResponse(command).contains("changes are disabled"));
        }
        assertEquals(original, Files.readString(dataPath));
    }

    @Test
    public void getResponse_failedSaves_rollBackAllMutationsAndAllowRetry() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo first");
        todd.getResponse("todo second");
        todd.getResponse("mark 2");
        String originalList = todd.getResponse("list");
        Path backup = tempDirectory.resolve("backup.txt");
        Files.move(dataPath, backup);
        // A nonempty directory at the save path causes a deterministic failure on every OS.
        Files.createDirectory(dataPath);
        Files.writeString(dataPath.resolve("keep.txt"), "keep");
        for (String command : new String[]{"todo new", "mark 1", "unmark 2", "delete 1"}) {
            assertTrue(todd.getResponse(command).contains("couldn't save"));
            assertEquals(originalList, todd.getResponse("list"));
            assertEquals("keep", Files.readString(dataPath.resolve("keep.txt")));
        }
        Files.delete(dataPath.resolve("keep.txt"));
        Files.delete(dataPath);
        Files.move(backup, dataPath);
        assertTrue(todd.getResponse("todo retry").contains("Another side quest? Okay, added:"));
        assertTrue(new Todd(dataPath).getResponse("list").contains("retry"));
        assertFalse(new Todd(dataPath).getResponse("list").contains("[T][ ] new"));
    }
    @Test
    public void getGuiWelcomeMessage_corruptSave_retainsWarningWithoutConsoleDecoration() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Files.writeString(dataPath, "invalid data");
        String welcome = new Todd(dataPath).getGuiWelcomeMessage();
        assertTrue(welcome.startsWith("Hello There!"));
        assertTrue(welcome.contains("line 1"));
        assertTrue(welcome.contains("changes are disabled"));
        assertFalse(welcome.contains("________"));
    }

    @Test
    public void formatForGui_taskReply_preservesTaskContent() {
        Ui ui = new Ui();
        String formatted = Ui.formatForGui(ui.formatAdded(new Todo("read ______ notes"), 1));
        assertTrue(formatted.startsWith("Another side quest? Okay, added:"));
        assertTrue(formatted.contains("[T][ ] read ______ notes"));
        assertFalse(formatted.contains("____________________________________________________________"));
        assertEquals("plain reply", Ui.formatForGui("plain reply"));
    }
    @Test
    public void getResponse_greetings_returnsSuppWithoutChangingTasks() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        String originalList = todd.getResponse("list");
        assertEquals("Supp", todd.getResponse("hi"));
        assertEquals("Supp", todd.getResponse("  hello  "));
        assertEquals(originalList, todd.getResponse("list"));
        assertFalse(todd.getWelcomeMessage().contains(":P"));
        assertFalse(todd.getGuiWelcomeMessage().contains(":P"));
    }

    @Test
    public void getResponse_missingArguments_explainsWhatToType() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        assertEquals("Wait you didn't tell me what the todo is. Try todo <description>",
                Ui.formatForGui(todd.getResponse("todo")));
        String[][] examples = {
            {"deadline", "Try deadline <description> /by <date> [HHmm]"},
            {"deadline /by today", "what the deadline is"},
            {"deadline report", "when the deadline is"},
            {"event", "Try event <description> /from <date> [HHmm] /to <date> [HHmm]"},
            {"event /from today /to tomorrow", "what the event is"},
            {"event study", "when the event starts"},
            {"event study /from /to tomorrow", "when the event starts"},
            {"event study /from today", "when the event ends"},
            {"find", "Try find <keyword>"},
            {"on", "Try on <date>"},
            {"mark", "Try mark <task number>"},
            {"unmark", "Try unmark <task number>"},
            {"delete", "Try delete <task number>"}
        };
        for (String[] example : examples) {
            assertTrue(todd.getResponse(example[0]).contains(example[1]), example[0]);
        }
    }
    @Test
    public void getReply_errorsAndHelp_haveDistinctDisplayCategories() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        Response error = todd.getReply("dance");
        assertTrue(error.error());
        assertFalse(error.help());
        Response help = todd.getReply("help");
        assertTrue(help.help());
        assertFalse(help.error());
        assertTrue(todd.getReply("help extra").error());
        assertFalse(todd.getReply("help extra").help());
        // A task containing error wording must still appear as a successful response.
        Response added = todd.getReply("todo What does that mean dawg?");
        assertFalse(added.error());
        assertFalse(added.help());
    }

    @Test
    public void getReply_alreadyUnmarked_reportsOriginalTaskNumber() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        todd.getResponse("todo first");
        todd.getResponse("todo second");
        Response response = todd.getReply("unmark 2");
        assertEquals("Gang, I think task 2 is already unmarked.", Ui.formatForGui(response.text()));
        assertTrue(response.error());
    }
    @Test
    public void getResponse_markAndUnmark_useEncouragingMessages() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        todd.getResponse("todo read notes");
        assertTrue(todd.getResponse("mark 1").contains("Okayy one more step to making it out alive."));
        assertTrue(todd.getResponse("unmark 1").contains("It's okay, I believe in you!"));
    }

    @Test
    public void getResponse_emptyReminders_omitsUpcomingHeading() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        assertEquals("Nothing coming up. Its peaceful out here.",
                Ui.formatForGui(todd.getResponse("reminders")));
        todd.getResponse("deadline report /by today");
        assertTrue(todd.getResponse("reminders")
                .contains("These deadlines are getting a little too close for comfort:"));
        todd.getResponse("mark 1");
        assertEquals("Nothing coming up. Its peaceful out here.",
                Ui.formatForGui(todd.getResponse("reminders")));
    }

    @Test
    public void getResponse_nonexistentTaskNumber_usesUpdatedMessage() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        for (String command : new String[]{"mark 1", "unmark 1", "delete 1"}) {
            assertEquals("Am I tripping or that task number does not exist?",
                    Ui.formatForGui(todd.getResponse(command)));
        }
    }
    @Test
    public void getReply_bye_exitsOnlyForAValidGoodbyeCommand() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        Response goodbye = todd.getReply("  bye  ");
        assertTrue(goodbye.exit());
        assertFalse(goodbye.error());
        assertEquals("Nooo ok bye atb on making it out alive", goodbye.text());
        assertFalse(todd.getReply("bye extra").exit());
        assertTrue(todd.getReply("bye extra").error());
        assertFalse(todd.getReply("todo bye").exit());
        assertFalse(todd.getReply("hello").exit());
    }
    @Test
    public void getResponse_personalityMessages_preserveTaskDetailsAndErrors() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        assertEquals("I think the list is empty gang, there's no way you have nothing to do.",
                Ui.formatForGui(todd.getResponse("list")));
        String added = Ui.formatForGui(todd.getResponse("todo read notes"));
        assertTrue(added.startsWith("Another side quest? Okay, added:"));
        assertTrue(added.contains("[T][ ] read notes"));
        assertTrue(Ui.formatForGui(todd.getResponse("list")).startsWith("Here's the current survival plan:"));
        String found = Ui.formatForGui(todd.getResponse("find notes"));
        assertTrue(found.startsWith("Found these lurking in your quest log:"));
        assertTrue(found.contains("read notes"));
        assertEquals("Couldn't find it gang. Try something else.",
                Ui.formatForGui(todd.getResponse("find missing")));
        todd.getResponse("mark 1");
        Response markedAgain = todd.getReply("mark 1");
        assertTrue(markedAgain.error());
        assertEquals("Gang, task 1 is already done. Take the win.", Ui.formatForGui(markedAgain.text()));
        Response invalidEvent = todd.getReply("event study /from tomorrow /to today");
        assertTrue(invalidEvent.error());
        assertEquals("I can't unlock time travel yet. Put the end after the start.",
                Ui.formatForGui(invalidEvent.text()));
        String deleted = Ui.formatForGui(todd.getResponse("delete 1"));
        assertTrue(deleted.startsWith("Quest abandoned. I saw nothing."));
        assertTrue(deleted.contains("read notes"));
    }
    @Test
    public void getResponse_findThenMarkAndDelete_targetsDisplayedTaskNumber() {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo unrelated task");
        todd.getResponse("todo read notes");
        todd.getResponse("todo more notes");
        String found = todd.getResponse("find notes");
        assertTrue(found.contains("2.[T][ ] read notes"));
        assertTrue(found.contains("3.[T][ ] more notes"));
        assertFalse(found.contains("1.[T]"));
        todd.getResponse("mark 2");
        assertTrue(new Todd(dataPath).getResponse("list").contains("2.[T][X] read notes"));
        todd.getResponse("delete 3");
        String remaining = new Todd(dataPath).getResponse("list");
        assertTrue(remaining.contains("1.[T][ ] unrelated task"));
        assertFalse(remaining.contains("more notes"));
    }

    @Test
    public void getReply_commonInvalidInputs_returnErrorsWithoutChangingTasks() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        todd.getResponse("todo original");
        String original = todd.getResponse("list");
        String[] invalidCommands = {
            "", "dance", "todo", "deadline", "event", "mark", "unmark", "delete", "find", "on",
            "mark abc", "delete -1", "mark 999999999999999999999", "delete 2", "list extra",
            "deadline report /by 2026-02-30", "deadline report /by today 2460",
            "deadline report /by today /by tomorrow", "event study /from today /to today",
            "event study /from tomorrow /to today", "event study /to tomorrow /from today"
        };
        for (String input : invalidCommands) {
            assertTrue(todd.getReply(input).error(), input);
            assertEquals(original, todd.getResponse("list"), input);
        }
    }

    @Test
    public void getResponse_missingDataFile_startsEmptyAndCreatesSaveOnAdd() {
        Path dataPath = tempDirectory.resolve("new-folder").resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        assertFalse(todd.getWelcomeMessage().contains("couldn't load"));
        assertFalse(todd.getReply("todo first task").error());
        assertTrue(Files.isRegularFile(dataPath));
        assertTrue(new Todd(dataPath).getResponse("list").contains("first task"));
    }

    @Test
    public void getResponse_directoryAtDataPath_reportsLoadFailureAndPreservesContents() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Files.createDirectory(dataPath);
        Path existingFile = dataPath.resolve("keep.txt");
        Files.writeString(existingFile, "keep");
        Todd todd = new Todd(dataPath);
        assertTrue(todd.getWelcomeMessage().contains("couldn't load"));
        assertTrue(todd.getReply("todo new task").error());
        assertEquals("keep", Files.readString(existingFile));
    }
    @Test
    public void getReply_duplicateTasksAfterReload_returnsErrorsAndPreservesSave() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        String[] commands = {"todo study", "deadline report /by 2026-09-18",
            "event meeting /from 2026-09-18 1400 /to 2026-09-18 1500"};
        String[] duplicates = {"  todo study  ", "deadline report /by 2026-09-18 0000",
            "event meeting /from 2026-09-18 1400 /to 2026-09-18 1500"};
        String[] types = {"task", "deadline", "event"};
        for (String command : commands) {
            assertFalse(todd.getReply(command).error());
        }
        todd.getResponse("mark 1");
        String saved = Files.readString(dataPath);
        Todd reopened = new Todd(dataPath);
        String originalList = reopened.getResponse("list");
        for (int i = 0; i < duplicates.length; i++) {
            Response response = reopened.getReply(duplicates[i]);
            assertTrue(response.error());
            assertEquals("Wait, that " + types[i] + " is already in the list.",
                    Ui.formatForGui(response.text()));
            assertEquals(saved, Files.readString(dataPath));
            assertEquals(originalList, reopened.getResponse("list"));
        }
    }
    @Test
    public void formatReminders_dateRangeAndHelp_explainInclusiveCalendarDates() {
        Ui ui = new Ui();
        LocalDate today = LocalDate.of(2026, 9, 18);
        String response = ui.formatReminders(today, 7,
                List.of(new Deadline("report", today.atTime(18, 0))), List.of(1));
        assertTrue(response.contains("Sep 18 2026 to Sep 24 2026 (both dates included)"));
        assertTrue(ui.formatHelp().contains("today and the next 6 days"));
    }
}
