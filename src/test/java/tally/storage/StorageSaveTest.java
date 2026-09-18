package tally.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tally.TallyException;
import tally.task.Deadline;
import tally.task.Event;
import tally.task.Moment;
import tally.task.Task;
import tally.task.Todo;
import tally.task.Window;

/** Tests that Storage writes the tally without risking what the file already holds. */
public class StorageSaveTest {
    @TempDir
    private Path folder;

    @Test
    public void save_fileTheUserProtected_isRefusedRatherThanReplaced() throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | protected\n");
        assumeTrue(file.toFile().setReadOnly(), "this file system has no read-only bit");

        Storage storage = new Storage(file);
        // Renaming over a file needs write permission on the directory rather than the
        // file, so without a check of its own the rename would go straight through.
        assertThrows(TallyException.class, () -> storage.save(List.of(new Todo("sneaky"))));
        assertEquals("T | 0 | protected", Files.readString(file).strip());
    }

    @Test
    public void save_fileWithItsOwnPermissions_keepsThem() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | private\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class));
        Set<PosixFilePermission> ownerOnlyPermissions = PosixFilePermissions.fromString("rw-------");
        Files.setPosixFilePermissions(file, ownerOnlyPermissions);

        new Storage(file).save(List.of(new Todo("another")));
        assertEquals(ownerOnlyPermissions, Files.getPosixFilePermissions(file));
    }

    @Test
    public void save_dataFileIsASymbolicLink_writesThroughIt() throws TallyException, IOException {
        Path real = folder.resolve("actual.txt");
        Files.writeString(real, "T | 0 | alpha\n");
        Path link = folder.resolve("tally.txt");
        SymbolicLinks.makeOrSkip(folder, link, real);

        new Storage(link).save(List.of(new Todo("alpha"), new Todo("beta")));
        assertTrue(Files.isSymbolicLink(link), "the link was replaced by a regular file");
        assertTrue(Files.readString(real).contains("beta"), "the link was not written through");
    }

    @Test
    public void save_danglingSymbolicLink_writesThroughItRatherThanReplacingIt()
            throws TallyException, IOException {
        // The link points at a file that is not there yet, which is what separates this
        // from the case above: there is nothing for toRealPath to resolve the link to.
        Path real = folder.resolve("actual.txt");
        Path link = folder.resolve("tally.txt");
        SymbolicLinks.makeOrSkip(folder, link, real);

        new Storage(link).save(List.of(new Todo("alpha")));
        assertTrue(Files.isSymbolicLink(link), "the link was replaced by a regular file");
        assertEquals(List.of("T | 0 | alpha"), Files.readAllLines(real));
    }

    @Test
    public void save_damageThatCouldNotBeCopiedAside_isRefused() throws TallyException, IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nBAD LINE\n");
        // A rescue copy of some earlier damage, which cannot be read to tell the two
        // apart, so this damage cannot be copied aside anywhere.
        Path occupied = folder.resolve("tally.txt.broken");
        Files.writeString(occupied, "older damage\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(occupied, PosixFilePermissions.fromString("---------"));
        assumeTrue(!Files.isReadable(occupied), "these tests are running as a user nothing stops");

        Storage storage = new Storage(file);
        LoadResult loaded = storage.load();
        assertEquals(1, loaded.tasks().size());
        assertTrue(loaded.note().orElseThrow().contains("could not be copied aside"));

        // Saving the one task that loaded would drop the damaged line for good.
        List<Task> readableTasks = loaded.tasks();
        TallyException refused = assertThrows(
                TallyException.class, () -> storage.save(readableTasks));
        assertTrue(refused.getMessage().contains("will not be written over"),
                refused.getMessage());
        assertTrue(Files.readString(file).contains("BAD LINE"), "the damaged line was lost");
    }

    @Test
    public void save_writeFails_leavesTheFileAsItWas() throws IOException {
        Path lockedFolder = Files.createDirectory(folder.resolve("locked"));
        Path file = lockedFolder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | keep me\n");
        // A folder that will take no new file is what a full disk looks like from here:
        // the replacement cannot be written, and what is already saved has to survive.
        assumeTrue(lockedFolder.toFile().setWritable(false), "no read-only bit here");
        assumeTrue(!Files.isWritable(lockedFolder), "the bit does not bind for this user");

        try {
            Storage storage = new Storage(file);
            assertThrows(TallyException.class, () -> storage.save(List.of(new Todo("new task"))));
            assertEquals("T | 0 | keep me", Files.readString(file).strip());
        } finally {
            lockedFolder.toFile().setWritable(true);
        }
    }

    @Test
    public void saveThenLoad_everyTaskType_survivesUnchanged() throws TallyException {
        Path file = folder.resolve("tally.txt");
        Storage storage = new Storage(file);

        Task done = new Todo("read book");
        done.markAsDone();
        List<Task> savedTasks = List.of(done,
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Event("project meeting", Moment.read("2019-08-06").orElseThrow(),
                        Moment.read("2019-08-07 16:00").orElseThrow()),
                new Window("submit form", LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 12)));
        storage.save(savedTasks);

        List<Task> loadedTasks = storage.load().tasks();
        assertEquals(savedTasks.size(), loadedTasks.size());
        for (int i = 0; i < savedTasks.size(); i++) {
            assertEquals(savedTasks.get(i).toString(), loadedTasks.get(i).toString());
        }
    }

    @Test
    public void save_missingDirectory_createsIt() throws TallyException {
        Path file = folder.resolve("nested").resolve("deeper").resolve("tally.txt");
        new Storage(file).save(List.of(new Todo("read book")));
        assertTrue(Files.exists(file));
    }

    @Test
    public void save_calledAgain_replacesWhatWasThereBefore() throws TallyException {
        Path file = folder.resolve("tally.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("only")));
        assertEquals(1, storage.load().tasks().size());
    }

    @Test
    public void save_symbolicLinksPointingAtEachOther_isRefusedRatherThanFollowedForever()
            throws IOException {
        Path first = folder.resolve("tally.txt");
        Path second = folder.resolve("other.txt");
        SymbolicLinks.makeOrSkip(folder, first, second);
        SymbolicLinks.makeOrSkip(folder, second, first);

        // Following them one after another never reaches a file, so the chain is given
        // a limit rather than being walked until the program stops responding.
        List<Task> tasks = List.of(new Todo("read book"));
        Storage storage = new Storage(first);
        assertThrows(TallyException.class, () -> storage.save(tasks));
    }

    @Test
    public void save_aLinkNamedRelativeToItsOwnFolder_isFollowedToWhereItPoints()
            throws TallyException, IOException {
        Path link = folder.resolve("tally.txt");
        // Named without a folder, so it resolves against the link's own folder.
        SymbolicLinks.makeOrSkip(folder, link, Path.of("actual.txt"));

        new Storage(link).save(List.of(new Todo("read book")));
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(folder.resolve("actual.txt")));
        assertTrue(Files.isSymbolicLink(link), "the link was replaced by a regular file");
    }
}
