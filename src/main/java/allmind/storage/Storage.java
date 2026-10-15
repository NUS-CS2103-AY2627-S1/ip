package allmind.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import allmind.model.Task;
import allmind.parser.InvalidCommandException;
import allmind.parser.Parser;

/** Loads tasks from and saves tasks to the application's task file. */
public class Storage {
    private final Path taskFile;

    /** Creates storage backed by the file at the given path. */
    public Storage(String filePath) {
        taskFile = Path.of(filePath);
    }

    /**
     * Loads all non-empty task records from the file.
     * Creates an empty file when the file does not exist.
     */
    public ArrayList<Task> load(Parser parser) throws IOException, InvalidCommandException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(taskFile)) {
            Files.createFile(taskFile);
            return tasks;
        }

        List<String> lines = Files.readAllLines(taskFile);
        for (String line : lines) {
            if (!line.isBlank()) {
                tasks.add(parser.fromFileFormat(line));
            }
        }
        return tasks;
    }

    /** Saves every task in the list using the task file format. */
    public void save(List<Task> tasks, Parser parser) throws IOException {
        List<String> lines = tasks.stream()
                .map(parser::toFileFormat)
                .collect(Collectors.toList());
        Files.write(taskFile, lines);
    }
}
