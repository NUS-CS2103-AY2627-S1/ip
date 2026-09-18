package tally.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * Keeps what could not be read, and refuses to write over it.
 *
 * <p>A file Tally cannot make sense of is copied to a name of its own before anything
 * can replace it, since the tally that did load will be written back over the original
 * and take the rest with it. When even the copy cannot be made, the damaged lines exist
 * nowhere else, so writing is refused outright rather than merely remarked upon.
 *
 * <p>Kept apart from Storage because it holds a decision of its own: once writing has
 * been refused, it stays refused for as long as this runs, and Storage asks rather than
 * remembers.
 */
final class Quarantine {
    private final Path file;

    /**
     * Why the tally must not be written, if it must not.
     *
     * <p>The first reason is the one kept, because the later ones follow from it: a file
     * that could not be read is also one whose damage could not be copied, and the
     * reading is what the user has to put right.
     */
    private Optional<String> refusal = Optional.empty();

    /**
     * Creates a quarantine for one data file.
     *
     * @param file the file whose damage is to be kept.
     */
    Quarantine(Path file) {
        this.file = file;
    }

    /**
     * Copies the file aside, so that a later save cannot write over it.
     *
     * <p>The file is copied rather than moved, because the tasks that did load stay on
     * the tally and the next change writes over the original, which would otherwise take
     * the unreadable lines with it. The original also stays where the user left it.
     *
     * <p>The same damage is copied once. An unrepaired file is read again on every start,
     * and a fresh copy each time would fill the folder without adding anything.
     *
     * @return a sentence saying where the copy was put, that one is already kept, or
     *     that none could be made.
     */
    String copyAside() {
        try {
            byte[] damagedBytes = Files.readAllBytes(file);
            Path backupFile = file.resolveSibling(file.getFileName() + ".broken");
            for (int attempt = 1; isNameTaken(backupFile); attempt++) {
                if (isKeptCopyOf(backupFile, damagedBytes)) {
                    return " Already copied to " + backupFile.getFileName() + ".";
                }
                backupFile = file.resolveSibling(file.getFileName() + ".broken." + attempt);
            }
            Files.copy(file, backupFile);
            return " Copied to " + backupFile.getFileName() + " for repair.";
        } catch (IOException exception) {
            refuse("What could not be read in " + file.getFileName()
                    + " could not be copied aside either, so it will not be written over."
                    + " Move it aside or repair it, then start Tally again.");
            return " It could not be copied aside.";
        }
    }

    /**
     * Records why the tally must not be written, keeping the first reason found.
     *
     * @param reason what to tell the user when they next change the tally.
     */
    void refuse(String reason) {
        if (refusal.isEmpty()) {
            refusal = Optional.of(reason);
        }
    }

    /**
     * Returns why the tally must not be written, if it must not.
     *
     * @return the reason, or empty when writing is allowed.
     */
    Optional<String> refusal() {
        return refusal;
    }

    /**
     * Returns whether anything at all sits at a name, a symbolic link included.
     *
     * <p>Links are not followed, because a name holding one is taken whether or not
     * there is anything at the end of it, and copying onto it would write through the
     * link rather than make the copy this is looking for a place for.
     *
     * @param candidate the name being considered for the copy.
     * @return true when the name is not free.
     */
    private static boolean isNameTaken(Path candidate) {
        return Files.exists(candidate, LinkOption.NOFOLLOW_LINKS);
    }

    /**
     * Returns whether a file already holds exactly the damage about to be copied.
     *
     * <p>It has to be a file of its own to count. A symbolic link back to the data file
     * holds the same bytes and so looks like a copy, while keeping nothing: the next save
     * writes through it and the damaged lines are gone from both names at once.
     *
     * @param candidate the file being considered as an existing copy.
     * @param damagedBytes what the data file holds.
     * @return true when the damage is already kept there.
     * @throws IOException if the file is there but cannot be read.
     */
    private static boolean isKeptCopyOf(Path candidate, byte[] damagedBytes) throws IOException {
        return Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)
                && Arrays.equals(Files.readAllBytes(candidate), damagedBytes);
    }
}
