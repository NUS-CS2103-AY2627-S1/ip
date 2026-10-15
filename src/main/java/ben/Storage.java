package ben;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads and writes the task list to a plain-text file on disk.
 * <p>
 * Level-7: the file is line-based, one {@link Task#serialize()} line per
 * task. A missing file or missing parent folder is treated as "no tasks
 * yet" on load, and created on demand on save, so the chatbot works on a
 * fresh machine where nothing has been saved before.
 */
class Storage {
    private final Path file;

    Storage(String relativePath) {
        // Path.of splits on "/" and re-joins with the OS separator, so the
        // same string works on Windows, macOS and Linux.
        this.file = Path.of(relativePath);
    }

    /**
     * Loads the saved tasks, or an empty list if the file does not exist
     * yet. Individual corrupted lines are skipped with a warning printed
     * to the console rather than aborting the whole load.
     *
     * @throws BenException if the file exists but cannot be read
     */
    List<Task> load() throws BenException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new BenException("Could not read the save file (" + file + "). Starting with an empty list.");
        }
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(Task.deserialize(line.trim()));
            } catch (BenException e) {
                System.out.println(e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Overwrites the data file with the current task list, creating the
     * parent folder first if it is not there yet.
     * <p>
     * A-MoreErrorHandling: a failure (e.g. the folder is not writable, or
     * disk space ran out) is reported back to the caller instead of just
     * being printed to the console, so the GUI user actually sees it
     * rather than the command silently appearing to succeed.
     *
     * @throws BenException if the file could not be written
     */
    void save(TaskList tasks) throws BenException {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.asList().stream()
                    .map(Task::serialize)
                    .collect(Collectors.toList());
            Files.write(file, lines);
        } catch (IOException e) {
            throw new BenException("Could not save tasks to " + file + " (" + e.getMessage() + ")");
        }
    }
}
