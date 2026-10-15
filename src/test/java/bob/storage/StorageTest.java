package bob.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskDateTime;
import bob.task.Todo;

/**
 * Tests {@link Storage#save} and {@link Storage#load}, which are the whole of
 * the chatbot's memory between one run and the next.
 *
 * <p>These are worth testing closely for two reasons. The first is that the two
 * have to agree exactly: a task saved in one run and misread in the next is lost
 * without anybody being told, and the escaping of bars and backslashes is where
 * that is most easily got wrong. The second is that {@code load} is the one part
 * of the chatbot that has to cope with a file somebody has edited by hand, so
 * every way a line can be damaged is a case it promises to survive.
 *
 * <p>Every test writes to a folder of its own, made and thrown away by JUnit, so
 * nothing here touches the real save file.
 */
public class StorageTest {

    /** A folder to keep each test's save file in, thrown away after the test. */
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_noFileYet_emptyListAndNothingToSay() {
        Storage.LoadResult result = storageAt("bob.txt").load();

        // The ordinary first run is not a problem, so it is not reported as one.
        assertEquals(List.of(), result.tasks());
        assertEquals(List.of(), result.messages());
    }

    @Test
    public void load_emptyFile_emptyListAndNothingToSay() throws IOException {
        Storage storage = storageWithLines();

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertEquals(List.of(), result.messages());
    }

    @Test
    public void load_oneOfEachKindOfTask_allReadBackInOrder() throws IOException {
        Storage storage = storageWithLines(
                "T | 1 | read book",
                "D | 0 | return book | 2026-12-02",
                "E | 0 | project meeting | 2026-12-02 1800 | 2026-12-02 2000");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.messages());
        assertEquals(3, result.tasks().size());
        assertEquals("[T][X] read book", result.tasks().get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2026)", result.tasks().get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2026 18:00 to: Dec 02 2026 20:00)",
                result.tasks().get(2).toString());
    }

    @Test
    public void load_blankLines_skippedWithoutComment() throws IOException {
        // A file opened in an editor easily picks one up; that is not damage.
        Storage storage = storageWithLines("", "T | 0 | read book", "   ", "");

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals(List.of(), result.messages());
    }

    @Test
    public void load_separatorsWithoutPadding_readTheSameWay() throws IOException {
        Storage storage = storageWithLines("T|0|read book");

        Storage.LoadResult result = storage.load();

        assertEquals("[T][ ] read book", result.tasks().get(0).toString());
    }

    @Test
    public void load_backslashesThatEscapeNothing_keptAsWritten() throws IOException {
        // Written by hand: a save would have doubled each backslash.
        Storage storage = storageWithLines("T | 0 | copy C:\\temp\\notes");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.messages());
        assertEquals("[T][ ] copy C:\\temp\\notes", result.tasks().get(0).toString());
    }

    @Test
    public void load_escapeSequencesBesideBackslashesThatEscapeNothing_eachReadAsWritten()
            throws IOException {
        // In the file: a\\b \| c\d
        Storage storage = storageWithLines("T | 0 | a\\\\b \\| c\\d");

        Storage.LoadResult result = storage.load();

        assertEquals("[T][ ] a\\b | c\\d", result.tasks().get(0).toString());
    }

    @Test
    public void saveAfterLoad_backslashesThatEscapeNothing_writtenEscapedAndReadBackUnchanged()
            throws BobException, IOException {
        Storage storage = storageWithLines("T | 0 | copy C:\\temp\\notes");

        storage.save(storage.load().tasks());

        // The backslashes survive the save that used to make their loss permanent.
        assertEquals(List.of("T | 0 | copy C:\\\\temp\\\\notes"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
        assertEquals("[T][ ] copy C:\\temp\\notes", storage.load().tasks().get(0).toString());
    }

    @Test
    public void load_unknownKindOfTask_lineReportedAndSkipped() throws IOException {
        Storage storage = storageWithLines("T | 0 | read book", "X | 0 | who knows");

        Storage.LoadResult result = storage.load();

        // The good line is kept: one damaged line should not cost the user the rest.
        assertEquals(1, result.tasks().size());
        assertTrue(result.messages().get(0).contains("Line 2"));
        assertTrue(result.messages().get(0).contains("not a kind of task I know"));
    }

    @Test
    public void load_statusThatIsNeitherFlag_lineReportedAndSkipped() throws IOException {
        // A status that cannot be read is not quietly assumed to mean "not done".
        Storage storage = storageWithLines("T | maybe | read book");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).contains("doesn't say whether the task is done"));
    }

    @Test
    public void load_wrongNumberOfFields_lineReportedAndSkipped() throws IOException {
        Storage storage = storageWithLines(
                "T | 0",
                "T | 0 | read book | 2026-12-02",
                "D | 0 | return book");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).contains("fields"));
    }

    @Test
    public void load_emptyDescription_lineReportedAndSkipped() throws IOException {
        // A task with no description is one the user could never have added.
        Storage storage = storageWithLines("T | 0 | ");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).contains("description is empty"));
    }

    @Test
    public void load_dateThatIsNotADate_lineReportedAndSkipped() throws IOException {
        Storage storage = storageWithLines("D | 0 | return book | Sunday");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).contains("isn't a date"));
    }

    @Test
    public void load_eventWhoseEndsBreakTheRule_linesReportedAndSkipped() throws IOException {
        // The rule an event typed at the chatbot meets, so a hand-edited file cannot
        // load an event the user could never have added.
        Storage storage = storageWithLines(
                "E | 0 | conference | 2026-12-05 | 2026-12-02",
                "E | 0 | standup | 2026-12-05 0900 | 2026-12-05 0900");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).startsWith("Line 1 "));
        assertTrue(result.messages().get(0).endsWith(": the event ends before it starts."));
        assertTrue(result.messages().get(1).startsWith("Line 2 "));
        assertTrue(result.messages().get(1).endsWith(": the event starts and ends at the same moment."));
    }

    @Test
    public void load_eventEndingAtMidnightOfItsUndatedStartDay_lineReportedAndSkipped() throws IOException {
        // The end day with no time in the line above means the end of the 5th, but here
        // the user asked for midnight, which is where the start day already puts it.
        Storage storage = storageWithLines("E | 0 | blip | 2026-12-05 | 2026-12-05 0000");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        assertTrue(result.messages().get(0).endsWith(": the event starts and ends at the same moment."));
    }

    @Test
    public void load_eventEndingOnItsStartDayWithoutATime_loaded() throws IOException {
        Storage storage = storageWithLines("E | 0 | party | 2026-12-05 1800 | 2026-12-05");

        Storage.LoadResult result = storage.load();

        // The end is the end of the 5th, so the event runs from six in the evening
        // until the day is over.
        assertEquals(List.of(), result.messages());
        assertEquals("[E][ ] party (from: Dec 05 2026 18:00 to: Dec 05 2026)", result.tasks().get(0).toString());
    }

    @Test
    public void load_oneDayEventWithoutTimes_loaded() throws IOException {
        Storage storage = storageWithLines("E | 0 | open day | 2026-12-05 | 2026-12-05");

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.messages());
        assertEquals("[E][ ] open day (from: Dec 05 2026 to: Dec 05 2026)", result.tasks().get(0).toString());
    }

    @Test
    public void load_sameTaskOnTwoLines_bothLoadedAndReportedByListNumberFirst() throws IOException {
        // The blank line makes the file's line numbers differ from the list's.
        Storage storage = storageWithLines(
                "T | 0 | read book",
                "",
                "D | 0 | return book | 2026-12-02",
                "T | 1 | Read  Book");

        Storage.LoadResult result = storage.load();

        // Both are kept: they differ in ways the user may care about, here the done status.
        assertEquals(3, result.tasks().size());
        // Tasks 1 and 3 are the numbers delete takes; lines 1 and 4 are where they are in the file.
        assertEquals(List.of("Tasks 1 and 3 in your list"
                + " (lines 1 and 4 of " + tempDirectory.resolve("bob.txt") + ")"
                + " are the same task. I've kept both — delete the one you don't need."), result.messages());
    }

    @Test
    public void load_sameTaskOnThreeLines_oneMessageNamingAllThree() throws IOException {
        Storage storage = storageWithLines(
                "T | 0 | read book",
                "T | 0 | read book",
                "T | 0 | return book",
                "T | 0 | read book");

        List<String> messages = storage.load().messages();

        assertEquals(List.of("Tasks 1, 2 and 4 in your list"
                + " (lines 1, 2 and 4 of " + tempDirectory.resolve("bob.txt") + ")"
                + " are the same task. I've kept all 3 — delete the ones you don't need."), messages);
    }

    @Test
    public void load_sameDescriptionWithDifferentDatesOrKinds_notReported() throws IOException {
        Storage storage = storageWithLines(
                "D | 0 | return book | 2026-12-02",
                "D | 0 | return book | 2026-12-02 0000",
                "E | 0 | return book | 2026-12-02 | 2026-12-03",
                "T | 0 | return book");

        assertEquals(List.of(), storage.load().messages());
    }

    @Test
    public void load_badLineBetweenSameTasks_reportedFirstAndLineNumbersKept() throws IOException {
        Storage storage = storageWithLines("T | 0 | read book", "nonsense", "T | 0 | read book");

        List<String> messages = storage.load().messages();

        assertTrue(messages.get(0).startsWith("Line 2 "));
        // The unreadable line is left out of the list, so the list counts 1 and 2
        // where the file counts 1 and 3.
        String lastMessage = messages.get(messages.size() - 1);
        assertTrue(lastMessage.startsWith("Tasks 1 and 2 in your list (lines 1 and 3 "));
    }

    @Test
    public void load_oneBadLine_messagesWordedInTheSingular() throws IOException {
        Storage storage = storageWithLines("nonsense");

        List<String> messages = storage.load().messages();

        assertTrue(messages.contains("I've left that line out of your list."));
    }

    @Test
    public void load_severalBadLines_messagesWordedInThePlural() throws IOException {
        Storage storage = storageWithLines("nonsense", "more nonsense");

        List<String> messages = storage.load().messages();

        assertTrue(messages.contains("I've left those 2 lines out of your list."));
        assertTrue(messages.contains("They'll be dropped from " + tempDirectory.resolve("bob.txt")
                + " the next time the list changes, but the copy still has them."));
    }

    @Test
    public void load_moreBadLinesThanAreReported_theRestAreCounted() throws IOException {
        // Seven damaged lines: five are quoted, and the other two are counted, so
        // a thoroughly damaged file does not bury the greeting.
        Storage storage = storageWithLines("a", "b", "c", "d", "e", "f", "g");

        List<String> messages = storage.load().messages();

        assertEquals(5, messages.stream().filter(message -> message.startsWith("Line ")).count());
        assertTrue(messages.contains("...and 2 more lines I couldn't read."));
    }

    @Test
    public void load_exactlyOneMoreBadLineThanIsReported_countWordedInTheSingular() throws IOException {
        // Six damaged lines: five are quoted, and the one left over is the
        // boundary where "more lines" must read "more line" instead.
        Storage storage = storageWithLines("a", "b", "c", "d", "e", "f");

        List<String> messages = storage.load().messages();

        assertTrue(messages.contains("...and 1 more line I couldn't read."));
    }

    @Test
    public void load_folderWhereFileShouldBe_emptyListAndSaidPlainly() throws IOException {
        Files.createDirectory(tempDirectory.resolve("bob.txt"));

        Storage.LoadResult result = storageAt("bob.txt").load();

        assertEquals(List.of(), result.tasks());
        // Not a warning that the file will be overwritten, which no save could do to a folder.
        List<String> expectedMessages = List.of(
                tempDirectory.resolve("bob.txt")
                        + " is a folder, not a file, so your tasks can't be saved.",
                "Move or rename that folder, then start Bob again.");
        assertEquals(expectedMessages, result.messages());
        // A folder holds no tasks to keep, and copying one would only make an empty folder.
        assertFalse(Files.exists(tempDirectory.resolve("bob.txt.bak")));
    }

    @Test
    public void load_fileStartingWithByteOrderMark_firstLineReadAsUsual() throws IOException {
        // Written in UTF-8, the character U+FEFF is the three bytes Notepad puts at the
        // start of a file it saves.
        Path path = tempDirectory.resolve("bob.txt");
        Files.writeString(path, "\uFEFFT | 0 | read book\nT | 1 | return book\n", StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(path).load();

        assertEquals(List.of(), result.messages());
        assertEquals(2, result.tasks().size());
        assertEquals("[T][ ] read book", result.tasks().get(0).toString());
    }

    @Test
    public void load_fileThatIsNotUtf8_fileCopiedAsItWasAndCopyReported() throws IOException {
        // The byte 0xFF never appears in UTF-8, so the file cannot be read as text at all.
        byte[] contents = {'T', ' ', '|', ' ', '0', ' ', '|', ' ', (byte) 0xFF};
        Path path = tempDirectory.resolve("bob.txt");
        Files.write(path, contents);

        Storage.LoadResult result = new Storage(path).load();

        assertEquals(List.of(), result.tasks());
        assertArrayEquals(contents, Files.readAllBytes(tempDirectory.resolve("bob.txt.bak")));
        // The first message quotes the error, whose wording is Java's rather than this project's.
        List<String> expectedWarningAndNote = List.of(
                "I'm starting with an empty list. That file will be overwritten the next time the list"
                        + " changes, but the copy stays as it is.",
                "The file as it was is kept in " + path + ".bak, so nothing in it is lost.");
        assertEquals(3, result.messages().size());
        assertEquals(expectedWarningAndNote, result.messages().subList(1, 3));
    }

    @Test
    public void load_badLine_fileCopiedAsItWasAndCopyReported() throws IOException {
        Storage storage = storageWithLines("T | 0 | read book", "nonsense", "", "T | 1 | return book");
        Path path = tempDirectory.resolve("bob.txt");
        byte[] contentsBeforeLoad = Files.readAllBytes(path);

        List<String> messages = storage.load().messages();

        // The copy is the file byte for byte, the blank and the damaged line included.
        assertArrayEquals(contentsBeforeLoad, Files.readAllBytes(tempDirectory.resolve("bob.txt.bak")));
        List<String> expectedSummary = List.of(
                "I've left that line out of your list.",
                "It'll be dropped from " + path + " the next time the list changes,"
                        + " but the copy still has it.",
                "The file as it was is kept in " + path + ".bak, so nothing in it is lost.");
        assertEquals(expectedSummary, messages.subList(1, messages.size()));
    }

    @Test
    public void load_everyLineLoaded_noBackupMade() throws IOException {
        // Lines holding the same task are all loaded, so they leave nothing out for a copy to keep.
        Storage storage = storageWithLines("T | 0 | read book", "T | 0 | read book");

        storage.load();

        assertFalse(Files.exists(tempDirectory.resolve("bob.txt.bak")));
    }

    @Test
    public void load_backupLeftByAnEarlierStart_replaced() throws IOException {
        Path backupPath = tempDirectory.resolve("bob.txt.bak");
        Files.writeString(backupPath, "a copy made on an earlier start");
        Storage storage = storageWithLines("nonsense");

        storage.load();

        assertEquals(List.of("nonsense"), Files.readAllLines(backupPath, StandardCharsets.UTF_8));
    }

    @Test
    public void load_backupCannotBeMade_warnedToCopyTheFileByHand() throws IOException {
        Storage storage = storageWithLines("nonsense");
        // A folder with something in it, where the copy would go, cannot be replaced by the copy.
        Path blockedPath = tempDirectory.resolve("bob.txt.bak");
        Files.createDirectory(blockedPath);
        Files.writeString(blockedPath.resolve("inside.txt"), "keeps the folder from being replaced");

        List<String> messages = storage.load().messages();

        // With no copy the line really will be lost, so the warning says so as it always did.
        assertEquals(5, messages.size());
        assertEquals("It will be lost the next time the list changes — fix the file to keep it.",
                messages.get(2));
        assertTrue(messages.get(3).startsWith("I couldn't make a backup at " + blockedPath + " ("));
        assertEquals("Copy " + tempDirectory.resolve("bob.txt")
                + " somewhere safe yourself before changing the list.", messages.get(4));
    }

    @Test
    public void load_unreadableFileThatCannotBeCopied_warnedItWillNotBeSavedOver() throws IOException {
        Storage storage = storageWithLines("T | 0 | read book");
        Path path = tempDirectory.resolve("bob.txt");
        makeUnreadable(path);

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.tasks());
        List<String> expectedMessages = List.of(
                "I couldn't read " + path + " (permission denied).",
                "I'm starting with an empty list, and I won't save over that file,"
                        + " so nothing in it is lost.",
                "I couldn't make a backup at " + path + ".bak (permission denied).",
                "Changes won't be saved until you fix that file and start Bob again,"
                        + " or move it somewhere else.");
        assertEquals(expectedMessages, result.messages());
    }

    @Test
    public void save_tasks_writtenOneToALineWithBarsBetweenFields() throws BobException, IOException {
        Storage storage = storageAt("bob.txt");
        Todo todo = new Todo("read book");
        todo.markAsDone();

        storage.save(List.of(todo, new Deadline("return book", TaskDateTime.parse("2026-12-02"))));

        assertEquals(List.of("T | 1 | read book", "D | 0 | return book | 2026-12-02"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void save_missingFolder_folderCreated() throws BobException {
        Storage storage = storageAt("data", "nested", "bob.txt");

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(tempDirectory.resolve("data").resolve("nested").resolve("bob.txt")));
    }

    @Test
    public void save_shorterListThanBefore_oldLinesGone() throws BobException, IOException {
        Storage storage = storageAt("bob.txt");
        storage.save(List.of(new Todo("read book"), new Todo("return book")));

        storage.save(List.of(new Todo("read book")));

        // The whole file is rewritten, so it always says exactly what the list says.
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void save_emptyList_fileIsEmpty() throws BobException, IOException {
        Storage storage = storageAt("bob.txt");
        storage.save(List.of(new Todo("read book")));

        storage.save(List.of());

        assertEquals(List.of(),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void save_overAnExistingFile_replacedWithNoTemporaryFileLeft() throws BobException, IOException {
        Storage storage = storageAt("bob.txt");
        storage.save(List.of(new Todo("read book")));

        storage.save(List.of(new Todo("return book")));

        assertEquals(List.of("T | 0 | return book"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
        // The list is written beside the save file and moved over it, leaving nothing behind.
        assertFalse(Files.exists(tempDirectory.resolve("bob.txt.tmp")));
    }

    @Test
    public void save_temporaryFileLeftByAStoppedSave_overwritten() throws BobException, IOException {
        Path leftover = tempDirectory.resolve("bob.txt.tmp");
        Files.writeString(leftover, "half a line from a save that was cut off");

        storageAt("bob.txt").save(List.of(new Todo("read book")));

        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
        assertFalse(Files.exists(leftover));
    }

    @Test
    public void save_newListCannotBeWritten_oldFileKeptWhole() throws BobException, IOException {
        Storage storage = storageAt("bob.txt");
        storage.save(List.of(new Todo("read book"), new Todo("return book")));
        // A folder with something in it, where the temporary file would go, makes writing
        // the new list fail once the old file is already there, as a full disk would.
        Path blockedPath = tempDirectory.resolve("bob.txt.tmp");
        Files.createDirectory(blockedPath);
        Files.writeString(blockedPath.resolve("inside.txt"), "keeps the folder from being removed");

        assertThrows(BobException.class, () -> storage.save(List.of(new Todo("pay bills"))));

        // Writing straight into the save file would have emptied it before failing.
        assertEquals(List.of("T | 0 | read book", "T | 0 | return book"),
                Files.readAllLines(tempDirectory.resolve("bob.txt"), StandardCharsets.UTF_8));
    }

    @Test
    public void save_overUnreadableFileThatCannotBeCopied_refusedAndFileKeptWhole() throws IOException {
        Storage storage = storageWithLines("T | 0 | read book", "T | 1 | return book");
        Path path = tempDirectory.resolve("bob.txt");
        makeUnreadable(path);
        storage.load();

        BobException error = assertThrows(BobException.class, () ->
                storage.save(List.of(new Todo("pay bills"))));

        String expectedMessage = "I won't save over " + path
                + ", because I couldn't read the tasks already in it."
                + "\nFix that file and start Bob again, or move it somewhere else so I can start a new one."
                + "\nThe change is in this session's list, but it won't survive quitting.";
        assertEquals(expectedMessage, error.getMessage());
        // Replacing the file needs leave to write to its folder, not to read the file,
        // so the refusal is all that kept these tasks.
        makeReadable(path);
        assertEquals(List.of("T | 0 | read book", "T | 1 | return book"),
                Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    @Test
    public void save_unreadableFileMovedAway_savedAsUsualFromThenOn() throws BobException, IOException {
        Storage storage = storageWithLines("T | 0 | read book");
        Path path = tempDirectory.resolve("bob.txt");
        makeUnreadable(path);
        storage.load();
        Files.move(path, tempDirectory.resolve("moved.txt"));

        storage.save(List.of(new Todo("pay bills")));
        // This save finds a file again, but it is the one the save above made.
        storage.save(List.of(new Todo("pay bills"), new Todo("walk dog")));

        assertEquals(List.of("T | 0 | pay bills", "T | 0 | walk dog"),
                Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    @Test
    public void save_unreadableFileReadOnALaterLoad_savedAsUsual() throws BobException, IOException {
        Storage storage = storageWithLines("T | 0 | read book");
        Path path = tempDirectory.resolve("bob.txt");
        makeUnreadable(path);
        storage.load();
        makeReadable(path);
        Task loadedTask = storage.load().tasks().get(0);

        storage.save(List.of(loadedTask, new Todo("pay bills")));

        assertEquals(List.of("T | 0 | read book", "T | 0 | pay bills"),
                Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    @Test
    public void save_overUnreadableFileThatWasCopied_overwritten() throws BobException, IOException {
        // The byte 0xFF never appears in UTF-8, so the file cannot be read, but it can be copied.
        Path path = tempDirectory.resolve("bob.txt");
        Files.write(path, new byte[] {'T', ' ', '|', ' ', '0', ' ', '|', ' ', (byte) 0xFF});
        Storage storage = new Storage(path);
        storage.load();

        storage.save(List.of(new Todo("pay bills")));

        // Nothing is lost by saving over it, since the copy keeps the file as it was.
        assertEquals(List.of("T | 0 | pay bills"), Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    @Test
    public void save_folderWhereFileShouldBe_refusedPlainlyWithNoTemporaryFileLeft() throws IOException {
        Path folder = tempDirectory.resolve("bob.txt");
        Files.createDirectory(folder);

        BobException error = assertThrows(BobException.class, () ->
                storageAt("bob.txt").save(List.of(new Todo("read book"))));

        String expectedMessage = "I couldn't save your tasks to " + folder
                + " because it is a folder, not a file."
                + "\nThe change is in this session's list, but it won't survive quitting.";
        assertEquals(expectedMessage, error.getMessage());
        assertFalse(Files.exists(tempDirectory.resolve("bob.txt.tmp")));
    }

    @Test
    public void save_fileWhereFolderShouldBe_refusedPlainly() throws IOException {
        Path file = tempDirectory.resolve("data");
        Files.writeString(file, "not a folder");

        BobException error = assertThrows(BobException.class, () ->
                storageAt("data", "bob.txt").save(List.of(new Todo("read book"))));

        String expectedMessage = "I couldn't save your tasks to " + file.resolve("bob.txt")
                + " because \"" + file + "\" is a file, not a folder."
                + "\nMove or rename that file so I can make the folder."
                + "\nThe change is in this session's list, but it won't survive quitting.";
        assertEquals(expectedMessage, error.getMessage());
        // The file in the way is left as it was, since it may be something the user needs.
        assertEquals("not a folder", Files.readString(file));
    }

    @Test
    public void hasUnsavedChanges_saveFailed_true() throws IOException {
        Files.createDirectory(tempDirectory.resolve("bob.txt"));
        Storage storage = storageAt("bob.txt");
        assertFalse(storage.hasUnsavedChanges());

        assertThrows(BobException.class, () -> storage.save(List.of(new Todo("read book"))));

        assertTrue(storage.hasUnsavedChanges());
    }

    @Test
    public void hasUnsavedChanges_saveWorksAfterAFailedOne_falseAndWarningForgotten()
            throws BobException, IOException {
        Path folder = tempDirectory.resolve("bob.txt");
        Files.createDirectory(folder);
        Storage storage = storageAt("bob.txt");
        assertThrows(BobException.class, () -> storage.save(List.of(new Todo("read book"))));
        storage.markUnsavedChangesWarningGiven();
        Files.delete(folder);

        storage.save(List.of(new Todo("read book")));

        assertFalse(storage.hasUnsavedChanges());
        // A later failure is a new problem, which the user has not been warned about yet.
        assertFalse(storage.isUnsavedChangesWarningGiven());
    }

    @Test
    public void isUnsavedChangesWarningGiven_saveFailsAgainAfterWarning_stillTrue() throws IOException {
        Files.createDirectory(tempDirectory.resolve("bob.txt"));
        Storage storage = storageAt("bob.txt");
        assertThrows(BobException.class, () -> storage.save(List.of(new Todo("read book"))));
        storage.markUnsavedChangesWarningGiven();

        // The save quitting tries again must not make the user be warned a second time.
        assertThrows(BobException.class, () -> storage.save(List.of(new Todo("read book"))));

        assertTrue(storage.isUnsavedChangesWarningGiven());
    }

    @Test
    public void saveThenLoad_descriptionHoldingASeparator_readBackUnchanged() throws BobException {
        assertSurvivesRoundTrip("tidy up | then rest");
    }

    @Test
    public void saveThenLoad_descriptionHoldingABackslash_readBackUnchanged() throws BobException {
        assertSurvivesRoundTrip("find C:\\notes");
    }

    @Test
    public void saveThenLoad_descriptionEndingInABackslash_readBackUnchanged() throws BobException {
        // The awkward one: the backslash is escaped before the bar is, so the
        // escape it adds is not itself escaped a moment later.
        assertSurvivesRoundTrip("read book\\");
    }

    @Test
    public void saveThenLoad_descriptionHoldingAnEscapedBar_readBackUnchanged() throws BobException {
        assertSurvivesRoundTrip("what \\| means");
    }

    @Test
    public void saveThenLoad_everyKindOfTask_readBackUnchanged() throws BobException {
        Storage storage = storageAt("bob.txt");
        Event event = new Event("project meeting",
                TaskDateTime.parse("2026-12-02 1800"), TaskDateTime.parse("2026-12-03 2000"));
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2026-12-02"));
        deadline.markAsDone();
        List<Task> saved = List.of(new Todo("read book"), deadline, event);

        storage.save(saved);
        List<Task> loaded = storage.load().tasks();

        assertEquals(saved.size(), loaded.size());
        for (int i = 0; i < saved.size(); i++) {
            assertEquals(saved.get(i).toString(), loaded.get(i).toString());
        }
    }

    /** Checks that a description survives being saved and read back unchanged. */
    private void assertSurvivesRoundTrip(String description) throws BobException {
        Storage storage = storageAt("bob.txt");

        storage.save(List.of(new Todo(description)));
        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.messages());
        assertEquals(1, result.tasks().size());
        assertEquals("[T][ ] " + description, result.tasks().get(0).toString());
    }

    /** Returns storage backed by a file at {@code names} inside this test's folder. */
    private Storage storageAt(String... names) {
        Path path = tempDirectory;
        for (String name : names) {
            path = path.resolve(name);
        }
        return new Storage(path);
    }

    /**
     * Takes away every permission on a file, so that it can be neither read nor copied.
     *
     * <p>The calling test is skipped where that cannot be done: on a file system without
     * POSIX permissions, such as Windows', or for a user whom permissions do not stop,
     * such as root.
     */
    private static void makeUnreadable(Path path) throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"),
                "file permissions cannot be taken away on this file system");
        Files.setPosixFilePermissions(path, Set.of());
        assumeFalse(Files.isReadable(path), "permissions do not stop this user reading the file");
    }

    /** Gives the owner of a file back leave to read and write it. */
    private static void makeReadable(Path path) throws IOException {
        Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rw-------"));
    }

    /** Returns storage backed by a save file already holding {@code lines}. */
    private Storage storageWithLines(String... lines) throws IOException {
        Path path = tempDirectory.resolve("bob.txt");
        Files.write(path, List.of(lines), StandardCharsets.UTF_8);
        return new Storage(path);
    }
}
