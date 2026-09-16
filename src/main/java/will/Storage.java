package will;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import will.task.Deadline;
import will.task.Event;
import will.task.Task;
import will.task.TaskType;
import will.task.Todo;

/**
 * Deals with loading tasks from the save file and saving tasks to it.
 * The save file format is pipe-delimited, one task per line, e.g.
 * "T | 1 | read book" or "D | 0 | return book | 2019-10-15".
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a Storage backed by the given file.
     *
     * @param filePath Where the task list is loaded from and saved to.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage requires a non-null file path";
        this.filePath = filePath;
    }

    /**
     * Loads the task list from disk. If the file doesn't exist yet (e.g.
     * this is the first time the program has ever been run), returns an
     * empty list rather than treating that as an error. A line that
     * fails to parse is skipped with a warning printed via ui instead of
     * aborting the whole load, so one corrupted line doesn't cost the
     * user every other saved task.
     */
    public ArrayList<Task> load(Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseSavedTask(line));
                } catch (WillException e) {
                    ui.showMessage("OOPS!!! Skipping a corrupted line in the data file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            ui.showMessage("OOPS!!! I couldn't load your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Writes the given task list to disk, one task per line in
     * Task#toSaveFormat(). Creates the parent folder first if it
     * doesn't exist yet, so this works on a fresh checkout where the
     * folder has never been created. On failure, throws a WillException
     * rather than crashing, so a save error is just another "OOPS!!!"
     * message and the session keeps running.
     */
    public void save(ArrayList<Task> tasks) throws WillException {
        try {
            Files.createDirectories(filePath.getParent());
            String content = tasks.stream()
                    .map(task -> task.toSaveFormat() + System.lineSeparator())
                    .collect(Collectors.joining());
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new WillException("I couldn't save your tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Parses one line of the data file (Task#toSaveFormat()'s format)
     * back into the matching Task subclass.
     */
    private Task parseSavedTask(String line) throws WillException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new WillException("\"" + line + "\" doesn't have enough fields.");
        }
        String typeSymbol = parts[0];
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new WillException("\"" + line + "\" has an invalid done flag (expected 0 or 1).");
        }
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        if (description.isBlank()) {
            throw new WillException("\"" + line + "\" has an empty description.");
        }

        Task task;
        if (typeSymbol.equals(TaskType.TODO.getSymbol())) {
            task = new Todo(description);
        } else if (typeSymbol.equals(TaskType.DEADLINE.getSymbol())) {
            if (parts.length < 4 || parts[3].isBlank()) {
                throw new WillException("\"" + line + "\" is missing its /by field.");
            }
            task = new Deadline(description, parts[3]);
        } else if (typeSymbol.equals(TaskType.EVENT.getSymbol())) {
            if (parts.length < 5 || parts[3].isBlank() || parts[4].isBlank()) {
                throw new WillException("\"" + line + "\" is missing its /from or /to field.");
            }
            task = new Event(description, parts[3], parts[4]);
        } else {
            throw new WillException("\"" + typeSymbol + "\" isn't a recognized task type.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
