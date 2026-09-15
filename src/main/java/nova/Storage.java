package nova;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads and writes the task list to disk.
 * This class owns everything about the on-disk format: where the file lives,
 * how a line is laid out, and how damaged lines are handled. Nothing else
 * should need to know that tasks are stored as "|"-separated text, so swapping
 * to another format means changing only this class.
 * Storage never prints; it reports problems by throwing or by exposing counts,
 * and leaves messaging to the caller.
 */
public class Storage {
    private final String filePath;
    private final File dataFile;

    /** Lines from the last load() that could not be parsed. */
    private int skippedLineCount;

    /**
     * Creates a Storage that reads and writes the file at the given path.
     *
     * @param filePath location of the data file, relative to the working directory.
     */
    /**
     * @param filePath location of the data file, relative to the working directory
     */
    Storage(String filePath) {
        this.filePath = filePath;
        this.dataFile = new File(filePath);
        this.skippedLineCount = 0;
    }

    /**
     * Reads the saved tasks from disk.
     * A file that does not exist yet means this is the first run, which is
     * normal: an empty list is returned and nothing is reported. Individual
     * lines that cannot be parsed are skipped and counted, so one damaged
     * entry never prevents the rest from loading.
     *
     * @return the tasks that were successfully read, never null.
     * @throws FileNotFoundException if the file exists but cannot be opened,
     *         for example because it is unreadable or is a directory. This is
     *         a real failure: the caller must not save over the file afterwards.
     */
    ArrayList<Task> load() throws FileNotFoundException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        this.skippedLineCount = 0;

        if (!this.dataFile.exists()) {
            return loadedTasks;
        }

        try (Scanner scanner = new Scanner(this.dataFile)) {
            while (scanner.hasNextLine()) {
                String dataLine = scanner.nextLine();

                if (dataLine.isBlank()) {
                    continue;
                }

                Task savedTask = Parser.parseDataLine(dataLine);
                if (savedTask == null) {
                    this.skippedLineCount++;
                } else {
                    loadedTasks.add(savedTask);
                }
            }
        }

        return loadedTasks;
    }

    /**
     * Rewrites the whole file so that it matches the given tasks.
     * Rewriting rather than appending is what lets edits and deletions be
     * saved, not just additions. The parent directory is created first
     * because FileWriter creates a missing file but never a missing directory.
     *
     * @param tasks the current task list.
     * @throws IOException if the file could not be written.
     */
    void save(TaskList tasks) throws IOException {
        File dataDirectory = this.dataFile.getParentFile();

        // getParentFile() is null when the path has no directory part.
        if (dataDirectory != null) {
            dataDirectory.mkdirs();
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(this.dataFile))) {
            for (int i = 0; i < tasks.size(); i++) {
                fileWriter.write(tasks.get(i).toDataString());
                fileWriter.newLine();
            }
        }
    }

    /**
     * Returns how many lines the last load could not parse, so the caller can
     * tell the user that some saved tasks were dropped.
     *
     * @return the number of unreadable lines.
     */
    int getSkippedLineCount() {
        return this.skippedLineCount;
    }

    /**
     * Returns the data file path, for naming the file in messages to the user.
     *
     * @return the path this Storage reads and writes.
     */
    String getFilePath() {
        return this.filePath;
    }
}
