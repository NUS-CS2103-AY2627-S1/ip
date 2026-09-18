package duke;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes Furina's tasks in a small, human-readable text file. */
class TaskStorage {
    private static final Path FILE_PATH = Path.of("data", "duke.txt");

    /**
     * Loads all valid tasks from disk. A missing file is treated as an empty list.
     * Invalid records are ignored so one bad line does not prevent startup.
     *
     * @return the tasks found on disk
     */
    static ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
                try {
                    Task task = parse(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (IllegalArgumentException exception) {
                    System.err.println("    OOPS!!! Skipping an invalid saved task.");
                }
            }
        } catch (IOException exception) {
            System.err.println("    OOPS!!! I couldn't load the saved tasks.");
        }
        return tasks;
    }

    /** Saves the complete current task list, creating the data directory if needed. */
    static boolean save(List<Task> tasks) {
        assert tasks != null : "Task collection must not be null";
        try {
            Files.createDirectories(FILE_PATH.getParent());
            List<String> lines = tasks.stream()
                    .map(TaskStorage::format)
                    .toList();
            Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private static Task parse(String line) {
        if (line.isBlank()) {
            return null;
        }

        String[] fields = split(line);
        if (fields.length < 3) {
            throw new IllegalArgumentException();
        }

        TaskType type = switch (fields[0].trim()) {
            case "T" -> TaskType.TODO;
            case "D" -> TaskType.DEADLINE;
            case "E" -> TaskType.EVENT;
            default -> throw new IllegalArgumentException();
        };
        if (!(fields[1].trim().equals("0") || fields[1].trim().equals("1"))) {
            throw new IllegalArgumentException();
        }

        String description = fields[2].trim();
        if (description.isBlank()) {
            throw new IllegalArgumentException();
        }

        Task task;
        if (type == TaskType.TODO && fields.length == 3) {
            task = new Task(type, description, null, null, null);
        } else if (type == TaskType.DEADLINE && fields.length == 4
                && !fields[3].trim().isBlank()) {
            try {
                task = new Task(description, DateTimeParser.parse(fields[3].trim()));
            } catch (DateTimeParseException ignored) {
                task = new Task(type, description, fields[3].trim(), null, null);
            }
        } else if (type == TaskType.EVENT && fields.length == 5
                && !fields[3].trim().isBlank() && !fields[4].trim().isBlank()) {
            task = new Task(type, description, null, fields[3].trim(), fields[4].trim());
        } else {
            throw new IllegalArgumentException();
        }

        if (fields[1].trim().equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private static String format(Task task) {
        assert task != null : "Task to save must not be null";
        StringBuilder record = new StringBuilder();
        record.append(task.type.getSymbol()).append(" | ")
                .append(task.isDone ? "1" : "0").append(" | ")
                .append(escape(task.description));
        if (task.type == TaskType.DEADLINE) {
            String deadline = task.byDateTime == null
                    ? task.by
                    : task.byDateTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"));
            record.append(" | ").append(escape(deadline));
        } else if (task.type == TaskType.EVENT) {
            record.append(" | ").append(escape(task.from))
                    .append(" | ").append(escape(task.to));
        }
        return record.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static String[] split(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaped) {
                field.append(character);
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException();
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
}
