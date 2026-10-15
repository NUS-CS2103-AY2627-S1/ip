package doe;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Loads validated task records and replaces the save file only after a complete write. */
public class Storage {
    private final Path filePath;
    /** A failed or partial load blocks saving to preserve the original data for recovery. */
    private String loadWarning = "";

    /** Creates storage at the supplied path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath).toAbsolutePath();
    }

    /** Returns a startup warning, including any invalid record line numbers. */
    public String getLoadWarning() {
        return loadWarning;
    }

    /** Loads valid records, preserving a damaged or unreadable source without overwriting it. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        List<String> invalidLines = new ArrayList<>();
        loadWarning = "";
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                try {
                    String[] items = line.split("\\|", -1);
                    if (items.length < 3 || !(items[1].equals("true") || items[1].equals("false"))) {
                        throw new IllegalArgumentException("invalid fields");
                    }
                    Task task;
                    if (items[0].equals("TODO") && items.length == 3) {
                        task = new Todo(items[2]);
                    } else if (items[0].equals("DEADLINE") && items.length == 4) {
                        task = new Deadline(items[2], items[3]);
                    } else if (items[0].equals("EVENT") && items.length == 4) {
                        task = new Event(items[2], items[3]);
                    } else {
                        throw new IllegalArgumentException("invalid task type or field count");
                    }
                    if (Boolean.parseBoolean(items[1])) {
                        task.markAsDone();
                    }
                    new TaskList(tasks).addTask(task);
                } catch (IllegalArgumentException | java.time.DateTimeException exception) {
                    invalidLines.add(Integer.toString(i + 1));
                }
            }
            if (!invalidLines.isEmpty()) {
                loadWarning = "Some tasks could not be loaded because lines "
                        + String.join(", ", invalidLines) + " in your task file have an invalid format.\n"
                        + "Your original file has not been changed, and saving is paused to prevent data loss.\n"
                        + "How to fix: correct or remove those lines in " + filePath + ", then restart Doe.";
            }
        } catch (NoSuchFileException exception) {
            // A missing file is expected on first use. A dangling link is a recovery problem.
            if (Files.isSymbolicLink(filePath)) {
                loadWarning = "Doe could not open your task file because its symbolic link points to a missing file.\n"
                        + "Saving is paused to prevent the link from being replaced.\n"
                        + "How to fix: restore the linked file or replace the broken link at " + filePath
                        + ", then restart Doe.";
            }
        } catch (IOException | SecurityException exception) {
            loadWarning = "Doe could not read your task file at " + filePath + ".\n"
                    + "Reason: " + exception.getMessage() + "\n"
                    + "Saving is paused to prevent data loss.\n"
                    + "How to fix: check that the path is a readable file and that Doe has permission to open it, "
                    + "then restart Doe.";
        }
        return tasks;
    }

    /**
     * Writes a temporary sibling before replacing the save file. Failure leaves changes in memory.
     *
     * @throws IllegalStateException if startup recovery is required
     * @throws UncheckedIOException if the new data cannot be saved
     */
    public void save(List<Task> currentTasks) {
        if (!loadWarning.isEmpty()) {
            throw new IllegalStateException(loadWarning);
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(filePath.getParent());
            temporaryFile = Files.createTempFile(filePath.getParent(), ".doe-", ".tmp");
            Files.write(temporaryFile, currentTasks.stream().map(Task::toFileFormat).toList());
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("could not save tasks: " + exception.getMessage(), exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Cleanup must not hide the original save failure.
                }
            }
        }
    }
}
