package tally.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tally.TallyException;
import tally.task.Task;

/** Tests what Storage makes of a data file, and what it says about one it cannot read. */
public class StorageLoadTest {
    @TempDir
    private Path folder;

    @Test
    public void load_backupNameHoldsALinkToTheFile_keepsARealCopyElsewhere()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | good\nBAD LINE\n");
        Path decoy = folder.resolve("tally.txt.broken");
        SymbolicLinks.makeOrSkip(folder, decoy, file);

        Storage storage = new Storage(file);
        LoadResult loaded = storage.load();
        // A link back to the data file holds the same bytes, so it looked like a copy
        // while keeping nothing: the save below wrote through it and took the damage.
        assertTrue(loaded.note().orElseThrow().contains("tally.txt.broken.1"),
                loaded.note().orElseThrow());

        storage.save(loaded.tasks());
        assertTrue(Files.readString(folder.resolve("tally.txt.broken.1")).contains("BAD LINE"),
                "the damaged line was kept nowhere");
        assertFalse(Files.readString(file).contains("BAD LINE"));
    }

    @Test
    public void load_theSameDamageTwice_isCopiedAsideOnce() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | good\nBAD LINE\n");

        new Storage(file).load();
        String second = new Storage(file).load().note().orElseThrow();

        // Reading does not rewrite the file, so an unrepaired one would otherwise be
        // copied again on every start, without limit.
        try (Stream<Path> keptFiles = Files.list(folder)) {
            assertEquals(1, (int) keptFiles.filter(each ->
                    each.getFileName().toString().contains(".broken")).count());
        }
        assertTrue(second.contains("Already copied"));
    }

    @Test
    public void load_damagedTwice_keepsEveryRescueCopy() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | first attempt\nBAD LINE\n");
        assertTrue(new Storage(file).load().note().isPresent());

        Files.writeString(file, "T | 0 | second attempt\nWORSE LINE\n");
        assertTrue(new Storage(file).load().note().isPresent());

        // The second damaged file must not overwrite what the first one rescued.
        assertTrue(Files.readString(folder.resolve("tally.txt.broken")).contains("first attempt"));
        assertTrue(Files.readString(folder.resolve("tally.txt.broken.1")).contains("second attempt"));
    }

    @Test
    public void load_noFileYet_returnsNoTasks() throws TallyException {
        assertTrue(new Storage(folder.resolve("tally.txt")).load().tasks().isEmpty());
    }

    @Test
    public void load_lineNotInTheSavedFormat_isSkippedAndReported() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nthis line is nonsense\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(1, loaded.tasks().size());
        assertEquals("[T][ ] read book", loaded.tasks().get(0).toString());
        assertTrue(loaded.note().orElseThrow().contains("Line 2"));
    }

    @Test
    public void load_everyLineReadable_reportsNothing() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nT | 1 | return book\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(2, loaded.tasks().size());
        assertTrue(loaded.note().isEmpty());
    }

    @Test
    public void load_damagedFile_copiedAsideWithItsContentsKept() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        String original = "T | 0 | precious task\nthis line is nonsense\n";
        Files.writeString(file, original);

        new Storage(file).load();

        // Copied, not moved: the tasks that loaded stay on the tally, so the next save
        // writes over the original and would otherwise take the bad line with it.
        Path spoiled = folder.resolve("tally.txt.broken");
        assertTrue(Files.exists(spoiled));
        assertTrue(Files.exists(file));
        assertEquals(original, Files.readString(spoiled));
    }

    @Test
    public void load_blankLines_ignored() throws IOException, TallyException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "\nT | 0 | read book\n\n\nT | 1 | return book\n\n");
        assertEquals(2, new Storage(file).load().tasks().size());
    }

    @Test
    public void load_aLineWithTheWrongNumberOfFields_isSkippedAndReported()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // Each of these names a kind of task, then gives it the wrong number of parts.
        Files.writeString(file, String.join("\n",
                "T | 0 | read book | extra",
                "D | 0 | return book",
                "E | 0 | meeting | 2pm",
                "W | 0 | form | 2026-09-08",
                "T | 0 | the only good line") + "\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(1, loaded.tasks().size());
        assertEquals("[T][ ] the only good line", loaded.tasks().get(0).toString());
        assertTrue(loaded.note().orElseThrow().contains("1, 2, 3 and 4"),
                loaded.note().orElseThrow());
    }

    @Test
    public void load_aBlankFieldWhereOneIsRequired_isSkipped() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // A blank done flag, and a blank description: neither is a task anyone wrote.
        Files.writeString(file, "T |   | read book\nT | 0 |   \nT | 0 | good\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(1, loaded.tasks().size());
        assertTrue(loaded.note().orElseThrow().contains("1 and 2"), loaded.note().orElseThrow());
    }

    @Test
    public void load_aFileAnEditorMarkedAsUtf8_readsItsFirstTaskAnyway()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // Editors on Windows write this at the start and do not show it, so a user who
        // opened the file to correct a line saves it back with the mark on task one.
        Files.writeString(file, "\ufeffT | 0 | read book\nT | 1 | buy bread\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(2, loaded.tasks().size(), loaded.note().orElse("no complaint"));
        assertEquals("[T][ ] read book", loaded.tasks().get(0).toString());
        assertTrue(loaded.note().isEmpty(), loaded.note().orElse(""));
    }

    @Test
    public void load_unreadableFileThatCannotBeCopied_saysEachThingOnce()
            throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Path occupied = folder.resolve("tally.txt.broken");
        Files.writeString(occupied, "older damage\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        Files.setPosixFilePermissions(occupied, PosixFilePermissions.fromString("---------"));
        assumeTrue(!Files.isReadable(file), "these tests are running as a user nothing stops");

        TallyException thrown = assertThrows(TallyException.class, () -> new Storage(file).load());
        String message = thrown.getMessage();
        // Both the read and the copy failing used to append the same refusal, so the
        // user was told twice, in two different wordings, in one breath.
        assertEquals(1, message.split("will not be written over", -1).length - 1, message);
    }

    @Test
    public void load_aTaskNamedTwice_isKeptOnceAndReported() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // Tally refuses to add a task it already holds, so a file naming one twice
        // would otherwise put the tally in a state no command could have reached.
        // The two copies disagree about being done, and dropping one must not throw
        // that away: a task recorded as done anywhere in the file has been done.
        Files.writeString(file, "T | 0 | read book\nT | 1 | read book\nT | 0 | buy bread\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(List.of("[T][X] read book", "[T][ ] buy bread"),
                loaded.tasks().stream().map(Task::toString).toList());
        String note = loaded.note().orElseThrow();
        assertTrue(note.contains("Line 2"), note);
        assertTrue(note.contains("repeats a task already on record"), note);
    }

    @Test
    public void load_repeatsButNoDamage_keepsNoRescueCopy() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nT | 0 | read book\n");

        new Storage(file).load();
        // A repeat is a readable line, not damage, so there is nothing to quarantine.
        try (Stream<Path> left = Files.list(folder)) {
            assertEquals(0, (int) left.filter(each ->
                    each.getFileName().toString().contains(".broken")).count());
        }
    }

    @Test
    public void load_repeatsThatAgreeOnBeingDone_leaveTheFlagAlone()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nT | 0 | read book\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(List.of("[T][ ] read book"),
                loaded.tasks().stream().map(Task::toString).toList());
    }

    @Test
    public void load_theSameTaskSpacedDifferently_isStillOneTask()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // The file is edited by hand, so it can space a description any way at all.
        // Tally holds "read    book" and "read book" to be the same task when they are
        // typed, and the rule has to hold on this road in too, or the file can put two
        // of the same task on a tally no command would have allowed it on.
        Files.writeString(file, "T | 0 | read    book\nT | 1 | read\tbook\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(List.of("[T][X] read book"),
                loaded.tasks().stream().map(Task::toString).toList());
        assertTrue(loaded.note().orElseThrow().contains("Line 2"), loaded.note().orElseThrow());
    }

    @Test
    public void load_paddedEventEndsRunningBackwards_isSkippedRatherThanCrashing()
            throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        // The reader asked about the untidied text while the event read the tidied
        // text, so this slipped past the guard and stopped the program on startup.
        Files.writeString(file, "E | 0 | trip |  2026-09-12 | 2026-09-08\nT | 0 | keep me\n");

        LoadResult loaded = new Storage(file).load();
        assertEquals(List.of("[T][ ] keep me"),
                loaded.tasks().stream().map(Task::toString).toList());
        assertTrue(loaded.note().orElseThrow().contains("Line 1"), loaded.note().orElseThrow());
    }
    @Test
    public void load_fileThatCannotBeOpened_keepsWhatTheFileSystemSaid() throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        assumeTrue(!Files.isReadable(file), "these tests are running as a user nothing stops");

        TallyException thrown = assertThrows(TallyException.class, () -> new Storage(file).load());
        // The user reads the message; the cause says which of a dozen things the file
        // system objected to, which is the only record of it there will ever be.
        assertTrue(thrown.getMessage().contains("could not be read"), thrown.getMessage());
        assertTrue(thrown.getCause() instanceof IOException, String.valueOf(thrown.getCause()));
    }
}
