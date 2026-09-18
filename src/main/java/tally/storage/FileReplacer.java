package tally.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.List;

/**
 * Puts new contents in a file without risking what is already in it.
 *
 * <p>The contents are written beside the file and renamed over it, so a write that
 * fails partway leaves either the old file or the new one and never a mixture. What
 * the old file carried is carried over deliberately, since a rename replaces a file
 * rather than writing into it.
 *
 * <p>Kept apart from Storage because this is mechanism rather than policy: it knows
 * nothing about tasks, or about when a file should be left alone, and Storage decides
 * both without knowing how a file is safely replaced.
 */
final class FileReplacer {
    /** How many symbolic links may be followed before the chain is called a loop. */
    private static final int MAX_LINKS_FOLLOWED = 8;

    /** Prevents anyone making one: every method here is static. */
    private FileReplacer() {
    }

    /**
     * Writes the given lines to a file, replacing whatever it held before.
     *
     * @param target the file to replace, which need not exist yet.
     * @param lines what it should hold afterwards, one per line.
     * @throws IOException if it cannot be written or put in place.
     */
    static void replace(Path target, List<String> lines) throws IOException {
        Path partial = null;
        try {
            // A name of its own, so that a file already sitting at a fixed one is not
            // overwritten, and two Tallys saving at once do not write the same place.
            // It goes in the target's own folder, because the rename that puts it in
            // place is only atomic within one folder.
            partial = Files.createTempFile(getFolderOf(target), target.getFileName().toString(),
                    ".part");
            Files.write(partial, lines);
            copyPermissions(target, partial);
            moveIntoPlace(partial, target);
        } catch (IOException exception) {
            deleteQuietly(partial);
            throw exception;
        }
    }

    /**
     * Returns the folder a file sits in.
     *
     * <p>Taken from the absolute form of the path, because a path written as a bare
     * name, such as "tally.txt", has no parent of its own even though it plainly sits
     * somewhere.
     *
     * @param path the file whose folder is wanted.
     * @return the folder holding it.
     */
    static Path getFolderOf(Path path) {
        return path.toAbsolutePath().getParent();
    }

    /**
     * Returns the file a path finally names, following symbolic links.
     *
     * <p>toRealPath covers a link pointing at a file that is there, but a link pointing
     * at one that is not resolves to nothing at all, and the save would then replace the
     * link itself with an ordinary file instead of writing through it.
     *
     * @param start the path to resolve.
     * @return what the last link in the chain names, which need not exist yet.
     * @throws IOException if a link cannot be read, or the chain does not end.
     */
    static Path followLinks(Path start) throws IOException {
        Path here = start;
        for (int followed = 0; Files.isSymbolicLink(here); followed++) {
            if (followed == MAX_LINKS_FOLLOWED) {
                throw new IOException("Too many symbolic links to follow from " + start);
            }
            Path pointee = Files.readSymbolicLink(here);
            here = pointee.isAbsolute() ? pointee : here.resolveSibling(pointee);
        }
        return here;
    }

    /**
     * Puts the written replacement in place of the file it replaces.
     *
     * <p>Asked for as one indivisible step, so that a crash midway leaves either the old
     * file or the new one and never a mixture. Not every file system can promise that,
     * and replacing without the promise is still better than writing into the file where
     * it lies, which a crash could leave half rewritten.
     *
     * @param partial the replacement that has been written.
     * @param target the file it replaces.
     * @throws IOException if it cannot be put in place.
     */
    private static void moveIntoPlace(Path partial, Path target) throws IOException {
        try {
            Files.move(partial, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) {
            // Whether an atomic move may replace a file that is already there is left
            // to the file system, and one that will not say so need not use the named
            // exception for it. Replacing without the promise is the fallback either
            // way, and if that fails too its own complaint is the one that gets out.
            Files.move(partial, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Gives the replacement file the permissions the one it replaces already had.
     *
     * <p>Without this the new file is made under the umask, so a file the user had kept
     * private would quietly become readable by others on the first save.
     *
     * @param existing the file being replaced, which may not exist yet.
     * @param replacement the file about to take its place.
     * @throws IOException if the permissions can be read but not written.
     */
    private static void copyPermissions(Path existing, Path replacement) throws IOException {
        // Not a question of whether the file system is POSIX: there is nothing to copy
        // from a file that is not there yet, which is the ordinary case on a first save.
        boolean shouldCopyPosixPermissions = Files.exists(existing)
                && Files.getFileStore(existing).supportsFileAttributeView(PosixFileAttributeView.class);
        if (shouldCopyPosixPermissions) {
            Files.setPosixFilePermissions(replacement, Files.getPosixFilePermissions(existing));
        }
    }

    /**
     * Removes a half-written file, saying nothing if it cannot be removed.
     *
     * <p>This runs while a write is already failing, so a complaint from here would hide
     * the reason it failed, which is the more useful of the two.
     *
     * @param leftover the file to remove, or null if none was made.
     */
    private static void deleteQuietly(Path leftover) {
        if (leftover == null) {
            return;
        }
        try {
            Files.deleteIfExists(leftover);
        } catch (IOException exception) {
            // The failing write is the more useful complaint; this would hide it.
        }
    }
}
