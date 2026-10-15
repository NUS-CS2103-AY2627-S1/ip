package bucket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the task list to a file and reads it back.
 * Each task is one line, e.g. "D | 0 | return book | 2019-10-15".
 */
public class Storage {
    /** Fixed path for the storage file, relative to the project root */
    private static final Path FILE_PATH = Path.of("data", "bucket.txt");

    /**
     * Returns the file the app saves to when no other is named.
     * Exposed so a caller can be pointed somewhere else, which is what lets tests
     * run against a scratch file instead of the real one.
     *
     * @return Default save file.
     */
    public static Path getDefaultPath() {
        return FILE_PATH;
    }

    /**
     * Writes every task to the file, one per line, creating the data folder
     * on the first run. A failure is reported but does not stop the program.
     *
     * @param items List to write out.
     */
    public static void save(TaskList items) {
        save(items, FILE_PATH);
    }

    /**
     * Writes every task to the given file, one per line.
     *
     * The path is a parameter so tests can write to a scratch file of their own
     * instead of the one the running app uses.
     *
     * @param items List to write out.
     * @param path File to write to.
     */
    public static void save(TaskList items, Path path) {
        // Bucket saves after every command, always passing its own field. A null would
        // mean the chatbot lost its list, and the loop below would throw part way
        // through, which is the one case that could truncate the save file.
        assert items != null : "save() needs a task list, not null";
        assert path != null : "save() needs a file to write to, not null";

        try {
            // The folder has to exist first - Files.write won't make it.
            // The file itself doesn't need checking, Files.write creates it if it's missing
            Files.createDirectories(path.getParent());

            ArrayList<String> lines = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                lines.add(items.get(i).toSaveString());
            }
            Files.write(path, lines);

        } catch (IOException e) {
            System.out.println("Couldn't save tasks to file - changes will be lost when the program exits.");
        }
    }

    /**
     * Reads the save file back into a task list. A missing or empty file is the
     * normal first-run case and gives an empty list rather than an error.
     * Lines that cannot be read are skipped so one bad line does not lose the rest.
     *
     * @return Tasks that were saved, or an empty list if there were none.
     */
    public static TaskList load() {
        return load(FILE_PATH);
    }

    /**
     * Reads the given save file back into a task list.
     *
     * The path is a parameter so tests can read a scratch file of their own instead
     * of the one the running app uses.
     *
     * @param path File to read from.
     * @return Tasks that were saved, or an empty list if there were none.
     */
    public static TaskList load(Path path) {
        assert path != null : "load() needs a file to read from, not null";

        TaskList items = new TaskList();

        // First run - the file isn't there yet - return an empty list
        if (!Files.exists(path)) {
            return items;
        }

        try {
            List<String> lines = Files.readAllLines(path);

            // File is there but there's nothing in it - return an empty list
            if (lines.isEmpty()) {
                return items;
            }

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                // Parse gives back null for a corrupted line, so that line just gets skipped
                Task task = parse(line);
                if (task != null) {
                    items.addItem(task);
                }
            }

        } catch (IOException e) {
            System.out.println("Couldn't read tasks from file - starting with an empty list.");
        }

        return items;
    }

    /**
     * Turns one saved line back into a task, or returns null if the line is
     * corrupted. Which constructor to call can only be decided from the type
     * letter, so this is an if-chain rather than polymorphism.
     *
     * @param line One line from the save file.
     * @return Task that line describes, or null if it cannot be read.
     */
    private static Task parse(String line) {
        // The | has to be escaped because split takes a regex, where | means "or"
        String[] values = line.split(" \\| ");

        if (values.length < 3) {
            return null;
        }

        String type = values[0];
        boolean isDone = values[1].equals("1");
        String name = values[2];

        // Each type has its own number of values, so a wrong count means it's corrupted
        Task task = null;
        try {
            if (type.equals("T") && values.length == 3) {
                task = new Todo(name);
            } else if (type.equals("D") && values.length == 4) {
                task = new Deadline(name, LocalDate.parse(values[3]));
            } else if (type.equals("E") && values.length == 5) {
                task = new Event(name, LocalDate.parse(values[3]), LocalDate.parse(values[4]));
            }
        } catch (DateTimeParseException e) {
            // The saved date isn't a real date, so the whole line counts as corrupted
            return null;
        }

        if (task != null) {
            task.setDone(isDone);
        }
        return task;
    }
}
