package davidgoggins.storage;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Task;
import davidgoggins.ui.Ui;

/**
 * Deals with loading tasks from the save file and writing them back to it.
 *
 * <p>The only class that knows a file is involved: {@link davidgoggins.task.TaskList} asks it to load or
 * save and hears nothing about paths or IO errors. A failure is reported through the
 * {@link Ui} and then shrugged off, so the session survives an unwritable disk.
 */
public class Storage {

    /** Added to the save file's name to name the copy kept before a damaged file is replaced. */
    private static final String BACKUP_SUFFIX = ".bak";

    /**
     * Where the list is saved between runs.
     *
     * <p>Held as a {@link Path} rather than a string so the correct separator is used on
     * every OS, and kept relative to the folder the program is run from so it works on
     * any machine.
     */
    private final Path file;

    /** Used to warn the user when a load or a save does not work out. */
    private final Ui ui;

    /**
     * Whether saving may replace the file on disk.
     *
     * <p>False once the file could neither be read nor copied aside, so the only copy of
     * the user's tasks is never overwritten by the empty list that loading produced.
     */
    private boolean canOverwrite = true;

    /**
     * Creates a storage that reads from and writes to the given file.
     *
     * @param filePath where to keep the saved list, e.g. {@code "data/tasks.txt"}
     * @param ui       used to report a load or save that fails
     */
    public Storage(String filePath, Ui ui) {
        this.file = Path.of(filePath);
        this.ui = ui;
        // The folder is not created here: a run that never changes the list should not
        // leave a stray ./data/ folder behind. save() creates it when it is needed.
    }

    /**
     * Writes the whole list to the save file, replacing whatever was there before.
     *
     * <p>The entire file is rewritten on every change rather than appending one line,
     * because {@code delete}, {@code mark} and {@code unmark} alter lines that are
     * already written, and a file has no "replace line 2" operation.
     *
     * @param tasks the tasks to write, in the order they should be stored
     */
    public void save(List<Task> tasks) {
        if (!canOverwrite) {
            ui.showWarning("not saving, so the unreadable " + file + " is not overwritten. "
                    + "Your changes are in this session only.");
            return;
        }

        // Every line, the last included, ends with a line separator, as a text file should.
        String lines = tasks.stream()
                .map(task -> task.toSaveFormat() + System.lineSeparator())
                .collect(Collectors.joining());

        Path temporary = null;
        try {
            // createDirectories (plural) makes any missing parent folders and does
            // nothing if they already exist, unlike createDirectory which throws.
            Path parent = file.getParent();
            if (parent != null) { // null only if the path were a bare file name
                Files.createDirectories(parent);
            }

            // The new list is written to a temporary file first and only then moved
            // into place. Writing straight to the real file would empty it before the
            // new contents were written, so a crash or a full disk part-way through
            // would leave the user with no tasks at all rather than the previous ones.
            // The temporary file goes in the same folder so the move stays on one disk.
            temporary = Files.createTempFile(parent, "tasks", ".tmp");
            Files.writeString(temporary, lines);
            replace(temporary, file);
            temporary = null; // the move consumed it, so there is nothing left to clean up
        } catch (IOException e) {
            // Saving is a background chore, so a failure warns the user but does not
            // stop the command they asked for from succeeding in memory.
            ui.showWarning("could not save your tasks to " + file
                    + " (" + describe(e) + "). Your last change is in this session only.");
        } finally {
            deleteIfPresent(temporary);
        }
    }

    /**
     * Moves {@code source} onto {@code target}, replacing it.
     *
     * <p>An atomic move is preferred, because it means a reader can only ever see the
     * old file or the new one, never a half-written mixture. Not every file system
     * supports it, so a plain replacing move is used when it is refused.
     *
     * @param source the file to move, which no longer exists once this returns
     * @param target the file to replace
     * @throws IOException if neither kind of move succeeds
     */
    private static void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a leftover temporary file, ignoring any further failure. */
    private static void deleteIfPresent(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Nothing useful can be done about a temporary file that will not delete,
            // and the user has already been told the save itself failed.
        }
    }

    /**
     * Reads the previously saved tasks back off disk.
     *
     * <p>Called once at start-up. A missing file, an unreadable file and a corrupted
     * line are each survivable and handled separately below: start-up always continues,
     * with a warning where the user would otherwise wonder where their tasks went.
     *
     * @return the tasks that could be read, which may be an empty list
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (NoSuchFileException e) {
            // Expected on a first run, so this is not worth telling the user about.
            // Caught before IOException because it is a subclass of it.
            return tasks;
        } catch (IOException e) {
            // Covers an unreadable file, a folder where the file should be, and so on.
            ui.showWarning("could not read your saved tasks (" + describe(e)
                    + "). Starting with an empty list." + backUp());
            return tasks;
        }

        int skipped = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue; // a stray blank line is harmless, not corruption
            }
            try {
                tasks.add(Task.fromSaveFormat(line));
            } catch (DavidGogginsException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            ui.showWarning("skipped " + skipped + " unreadable line"
                    + (skipped == 1 ? "" : "s") + " in " + file + "." + backUp());
        }
        return tasks;
    }

    /**
     * Copies a damaged save file aside before the next save overwrites it.
     *
     * <p>Lines that cannot be read are dropped the next time the list is saved, so
     * without a copy a hand-editing slip would silently cost the user those tasks.
     *
     * @return a sentence, with a leading space, saying where the copy is or what will be
     *         lost; empty if there is nothing to copy
     */
    private String backUp() {
        if (!Files.isRegularFile(file)) {
            // A folder or other oddity in the file's place has no lines worth keeping.
            return "";
        }
        Path backup = file.resolveSibling(file.getFileName() + BACKUP_SUFFIX);
        try {
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
            return " Your original file is backed up to " + backup + ".";
        } catch (IOException e) {
            canOverwrite = false;
            return " It could not be backed up (" + describe(e)
                    + ") either, so I won't save over it: changes stay in this session only.";
        }
    }

    /**
     * Returns a short, plain-English reason for a file error.
     *
     * <p>Several of Java's file exceptions carry only the file name as their message,
     * which tells the user where it went wrong but not what went wrong.
     *
     * @param e the error to describe
     * @return the reason, e.g. {@code "permission denied"}
     */
    private static String describe(IOException e) {
        if (e instanceof AccessDeniedException) {
            return "permission denied";
        }
        if (e instanceof CharacterCodingException) {
            return "it is not a plain text file";
        }
        if (e instanceof FileAlreadyExistsException) {
            return "a file is in the way of " + e.getMessage();
        }
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
