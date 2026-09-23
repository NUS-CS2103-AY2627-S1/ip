package sallman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sallman.task.Deadline;
import sallman.task.Event;
import sallman.task.Task;
import sallman.task.Todo;

/**
 * Tests that {@link Storage} writes a task list that it can read back, and that
 * a damaged data file costs the user only the damaged lines.
 * <p>
 * Every test writes inside a temporary folder supplied by JUnit, so none of
 * them can touch the real task list.
 */
public class StorageTest {

    @TempDir
    private Path folder;

    @Test
    public void loadThenSave_everyTaskType_roundTripsUnchanged() throws Exception {
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();

        storage.save(List.of(todo,
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Event("conference", LocalDate.of(2019, 10, 14),
                        LocalDate.of(2019, 10, 17))));
        List<Task> loaded = storage.load();

        // Comparing the display form checks the type, description, done status
        // and dates all survived the trip through the file.
        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)",
                loaded.get(2).toString());
    }

    @Test
    public void loadThenSave_taggedTasks_tagsRoundTrip() throws Exception {
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("books");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.addTag("urgent");

        storage.save(List.of(todo, deadline));
        List<Task> loaded = storage.load();

        assertEquals("[T][ ] read book #fun #books", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019) #urgent",
                loaded.get(1).toString());
    }

    @Test
    public void load_fileWrittenBeforeTagsExisted_stillReadsCorrectly() throws Exception {
        // The tag field is optional and marked with #, so a line without one
        // must not have its last field mistaken for tags.
        Storage storage = write("T | 0 | read book",
                "D | 0 | return book | 2019-10-15",
                "E | 0 | conference | 2019-10-14 | 2019-10-17");

        List<Task> loaded = storage.load();

        assertTrue(storage.getSkippedLines().isEmpty());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)",
                loaded.get(2).toString());
    }

    @Test
    public void load_descriptionContainsSeparator_notMistakenForTags() throws Exception {
        Storage storage = write("T | 0 | pipe | in description");

        assertEquals("[T][ ] pipe | in description", storage.load().get(0).toString());
    }

    @Test
    public void loadThenSave_descriptionEndingInWhatLooksLikeTags_keptAsDescription() throws Exception {
        // With no tags of its own, a todo's description is the last field on its
        // line, so a description ending in " | #..." must not be read back as tags.
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        storage.save(List.of(new Todo("buy milk | #urgent"), new Todo("pack bag | #")));

        List<Task> loaded = storage.load();

        assertEquals("[T][ ] buy milk | #urgent", loaded.get(0).toString());
        assertTrue(loaded.get(0).getTags().isEmpty());
        assertEquals("[T][ ] pack bag | #", loaded.get(1).toString());
    }

    @Test
    public void loadThenSave_taggedTaskWhoseDescriptionLooksLikeTags_bothKept() throws Exception {
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        Todo todo = new Todo("buy milk | #urgent");
        todo.addTag("errand");

        storage.save(List.of(todo));

        assertEquals("[T][ ] buy milk | #urgent #errand", storage.load().get(0).toString());
    }

    @Test
    public void load_fileDoesNotExist_emptyListAndNoError() throws Exception {
        // The first run on a new computer has no data file, which is not a fault.
        Storage storage = new Storage(folder.resolve("never-written.txt").toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    public void save_folderDoesNotExist_folderCreated() throws Exception {
        Path nested = folder.resolve("data").resolve("tasks.txt");
        assertFalse(Files.exists(nested.getParent()));

        new Storage(nested.toString()).save(List.of(new Todo("read book")));

        assertTrue(Files.exists(nested));
    }

    @Test
    public void save_emptyList_fileEmptiedNotLeftStale() throws Exception {
        // Deleting the last task must clear the file, or it would come back.
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        storage.save(List.of(new Todo("read book")));
        storage.save(List.of());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void loadThenSave_descriptionContainsFieldSeparator_survives() throws Exception {
        // " | " separates the fields, so a description containing it could be
        // mistaken for extra fields and silently truncated.
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());
        storage.save(List.of(new Todo("read | book"),
                new Deadline("pay a | b", LocalDate.of(2019, 10, 15))));

        List<Task> loaded = storage.load();

        assertEquals("[T][ ] read | book", loaded.get(0).toString());
        assertEquals("[D][ ] pay a | b (by: Oct 15 2019)", loaded.get(1).toString());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    public void load_corruptedLines_goodLinesKeptAndBadOnesReported() throws Exception {
        Storage storage = write(
                "T | 1 | read book",
                "GARBAGE LINE",
                "D | 0 | return book | 2019-10-15",
                "X | 0 | alien task",
                "T | 7 | bad flag",
                "D | 0 | broken date | not-a-date");

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());

        List<String> skipped = storage.getSkippedLines();
        assertEquals(4, skipped.size());
        // Each report names the line number, so a hand-edited file can be fixed.
        assertEquals("line 2: expected at least 3 fields, found 1", skipped.get(0));
        assertEquals("line 4: unknown task type \"X\"", skipped.get(1));
        assertEquals("line 5: the done flag should be 0 or 1, found \"7\"", skipped.get(2));
        assertEquals("line 6: the due date \"not-a-date\" is not a date", skipped.get(3));
    }

    @Test
    public void load_eventEndingBeforeItStarts_lineSkipped() throws Exception {
        // The parser never creates one, so such a line was edited by hand, and
        // loading it would put an event in the list that no date can find.
        Storage storage = write("T | 0 | read book",
                "E | 0 | trip | 2019-10-17 | 2019-10-15");

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(List.of("line 2: the event ends before it starts"), storage.getSkippedLines());
    }

    @Test
    public void load_dataPathIsAFolder_exceptionSaysItCouldNotRead() throws Exception {
        Storage storage = new Storage(Files.createDirectory(folder.resolve("tasks.txt")).toString());

        SallmanException e = assertThrows(SallmanException.class, storage::load);

        assertTrue(e.getMessage().startsWith("I couldn't read your saved tasks"));
    }

    @Test
    public void save_folderPathTakenByAFile_exceptionSaysItCouldNotSave() throws Exception {
        // The data file's folder cannot be created where a file already has that name.
        Path blocker = Files.createFile(folder.resolve("data"));
        Storage storage = new Storage(blocker.resolve("tasks.txt").toString());

        SallmanException e = assertThrows(SallmanException.class, () -> storage.save(List.of()));

        assertTrue(e.getMessage().startsWith("I couldn't save your tasks"));
    }

    @Test
    public void save_folderMissing_createdFirst() throws Exception {
        Storage storage = new Storage(folder.resolve("new/nested/tasks.txt").toString());

        storage.save(List.of(new Todo("read book")));

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_linesMissingTheirDatesOrDescription_eachSkippedWithTheReason() throws Exception {
        Storage storage = write("D | 0 | return book",
                "E | 0 | trip | 2019-10-15",
                "E | 0 | trip",
                "T | 0 | ");

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("line 1: a deadline needs a due date",
                "line 2: an event needs both a start and an end",
                "line 3: an event needs both a start and an end",
                "line 4: the description is empty"), storage.getSkippedLines());
    }

    @Test
    public void load_tagFieldWithEmptyEntries_emptyEntriesIgnored() throws Exception {
        Storage storage = write("T | 0 | read book | #fun,, ,books");

        assertEquals("[T][ ] read book #fun #books", storage.load().get(0).toString());
    }

    @Test
    public void load_tagThatCouldNotHaveBeenTyped_lineSkipped() throws Exception {
        // Loading it would leave a tag that untag refuses, so it could never be removed.
        Storage storage = write("T | 0 | read book",
                "T | 0 | buy milk | #fun,bad!tag");

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(List.of("line 2: the tag \"bad!tag\" is not a valid tag"), storage.getSkippedLines());
    }

    @Test
    public void load_lineThatIsNotValidText_onlyThatLineSkipped() throws Exception {
        // An accented letter saved by an editor using a Windows code page rather
        // than UTF-8 must cost only its own line, not the whole list.
        Storage storage = writeBytes(bytesWithLatin1Line());

        List<Task> loaded = storage.load();

        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[T][ ] buy milk", loaded.get(1).toString());
        assertEquals(List.of("line 2: the line is not valid UTF-8 text"), storage.getSkippedLines());
    }

    @Test
    public void load_windowsLineEndingsAndByteOrderMark_readAsPlainLines() throws Exception {
        // Windows editors may end lines with CR LF and start the file with a
        // byte order mark; neither is part of the text.
        String text = "﻿T | 0 | read book\r\nT | 0 | buy milk\r\n";
        Storage storage = writeBytes(text.getBytes(StandardCharsets.UTF_8));

        List<Task> loaded = storage.load();

        assertTrue(storage.getSkippedLines().isEmpty());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[T][ ] buy milk", loaded.get(1).toString());
    }

    @Test
    public void save_afterTheFileCouldNotBeRead_refusesToOverwriteIt() throws Exception {
        // Saving then would replace every task in the file with this session's
        // list, which starts empty because nothing could be read.
        Storage storage = new Storage(Files.createDirectory(folder.resolve("tasks.txt")).toString());
        assertThrows(SallmanException.class, storage::load);

        SallmanException e = assertThrows(SallmanException.class, () -> storage.save(List.of(new Todo("x"))));

        assertTrue(e.getMessage().startsWith("I'm sorry, but I won't save over"));
    }

    @Test
    public void load_blankLines_skippedSilently() throws Exception {
        // A trailing newline is normal, so blank lines must not be reported.
        Storage storage = write("T | 0 | read book", "", "   ");

        assertEquals(1, storage.load().size());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    public void load_calledTwice_reportsAreNotCumulative() throws Exception {
        // The skipped lines are remembered between calls, so they must be
        // cleared each time or the same faults would be reported twice.
        Storage storage = write("GARBAGE LINE");

        storage.load();
        storage.load();

        assertEquals(1, storage.getSkippedLines().size());
    }

    /**
     * Writes raw bytes to a fresh data file inside the temporary folder.
     *
     * @param bytes the exact contents of the file
     * @return storage backed by that file
     */
    private Storage writeBytes(byte[] bytes) throws IOException {
        Path file = folder.resolve("tasks.txt");
        Files.write(file, bytes);
        return new Storage(file.toString());
    }

    /**
     * Returns a three-line data file whose second line spells "cafe" with an
     * accented e in Latin-1, which is not valid UTF-8.
     *
     * @return the file's bytes
     */
    static byte[] bytesWithLatin1Line() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("T | 0 | read book\nT | 0 | caf".getBytes(StandardCharsets.UTF_8));
        out.write(0xE9);
        out.write(" visit\nT | 0 | buy milk\n".getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    /**
     * Writes the given lines to a fresh data file inside the temporary folder.
     *
     * @param lines contents of the file, one line each
     * @return storage backed by that file
     */
    private Storage write(String... lines) throws IOException {
        Path file = folder.resolve("tasks.txt");
        Files.write(file, List.of(lines));
        return new Storage(file.toString());
    }
}
