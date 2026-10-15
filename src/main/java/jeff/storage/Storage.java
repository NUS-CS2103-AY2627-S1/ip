package jeff.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.Todo;

/**
 * Reads tasks from, and writes tasks to, a fixed file on disk, so that the
 * task list persists between runs of the chatbot.
 */
public class Storage {
    private final Path filePath;

    /**
     * Set by {@link #load()} to describe anything that went wrong on the most recent
     * call (an unreadable file, or corrupted lines that had to be skipped), or left
     * empty if it loaded cleanly. {@link jeff.Jeff} surfaces this to the user via
     * {@link jeff.Jeff#getWelcomeMessage()} instead of it only reaching a console
     * nobody may be looking at (e.g. when running the GUI).
     */
    private Optional<String> lastLoadWarning = Optional.empty();

    /**
     * Creates a Storage bound to the given save file path (which need not exist yet).
     *
     * @param filePath path (relative or absolute) of the file to load from and save to.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads previously saved tasks from disk. Returns an empty list if the save file
     * does not exist yet (e.g. on first run). Any line that is corrupted or unreadable
     * is skipped, so a single bad line does not prevent the rest of the file from
     * loading; call {@link #getLastLoadWarning()} afterwards to find out whether that
     * happened.
     */
    public List<Task> load() {
        lastLoadWarning = Optional.empty();
        if (!Files.exists(filePath)) {
            return List.of();
        }
        try {
            List<Task> parsedLines = Files.readAllLines(filePath).stream()
                    .map(this::parseLine)
                    .collect(Collectors.toList());
            long corruptedCount = parsedLines.stream().filter(Objects::isNull).count();
            if (corruptedCount > 0) {
                lastLoadWarning = Optional.of(corruptedCount
                        + " line(s) of your saved tasks were corrupted and had to be skipped.");
            }
            return parsedLines.stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            lastLoadWarning = Optional.of("Could not read your saved tasks (" + e.getMessage()
                    + "). Starting with an empty list.");
            return List.of();
        }
    }

    /**
     * Describes what went wrong on the most recent {@link #load()} call, or is empty
     * if it loaded every task cleanly (or hasn't been called yet).
     */
    public Optional<String> getLastLoadWarning() {
        return lastLoadWarning;
    }

    /**
     * Parses a single save-file line (see {@link Task#toSaveFormat()} for the
     * format) into the matching task, or returns {@code null} if the line is
     * corrupted or its type letter is unrecognized.
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        try {
            boolean isDone = parts[1].equals("1");
            String description = parts[2];
            Task task;
            switch (parts[0]) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    task = new Deadline(description, parts[3]);
                    break;
                case "E":
                    task = new Event(description, parts[3], parts[4]);
                    break;
                default:
                    return null;
            }
            // Every case above either assigns task or breaks out of the method entirely
            // (the default case returns null), so task can never be null here. If a future
            // case is added that forgets to assign task, or the switch is refactored to
            // fall through incorrectly, this catches that bug immediately instead of
            // letting a null task silently reach markAsDone() below.
            assert task != null : "task should have been assigned by one of the cases above";
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Writes the given tasks to disk, one per line, creating the save file's parent
     * folder first if it does not already exist.
     *
     * @return empty on success, or a description of what went wrong (e.g. the disk is
     *     full, or the save file's permissions were changed) so the caller can tell the
     *     user their change was not actually persisted, instead of the failure only
     *     reaching a console nobody may be looking at.
     */
    public Optional<String> save(List<Task> tasks) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            String content = tasks.stream()
                    .map(task -> task.toSaveFormat() + System.lineSeparator())
                    .collect(Collectors.joining());
            Files.writeString(filePath, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of("Could not save your tasks (" + e.getMessage() + ").");
        }
    }
}
