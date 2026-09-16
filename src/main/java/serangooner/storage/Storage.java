package serangooner.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import serangooner.SerangoonerException;
import serangooner.task.Deadline;
import serangooner.task.Event;
import serangooner.task.Task;
import serangooner.task.Todo;

/**
 * Reads the task list from the hard disk and writes it back again.
 * The save file holds one task per line, in the format produced by
 * {@link Task#toSaveFormat()} and read back by each task type's own
 * factory. A line that cannot be understood is skipped rather than
 * aborting the whole load, so one damaged line never costs the user the
 * rest of the list.
 * Skipped lines would vanish the next time the list is saved, so a file
 * that could not be read in full is first copied aside, and is never saved
 * over if that copy cannot be made.
 */
public class Storage {
    private static final String BACKUP_SUFFIX = ".bak";

    private final Path filePath;
    private boolean canOverwrite = true;

    /**
     * Constructs storage backed by the given file.
     *
     * @param filePath Relative path of the file that tasks are read from and written to.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the tasks held in the save file.
     * A missing file is treated as an empty list, since that is simply the
     * first run of the program. A file that cannot be read is not fatal
     * either, so the reason is reported in the result rather than thrown.
     * A file that cannot be read in full is backed up before anything is
     * saved over it.
     *
     * @return Tasks that were read, the number of lines skipped, the reason
     *         the file could not be read, and what became of its backup.
     */
    public LoadResult load() {
        if (!Files.exists(filePath)) {
            return new LoadResult(List.of(), 0, "", "");
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException exception) {
            return new LoadResult(List.of(), 0,
                    "couldn't read " + filePath + ", so we're starting fresh", backUp());
        }

        List<String> taskLines = lines.stream()
                .filter(line -> !line.isBlank())
                .toList();
        List<Task> tasks = taskLines.stream()
                .map(Storage::parseTask)
                .flatMap(Optional::stream)
                .toList();
        int skippedLineCount = taskLines.size() - tasks.size();
        assert skippedLineCount >= 0 : "a line of the file yields at most one task";
        String backupMessage = skippedLineCount > 0 ? backUp() : "";
        return new LoadResult(tasks, skippedLineCount, "", backupMessage);
    }

    /**
     * Writes the given tasks to the save file, replacing whatever it held before.
     * Any missing parent directory is created first.
     *
     * @param tasks Tasks to write, in the order they should be read back.
     * @throws SerangoonerException If the file cannot be written, or holds
     *         tasks that could not be read and could not be backed up.
     */
    public void save(List<Task> tasks) {
        if (!canOverwrite) {
            throw new SerangoonerException("i won't save over " + filePath
                    + " since i couldn't back it up :( your change is only in memory");
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, tasks.stream().map(Task::toSaveFormat).toList());
        } catch (IOException exception) {
            throw new SerangoonerException("couldn't save your list to " + filePath
                    + " :( your change is only in memory", exception);
        }
    }

    /**
     * Copies the save file to a backup beside it, replacing any earlier backup.
     * If the copy cannot be made, saving over the file is refused from then
     * on, so that what could not be read is never lost.
     *
     * @return What to tell the user about the backup, or an empty string when
     *         the save file is not a regular file and holds nothing to back up.
     */
    private String backUp() {
        if (!Files.isRegularFile(filePath)) {
            return "";
        }

        Path backupPath = filePath.resolveSibling(filePath.getFileName() + BACKUP_SUFFIX);
        try {
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return "i kept a copy of the original at " + backupPath;
        } catch (IOException exception) {
            canOverwrite = false;
            return "i couldn't back it up to " + backupPath + ", so i won't save over it";
        }
    }

    /**
     * Returns the task encoded by the given line of the save file.
     * The type code decides which task type is asked to read the line, and
     * that type alone decides whether the remaining fields make sense.
     *
     * @param line Single non-blank line read from the save file.
     * @return Task the line describes, or nothing if it cannot be read.
     */
    private static Optional<Task> parseTask(String line) {
        assert !line.isBlank() : "load() skips blank lines, so a line here can name a task type";
        String[] fields = line.split(Pattern.quote(Task.SAVE_DELIMITER));
        // A line made only of delimiters splits into no fields at all.
        if (fields.length == 0) {
            return Optional.empty();
        }
        return switch (fields[Task.SAVE_INDEX_TYPE]) {
            case Todo.SAVE_CODE -> Todo.parseSaveFields(fields);
            case Deadline.SAVE_CODE -> Deadline.parseSaveFields(fields);
            case Event.SAVE_CODE -> Event.parseSaveFields(fields);
            default -> Optional.empty();
        };
    }

    /**
     * Holds the outcome of reading the save file.
     *
     * @param tasks Tasks that were read successfully.
     * @param skippedLineCount Number of lines that could not be understood.
     * @param errorMessage Reason the file could not be read, or an empty
     *         string when it was read.
     * @param backupMessage What became of the backup of a file that could not
     *         be read in full, or an empty string when none was needed.
     */
    public record LoadResult(List<Task> tasks, int skippedLineCount, String errorMessage,
            String backupMessage) {
    }
}
