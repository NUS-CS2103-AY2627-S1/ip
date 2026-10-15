package duke.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

/**
 * Handles saving tasks to disk and loading them when the chatbot starts.
 */
public class Storage {
    private static final String SEPARATOR = " | ";

    private final Path dataFile;

    /**
     * Creates a storage helper that reads from and writes to the given file path.
     *
     * @param filePath Path of the data file used to save tasks.
     */
    public Storage(String filePath) {
        dataFile = Paths.get(filePath);
    }

    /**
     * Loads saved tasks from the data file. If the file does not exist yet,
     * the chatbot starts with an empty task list.
     *
     * @return Tasks loaded from the data file.
     * @throws DukeException If the data file cannot be read or has invalid content.
     */
    public List<Task> loadTasks() throws DukeException {
        ArrayList<Task> loadedTasks = new ArrayList<>();

        if (!Files.exists(dataFile)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(dataFile);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.trim().isEmpty()) {
                    loadedTasks.add(parseTask(line, i + 1));
                }
            }
        } catch (IOException e) {
            throw new DukeException("I could not read the saved tasks.");
        }

        return loadedTasks;
    }

    /**
     * Saves the current tasks to the data file, creating the data folder first
     * if needed.
     *
     * @param tasks Tasks to save.
     * @throws DukeException If the data file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws DukeException {
        try {
            Path folder = dataFile.getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(dataFile, lines);
        } catch (IOException e) {
            throw new DukeException("I could not save the tasks.");
        }
    }

    /**
     * Converts one saved line into a task.
     */
    private static Task parseTask(String line, int lineNumber) throws DukeException {
        ArrayList<String> fields = splitFields(line);
        if (fields.size() < 3) {
            throw new DukeException("The saved task on line " + lineNumber + " is incomplete.");
        }

        String type = unescape(fields.get(0).trim(), lineNumber);
        boolean isDone = parseDoneStatus(fields.get(1).trim(), lineNumber);
        String description = unescape(fields.get(2).trim(), lineNumber);
        if (description.isEmpty()) {
            throw new DukeException("The saved task on line " + lineNumber + " has no description.");
        }

        Task task;
        if (type.equals("T") && fields.size() == 3) {
            task = new ToDo(description);
        } else if (type.equals("D") && fields.size() == 4) {
            String byText = unescape(fields.get(3).trim(), lineNumber);
            if (byText.isEmpty()) {
                throw new DukeException("The saved deadline on line " + lineNumber + " has no date.");
            }
            task = new Deadline(description, parseSavedDate(byText, lineNumber, "deadline"));
        } else if (type.equals("E") && fields.size() == 5) {
            String fromText = unescape(fields.get(3).trim(), lineNumber);
            String toText = unescape(fields.get(4).trim(), lineNumber);
            if (fromText.isEmpty() || toText.isEmpty()) {
                throw new DukeException("The saved event on line " + lineNumber + " has missing dates.");
            }
            task = new Event(
                    description,
                    parseSavedDate(fromText, lineNumber, "event"),
                    parseSavedDate(toText, lineNumber, "event"));
        } else {
            throw new DukeException("The saved task on line " + lineNumber + " has an invalid format.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns whether a saved status field represents a completed task.
     */
    private static boolean parseDoneStatus(String value, int lineNumber) throws DukeException {
        if (value.equals("1")) {
            return true;
        }
        if (value.equals("0")) {
            return false;
        }
        throw new DukeException("The saved task on line " + lineNumber + " has an invalid done status.");
    }

    /**
     * Converts a task into one line of saved text.
     */
    private static String formatTask(Task task) throws DukeException {
        String doneValue = task.isDone() ? "1" : "0";

        if (task instanceof ToDo) {
            return String.join(SEPARATOR, "T", doneValue, escape(task.getDescription()));
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return String.join(
                    SEPARATOR,
                    "D",
                    doneValue,
                    escape(deadline.getDescription()),
                    deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return String.join(
                    SEPARATOR,
                    "E",
                    doneValue,
                    escape(event.getDescription()),
                    event.getFrom().toString(),
                    event.getTo().toString());
        }

        throw new DukeException("I could not save an unknown task type.");
    }

    /**
     * Parses a date stored in the data file in yyyy-MM-dd format.
     */
    private static LocalDate parseSavedDate(String dateText, int lineNumber, String taskType) throws DukeException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new DukeException("The saved " + taskType + " on line " + lineNumber + " has an invalid date.");
        }
    }

    /**
     * Splits a saved line on separators while keeping escaped separators inside fields.
     */
    private static ArrayList<String> splitFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (isEscaped) {
                field.append(current);
                isEscaped = false;
            } else if (current == '\\') {
                field.append(current);
                isEscaped = true;
            } else if (current == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }

        fields.add(field.toString());
        return fields;
    }

    /**
     * Escapes characters that have special meaning in the save file.
     */
    private static String escape(String value) {
        StringBuilder escapedValue = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\') {
                escapedValue.append("\\\\");
            } else if (current == '|') {
                escapedValue.append("\\|");
            } else if (current == '\n') {
                escapedValue.append("\\n");
            } else {
                escapedValue.append(current);
            }
        }
        return escapedValue.toString();
    }

    /**
     * Reverses escaping used in the save file and rejects invalid escape sequences.
     */
    private static String unescape(String value, int lineNumber) throws DukeException {
        StringBuilder unescapedValue = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (isEscaped) {
                if (current == 'n') {
                    unescapedValue.append('\n');
                } else if (current == '\\' || current == '|') {
                    unescapedValue.append(current);
                } else {
                    throw new DukeException("The saved task on line " + lineNumber + " has an invalid escape.");
                }
                isEscaped = false;
            } else if (current == '\\') {
                isEscaped = true;
            } else {
                unescapedValue.append(current);
            }
        }

        if (isEscaped) {
            throw new DukeException("The saved task on line " + lineNumber + " has an invalid escape.");
        }

        return unescapedValue.toString();
    }
}
