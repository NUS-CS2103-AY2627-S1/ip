package cassava.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles direct reads and writes from a data file.
 */
public class TaskFileIO {

    public static final String DATAPATH = "./data/tasks.txt";

    /**
     * Opens task data file as a path object,
     * or creates the data file if it does not exist.
     * @return Path object representing the data file.
     * @throws IOException
     */
    public static Path openOrCreateTaskFile() throws IOException {
        Path path = Paths.get(DATAPATH);
        Path parentDirectory = path.getParent();

        if (!Files.exists(parentDirectory)) {
            Files.createDirectories(parentDirectory);
        }

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        return path;
    }

    /**
     * Reads the entire data file and returns it.
     * @param path Path object representing the data file.
     * @return List of lines of text in the data file.
     * @throws IOException
     */
    public static List<String> readTasks(Path path) throws IOException {
        List<String> tasks = Files.readAllLines(path);
        return tasks;
    }

    /**
     * Writes over the entire data file.
     * @param path Path object representing the data file.
     * @param tasks List of lines of text to write to the data file.
     * @throws IOException
     */
    public static void writeTasks(Path path, List<String> tasks) throws IOException {
        Files.write(path, tasks);
    }

}
