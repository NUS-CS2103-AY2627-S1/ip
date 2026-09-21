package maggigorengayam.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import maggigorengayam.task.Deadline;
import maggigorengayam.task.Event;
import maggigorengayam.task.Task;
import maggigorengayam.task.ToDo;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;
import maggigorengayam.util.DateTimeUtil;

/**
 * Loads tasks from, and saves tasks to, a plain-text file on disk so the
 * task list survives between runs of the program.
 */
public class Storage {
    private final String filePath;

    /** Points this Storage at the given file path (not read/written until {@link #load()}/{@link #save}). */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * The result of a {@link #load()} call: the tasks that were
     * successfully parsed, plus a count of lines that were skipped because
     * they could not be parsed (e.g. the file was hand-edited into an
     * invalid state). Callers can use {@code skippedLineCount} to warn the
     * user that some previously-saved tasks were silently lost, instead of
     * that data loss going completely unnoticed.
     */
    public static class LoadResult {
        public final List<Task> tasks;
        public final int skippedLineCount;

        /** Pairs the successfully-parsed tasks with how many lines were skipped. */
        public LoadResult(List<Task> tasks, int skippedLineCount) {
            this.tasks = tasks;
            this.skippedLineCount = skippedLineCount;
        }
    }

    /**
     * Loads tasks from the data file. A missing file (e.g. the very first
     * run) is not an error: it just means there is nothing to load yet, so
     * an empty result is returned. A line that cannot be parsed is skipped
     * rather than failing the whole load, so a single bad line doesn't lose
     * every other saved task; the number of lines skipped is reported back
     * via {@link LoadResult#skippedLineCount} rather than being hidden.
     *
     * @throws IOException if the file exists but could not be read (e.g.
     *         it is actually a directory, or permissions deny access) -
     *         this is distinct from "no file yet", which is not an error.
     */
    public LoadResult load() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new LoadResult(new ArrayList<>(), 0);
        }
        // Each line becomes either a successfully-parsed Task or null (unparseable);
        // partitioningBy splits them into the two groups in a single pass, with the
        // null/non-null test doubling as the "did this line parse?" check.
        Map<Boolean, List<Task>> parsedByIsSkipped = Files.readAllLines(file.toPath()).stream()
                .filter(line -> !line.isBlank())
                .map(this::parseLine)
                .collect(Collectors.partitioningBy(Objects::isNull));
        List<Task> tasks = parsedByIsSkipped.get(false);
        int skippedLineCount = parsedByIsSkipped.get(true).size();
        return new LoadResult(tasks, skippedLineCount);
    }

    /**
     * The result of {@link #loadOrEmpty}: the {@link TaskList} to start the
     * program with, plus a one-time startup message to show about it, or
     * {@code null} if loading went by without anything worth mentioning
     * (the common case).
     */
    public static class StartupResult {
        public final TaskList tasks;
        public final String message;

        /** Pairs the starting task list with the (possibly {@code null}) message to show about it. */
        public StartupResult(TaskList tasks, String message) {
            this.tasks = tasks;
            this.message = message;
        }
    }

    /**
     * Loads tasks for program startup and turns a load failure or skipped
     * lines into a ready-to-show message via {@code ui}, instead of making
     * every entry point (the CLI's {@code main}, the GUI's
     * {@code MaggiGorengAyamBot}) repeat the same load/catch/warn sequence
     * itself. On a hard failure ({@link #load()} throwing {@link IOException}),
     * the returned tasks are an empty list rather than the failure propagating,
     * since starting empty is preferable to the program not starting at all.
     */
    public StartupResult loadOrEmpty(Ui ui) {
        try {
            LoadResult result = load();
            TaskList tasks = new TaskList(result.tasks);
            String message = result.skippedLineCount > 0 ? ui.showLoadWarning(result.skippedLineCount) : null;
            return new StartupResult(tasks, message);
        } catch (IOException e) {
            return new StartupResult(new TaskList(), ui.showLoadingError());
        }
    }

    /**
     * Parses one save-file line ("T"/"D"/"E" followed by its
     * {@code |}-separated fields) back into a {@link Task}, or returns
     * {@code null} if the type letter, field count, or a date value is
     * invalid - the caller counts a {@code null} as a skipped line rather
     * than failing the whole load.
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        Task task;
        switch (type) {
            case "T":
                if (parts.length != 3) {
                    return null;
                }
                task = new ToDo(description);
                break;
            case "D":
                if (parts.length != 4) {
                    return null;
                }
                try {
                    DateTimeUtil.ParsedDateTime by = DateTimeUtil.parse(parts[3]);
                    task = new Deadline(description, by.date, by.time);
                } catch (DateTimeParseException e) {
                    return null;
                }
                break;
            case "E":
                if (parts.length != 5) {
                    return null;
                }
                try {
                    DateTimeUtil.ParsedDateTime from = DateTimeUtil.parse(parts[3]);
                    DateTimeUtil.ParsedDateTime to = DateTimeUtil.parse(parts[4]);
                    task = new Event(description, from.date, from.time, to.date, to.time);
                } catch (DateTimeParseException e) {
                    return null;
                }
                break;
            default:
                return null;
        }
        // Every case above either returns null (unparseable line) or assigns task;
        // reaching here means a case fell through without doing either, which is a bug.
        assert task != null : "task should have been assigned or this method should have returned null by now";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves the given tasks to the data file, overwriting whatever was
     * there before. Creates the parent directory (e.g. "data/") first if
     * it does not exist yet.
     */
    public void save(List<Task> tasks) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        String content = tasks.stream()
                .map(task -> task.toSaveFormat() + System.lineSeparator())
                .collect(Collectors.joining());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
