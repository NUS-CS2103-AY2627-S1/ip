package megatron.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.Todo;

/** Loads and saves tasks in the application's data file. */
public class Storage {
    private final String filePath;
    /** Creates storage backed by the specified file. */
    public Storage(String filePath) { this.filePath = filePath; }

    /** Loads valid tasks from storage. */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                try {
                    tasks.add(parse(scanner.nextLine()));
                } catch (RuntimeException e) {
                    System.out.println("Sorry, I could not read one of your saved tasks.");
                }
            }
        } catch (FileNotFoundException | SecurityException e) {
            System.out.println("Sorry, I could not load your saved tasks.");
        }
        return tasks;
    }

    /** Saves all tasks in their display format. */
    public void save(ArrayList<Task> tasks) {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Task task : tasks) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Sorry, I could not save your tasks.");
        }
    }

    private Task parse(String line) {
        if (line.length() < 7 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[' || line.charAt(5) != ']') {
            throw new IllegalArgumentException();
        }
        boolean done = line.charAt(4) == 'X';
        String text = line.substring(7);
        Task task;
        if (line.startsWith("[T]")) {
            task = new Todo(text);
        } else if (line.startsWith("[D]")) {
            int i = text.lastIndexOf(" (by: ");
            if (i < 1 || !text.endsWith(")")) {
                throw new IllegalArgumentException();
            }
            task = new Deadline(text.substring(0, i), text.substring(i + 6, text.length() - 1));
        } else if (line.startsWith("[E]")) {
            int from = text.lastIndexOf(" (from: ");
            int to = text.lastIndexOf(" to: ");
            if (from < 1 || to < from || !text.endsWith(")")) {
                throw new IllegalArgumentException();
            }
            task = new Event(text.substring(0, from), text.substring(from + 8, to),
                    text.substring(to + 5, text.length() - 1));
        } else {
            throw new IllegalArgumentException();
        }
        if (done) {
            task.markAsDone();
        }
        return task;
    }
}
