package hu9o.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu9o.parser.Parser;
import hu9o.task.TaskList;
import hu9o.ui.Ui;

/**
 * Reads and writes the task list on disk.
 *
 * <p>{@code Storage} knows where the save file lives and how each task is
 * serialized line by line, but it delegates the actual text-to-task conversion
 * to {@link Parser} and all progress messages to {@link Ui}.
 */
public class Storage {
    /** Location of the task file, relative to the directory where Hu9o is run. */
    private static final Path TASK_DATA_PATH = Path.of("data", "taskData.txt");

    private final Parser parser;
    private final Ui ui;

    /**
     * Creates a storage helper.
     *
     * @param parser used to rebuild a task from each saved record.
     * @param ui     used to show saving progress.
     */
    public Storage(Parser parser, Ui ui) {
        // Both collaborators are wired up by Hu9o; a null here is a construction bug.
        assert parser != null : "Storage needs a parser to rebuild saved tasks";
        assert ui != null : "Storage needs a UI to show saving progress";
        this.parser = parser;
        this.ui = ui;
    }

    /**
     * Loads every saved task record into the given task list.
     * The file stores one pipe-delimited task record per line.
     *
     * @param tasks the list to populate.
     */
    public void loadTasks(TaskList tasks) {
        if (Files.notExists(TASK_DATA_PATH)) {
            // No save file yet (e.g. first run, or the data folder went missing) --
            // create an empty one straight away so it exists on disk even before
            // the next save, then start with an empty list.
            createEmptyFile(TASK_DATA_PATH);
            return;
        }
        try (Stream<String> lines = Files.lines(TASK_DATA_PATH)) {
            lines.map(parser::parseTask)
                    .filter(Objects::nonNull) // drop lines whose type code was unknown
                    .forEach(tasks::addTask);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Creates an empty file, and its parent directory if needed, so a path
     * that could not be found exists on disk from that point on. Failures
     * are logged, not thrown, since a missing save file is not fatal.
     *
     * @param path the file to create.
     */
    private void createEmptyFile(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves every task as a pipe-delimited record, one per line, so it can be
     * loaded on the next run.
     *
     * @param tasks the tasks to persist.
     */
    public void dumpTasks(TaskList tasks) {
        // One record per line, each line terminated by '\n' (so an empty list
        // produces an empty file); joining() then concatenates the pieces.
        String data = tasks.stream()
                .map(task -> task.compressionString() + "\n")
                .collect(Collectors.joining());

        try {
            Files.createDirectories(TASK_DATA_PATH.getParent()); // make ./data/ if it is absent
            Files.writeString(TASK_DATA_PATH, data);
            ui.showSaving();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
