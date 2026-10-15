package bot67.storage;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import bot67.exception.Bot67Exception;
import bot67.parser.Parser;
import bot67.task.Deadline;
import bot67.task.Event;
import bot67.task.Task;
import bot67.task.Todo;

/** Loads validated task records and replaces the save file only after a complete write. */
public class Storage {
    private static final Path DEFAULT_SAVE_FILE = Path.of("data", "bot67.txt");
    private static final Path LEGACY_SAVE_FILE = Path.of("data", "duke.txt");

    private final Path saveFile;
    private final Path legacySaveFile;
    private Path loadFile;

    /** Uses Bot67's save path while retaining read compatibility with the old Duke path. */
    public Storage() {
        this(DEFAULT_SAVE_FILE, LEGACY_SAVE_FILE);
    }

    /** Uses an explicit save path, allowing isolated tests without touching user data. */
    public Storage(Path saveFile) {
        this(saveFile, null);
    }

    /** Creates storage with an optional read-only fallback for data from an older product name. */
    Storage(Path saveFile, Path legacySaveFile) {
        this.saveFile = saveFile;
        this.legacySaveFile = legacySaveFile;
        this.loadFile = saveFile;
    }

    /** Returns the file to check when a load or save fails. */
    public Path getSaveFile() {
        return saveFile;
    }

    /** Returns the current file used for loading, including the legacy fallback when needed. */
    public Path getLoadFile() {
        return loadFile;
    }

    /**
     * Writes to a temporary sibling, then atomically replaces the save file.
     * If the filesystem cannot do an atomic replacement, saving fails safely.
     *
     * @param tasks the tasks to save
     * @throws IOException if writing or replacing the file fails
     */
    public void save(List<Task> tasks) throws IOException {
        Path target = saveFile.toAbsolutePath();
        Files.createDirectories(target.getParent());
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)
                && (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS) || !Files.isWritable(target))) {
            throw new AccessDeniedException(target.toString());
        }
        Path temporary = Files.createTempFile(target.getParent(), "bot67-", ".tmp");
        try {
            Files.write(temporary, tasks.stream().map(Task::toFileFormat).toList());
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            // Cleanup must not turn a successful replacement into a reported save failure.
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException | SecurityException ignored) {
                // An unused temporary file can be removed later; the save file is unaffected.
            }
        }
    }

    /**
     * Loads the entire file, accepting a missing file as an empty first session.
     * Rejects malformed records instead of silently discarding them on the next save.
     *
     * @return the tasks read from the save file
     * @throws IOException if the file cannot be read or contains an invalid record
     */
    public ArrayList<Task> load() throws IOException {
        Path fileToLoad = saveFile;
        if (!Files.exists(fileToLoad, LinkOption.NOFOLLOW_LINKS)
                && legacySaveFile != null && Files.exists(legacySaveFile, LinkOption.NOFOLLOW_LINKS)) {
            fileToLoad = legacySaveFile;
        }
        loadFile = fileToLoad;
        List<String> lines;
        try {
            lines = Files.readAllLines(fileToLoad);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (Bot67Exception | IllegalArgumentException e) {
                throw new IOException("Invalid saved task at line " + (i + 1) + ".", e);
            }
        }
        return tasks;
    }

    /** Validates every field using the same rules as commands before constructing a task. */
    private Task parseTask(String line) throws Bot67Exception {
        String[] parts = line.split("\\|", -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].strip();
            if (parts[i].isEmpty()) {
                throw new IllegalArgumentException("Empty saved field");
            }
        }
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid saved status or field count");
        }
        Parser parser = new Parser();
        Task task;
        if (parts.length == 3 && parts[0].equals("T")) {
            String command = parser.normalize("todo " + parts[2]);
            parser.requireText(command.substring(5));
            task = new Todo(command);
        } else if (parts.length == 4 && parts[0].equals("D")) {
            String command = parser.normalize("deadline " + parts[2] + " /by " + parts[3]);
            parser.requireValidDeadline(command);
            task = new Deadline(command);
        } else if (parts.length == 5 && parts[0].equals("E")) {
            String command = parser.normalize("event " + parts[2] + " /from " + parts[3] + " /to " + parts[4]);
            parser.requireValidEvent(command);
            task = new Event(command);
        } else {
            throw new IllegalArgumentException("Invalid saved task type or field count");
        }
        if (parts[1].equals("1")) {
            task.mark();
        }
        return task;
    }
}
