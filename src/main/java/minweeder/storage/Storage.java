package minweeder.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import minweeder.exception.MinweederException;
import minweeder.task.Deadline;
import minweeder.task.Event;
import minweeder.task.Loan;
import minweeder.task.LoanType;
import minweeder.task.Task;
import minweeder.task.TaskList;
import minweeder.task.Todo;

/**
 * Persists tasks to, and loads them from, a save file on disk.
 */
public class Storage {
    private static final Path FILE_PATH = Paths.get("data", "minweeder.txt");

    private int skippedLineCount = 0;

    /**
     * Writes every task in the list to the save file, overwriting its previous contents.
     *
     * @param tasks the tasks to save.
     * @throws MinweederException if the save file could not be written.
     */
    public void save(TaskList tasks) throws MinweederException {
        try {
            Files.createDirectories(FILE_PATH.getParent());

            List<String> lines = tasks.stream()
                    .map(Task::toFileString)
                    .collect(Collectors.toList());

            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new MinweederException("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Reads tasks from the save file, if it exists. Lines that cannot be
     * parsed are skipped and counted, retrievable via {@link #getSkippedLineCount()}.
     *
     * @return the loaded tasks, or an empty list if no save file exists.
     * @throws MinweederException if the save file could not be read.
     */
    public TaskList load() throws MinweederException {
        TaskList tasks = new TaskList();
        skippedLineCount = 0;
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task == null) {
                    skippedLineCount++;
                } else {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new MinweederException("I couldn't read your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Returns the number of lines skipped during the most recent {@link #load()}
     * because they could not be parsed into a task.
     *
     * @return the number of skipped lines.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Parses a single line of the storage file into a {@link Task}.
     *
     * @param line a "|"-separated line read from the storage file.
     * @return the parsed task, or null if the line is malformed and should be skipped.
     */
    private static Task parseTask(String line) {
        String[] parts = line.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        if (parts.length < 3 || parts[2].isEmpty()) {
            return null;
        }
        Task task = parseTaskBody(parts);
        if (task == null) {
            return null;
        }
        return applyMarkedStatus(task, parts[1]);
    }

    /**
     * Parses the type-specific fields of a task (everything after the "type"
     * and "marked" columns), dispatching to the parser for that type.
     *
     * @param parts the trimmed "|"-separated fields of the storage line.
     * @return the parsed task, or null if the fields are malformed.
     */
    private static Task parseTaskBody(String[] parts) {
        switch (parts[0]) {
            case "T":
                return new Todo(parts[2]);
            case "D":
                return parseDeadline(parts);
            case "E":
                return parseEvent(parts);
            case "L":
                return parseLoan(parts);
            default:
                return null;
        }
    }

    /**
     * Parses a deadline's fields.
     *
     * @param parts the trimmed "|"-separated fields, where parts[2] is the description
     *              and parts[3] is the "by" date-time in ISO-8601 format.
     * @return the parsed deadline, or null if the fields are missing or malformed.
     */
    private static Task parseDeadline(String[] parts) {
        if (parts.length < 4 || parts[3].isEmpty()) {
            return null;
        }
        try {
            LocalDateTime by = LocalDateTime.parse(parts[3]);
            return new Deadline(parts[2], by);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses an event's fields.
     *
     * @param parts the trimmed "|"-separated fields, where parts[2] is the description,
     *              parts[3] is the "from" text, and parts[4] is the "to" text.
     * @return the parsed event, or null if any field is missing or empty.
     */
    private static Task parseEvent(String[] parts) {
        if (parts.length < 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
            return null;
        }
        return new Event(parts[2], parts[3], parts[4]);
    }

    /**
     * Parses a loan's fields.
     *
     * @param parts the trimmed "|"-separated fields, where parts[2] is the other party's name,
     *              parts[3] is the {@link LoanType} name, and parts[4] is the amount.
     * @return the parsed loan, or null if any field is missing or malformed.
     */
    private static Task parseLoan(String[] parts) {
        if (parts.length < 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
            return null;
        }
        try {
            LoanType type = LoanType.valueOf(parts[3]);
            double amount = Double.parseDouble(parts[4]);
            return new Loan(parts[2], amount, type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Applies the "marked" column to a freshly-parsed task.
     *
     * @param task the task to update.
     * @param markedColumn "1" if the task is marked done, "0" if not.
     * @return the same task, or null if {@code markedColumn} is neither "1" nor "0".
     */
    private static Task applyMarkedStatus(Task task, String markedColumn) {
        if (markedColumn.equals("1")) {
            task.mark();
        } else if (!markedColumn.equals("0")) {
            return null;
        }
        return task;
    }
}
