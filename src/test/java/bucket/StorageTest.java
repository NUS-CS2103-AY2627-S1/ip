package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for reading and writing the save file.
 *
 * Every test writes into a JUnit @TempDir rather than the real data folder, so
 * running the suite never touches the tasks of whoever is using the app.
 *
 * Several of these cover differences between operating systems, which is the part
 * of "test on a different OS" that can actually be automated: how lines are ended,
 * and whether non-ASCII descriptions survive a round trip.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Path saveFile() {
        return tempDir.resolve("data").resolve("bucket.txt");
    }

    private void writeRaw(String contents) throws IOException {
        Path file = saveFile();
        Files.createDirectories(file.getParent());
        Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- round trip ----------

    @Test
    public void saveThenLoad_allThreeTaskTypes_survivesUnchanged() {
        TaskList original = new TaskList();
        original.addItem(new Todo("read book"));
        original.addItem(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        original.addItem(new Event("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16)));

        Storage.save(original, saveFile());
        TaskList reloaded = Storage.load(saveFile());

        assertEquals(3, reloaded.size());
        assertEquals("[T][ ] read book", reloaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", reloaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", reloaded.get(2).toString());
    }

    // Whether a task was done has to survive too, or marking is pointless between runs
    @Test
    public void saveThenLoad_doneTask_staysDone() {
        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.setDone(true);
        original.addItem(todo);

        Storage.save(original, saveFile());

        assertEquals("[T][X] read book", Storage.load(saveFile()).get(0).toString());
    }

    @Test
    public void saveThenLoad_emptyList_givesEmptyList() {
        Storage.save(new TaskList(), saveFile());
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    // Saving twice must replace the file, not append to it
    @Test
    public void save_calledTwice_doesNotAccumulate() {
        TaskList items = new TaskList();
        items.addItem(new Todo("read book"));

        Storage.save(items, saveFile());
        Storage.save(items, saveFile());

        assertEquals(1, Storage.load(saveFile()).size());
    }

    // The data folder does not exist on a first run, so saving has to create it
    @Test
    public void save_missingParentFolder_createsIt() {
        TaskList items = new TaskList();
        items.addItem(new Todo("read book"));

        Storage.save(items, saveFile());

        assertTrue(Files.exists(saveFile()));
    }

    // ---------- first run and empty files ----------

    @Test
    public void load_fileDoesNotExist_givesEmptyListRatherThanThrowing() {
        assertTrue(Storage.load(tempDir.resolve("nothing-here.txt")).isEmpty());
    }

    @Test
    public void load_emptyFile_givesEmptyList() throws IOException {
        writeRaw("");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    @Test
    public void load_onlyBlankLines_givesEmptyList() throws IOException {
        writeRaw("\n\n   \n");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    // ---------- operating system differences ----------

    /*
     * Windows ends lines with CRLF and Unix with LF. A file saved on one and opened
     * on the other, for instance through a synced folder or a repository, must still
     * load, so both endings are read back explicitly here.
     */
    @Test
    public void load_unixLineEndings_readsEveryLine() throws IOException {
        writeRaw("T | 0 | read book\nD | 0 | return book | 2019-10-15\n");
        assertEquals(2, Storage.load(saveFile()).size());
    }

    @Test
    public void load_windowsLineEndings_readsEveryLine() throws IOException {
        writeRaw("T | 0 | read book\r\nD | 0 | return book | 2019-10-15\r\n");
        assertEquals(2, Storage.load(saveFile()).size());
    }

    @Test
    public void load_noTrailingNewline_readsLastLine() throws IOException {
        writeRaw("T | 0 | read book");
        assertEquals(1, Storage.load(saveFile()).size());
    }

    /*
     * A description typed on a machine set to another language must come back as it
     * went in. This fails if the file is written in one encoding and read in another,
     * which is what happens when the platform default charset is used instead of UTF-8.
     */
    @Test
    public void saveThenLoad_chineseDescription_survivesUnchanged() {
        TaskList items = new TaskList();
        items.addItem(new Todo("读一本书"));

        Storage.save(items, saveFile());

        assertEquals("[T][ ] 读一本书", Storage.load(saveFile()).get(0).toString());
    }

    @Test
    public void saveThenLoad_accentedDescription_survivesUnchanged() {
        TaskList items = new TaskList();
        items.addItem(new Todo("café brûlée"));

        Storage.save(items, saveFile());

        assertEquals("[T][ ] café brûlée", Storage.load(saveFile()).get(0).toString());
    }

    // The save file is written as UTF-8 regardless of what the machine defaults to
    @Test
    public void save_nonAsciiDescription_writtenAsUtf8() throws IOException {
        TaskList items = new TaskList();
        items.addItem(new Todo("读一本书"));

        Storage.save(items, saveFile());

        String raw = new String(Files.readAllBytes(saveFile()), StandardCharsets.UTF_8);
        assertTrue(raw.contains("读一本书"));
    }

    // ---------- damaged files ----------

    // One unreadable line must not cost the user the rest of their tasks
    @Test
    public void load_oneCorruptedLine_keepsTheOthers() throws IOException {
        writeRaw("T | 0 | read book\nthis is not a task\nT | 0 | buy milk\n");
        assertEquals(2, Storage.load(saveFile()).size());
    }

    @Test
    public void load_unknownTypeLetter_skipsThatLine() throws IOException {
        writeRaw("T | 0 | read book\nZ | 0 | mystery\n");
        assertEquals(1, Storage.load(saveFile()).size());
    }

    // A deadline needs four fields; three means the line lost its date
    @Test
    public void load_deadlineMissingItsDate_skipsThatLine() throws IOException {
        writeRaw("D | 0 | return book\n");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    @Test
    public void load_eventMissingItsSecondDate_skipsThatLine() throws IOException {
        writeRaw("E | 0 | meeting | 2019-10-15\n");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    // A date that is not a real date makes the whole line unusable
    @Test
    public void load_unparseableDate_skipsThatLine() throws IOException {
        writeRaw("D | 0 | return book | not-a-date\n");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    @Test
    public void load_impossibleDate_skipsThatLine() throws IOException {
        writeRaw("D | 0 | return book | 2019-02-29\n");
        assertTrue(Storage.load(saveFile()).isEmpty());
    }

    @Test
    public void load_blankLineBetweenTasks_ignoresIt() throws IOException {
        writeRaw("T | 0 | read book\n\nT | 0 | buy milk\n");
        assertEquals(2, Storage.load(saveFile()).size());
    }

    /*
     * PowerShell's Set-Content writes a UTF-8 byte order mark by default. It sits in
     * front of the first line and makes "T" read as something else, so that task
     * silently disappears. Recorded here as the behaviour that actually happens
     * rather than as behaviour anyone wants.
     */
    @Test
    public void load_fileWithByteOrderMark_losesOnlyTheFirstLine() throws IOException {
        writeRaw("﻿T | 0 | read book\nT | 0 | buy milk\n");
        assertEquals(1, Storage.load(saveFile()).size());
    }
}
