package elsa.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import elsa.ElsaException;
import elsa.task.Task;
import elsa.task.TaskFormat;
import elsa.task.TaskList;

/**
 * Keeps the task list on the hard disk so that it outlives a single run of the
 * chatbot: {@link #save} writes the list after every change, and {@link #load}
 * reads it back when the chatbot starts.
 *
 * <p>This class is about the file itself: whether it is there, creating the
 * folder it sits in, reading its lines and writing them back. What one of those
 * lines means is {@link TaskFormat}'s business, so a change to the format is
 * made there and a change to how the file is handled is made here.
 */
public class Storage {
    /**
     * The file this Storage looks after, as the caller wrote it, for example
     * "data/elsa.txt". Kept as text as well as a Path so that messages naming the
     * file read the same on every operating system: Path.toString() would use the
     * separator of whichever one is running.
     */
    private final String name;

    /**
     * Where the task list is kept. The path given is relative, so it is resolved
     * against the folder the program is run from rather than naming one computer's
     * drive, and Path.of turns it into a path the current operating system
     * understands, so the same code works on Windows and on macOS.
     */
    private final Path filePath;

    /**
     * What a load produced: the tasks that could be read, and one message for
     * each line that could not be. Both are needed, because a file with a bad
     * line in it still has good lines worth keeping.
     *
     * <p>This is a record: a class whose only job is to hold a few values
     * together. Java writes the constructor and the tasks() and problems()
     * accessor methods from this one declaration.
     *
     * @param tasks    the tasks read from the file, in the order they appear
     * @param problems one message per line that could not be understood
     */
    public record LoadResult(TaskList tasks, ArrayList<String> problems) {
    }

    /**
     * Creates a store that keeps the tasks in one named file.
     *
     * <p>The file is named by the caller rather than fixed here, so that the one
     * place deciding where the tasks live is the program's starting point, and so
     * that a test could point a Storage at a file of its own. Write the path with
     * forward slashes: every operating system accepts them.
     *
     * @param filePath where to keep the tasks, relative to where the program is run
     */
    public Storage(String filePath) {
        this.name = filePath;
        this.filePath = Path.of(filePath);
        assert this.filePath.getParent() != null
                : "the data file needs a folder for save() to create: " + filePath;
    }

    /**
     * Returns the name of the data file, so that messages to the user can say
     * where the tasks are kept without other classes knowing the path itself.
     *
     * @return the path of the data file as text, such as "data/elsa.txt"
     */
    public String getFileName() {
        return name;
    }

    /**
     * Writes the whole task list to the data file, one task per line.
     * The file is replaced each time rather than appended to, so what is on the
     * disk always matches the list in memory exactly, even after a deletion.
     *
     * @param tasks the tasks to save, in the order they appear in the list
     * @throws ElsaException if the file or its folder could not be written
     */
    public void save(TaskList tasks) throws ElsaException {
        List<String> lines = IntStream.range(0, tasks.size())
                .mapToObj(i -> tasks.get(i).toSaveFormat())
                .toList();

        try {
            // The data folder is not part of the repository, so create it on the
            // first save. createDirectories does nothing if it already exists.
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines);
        } catch (IOException e) {
            // Rethrown as an ElsaException so the chatbot reports it the same way
            // as any other problem, instead of a stack trace ending the session.
            // The reason is included because a full disk, a folder that is really
            // a file, and a read-only file all arrive here and need telling apart.
            throw new ElsaException("I could not save your tasks to " + getFileName()
                    + ". The reason given was: " + e);
        }
    }

    /**
     * Reads back the tasks saved by an earlier run.
     * A missing file is not an error: it simply means this is the first run,
     * so an empty result is returned and the file is made by the first save.
     *
     * <p>A line that cannot be understood is left out and described in the
     * result, rather than the whole file being abandoned, so that one damaged
     * line cannot cost the user every other task they had saved.
     *
     * @return the tasks that could be read, and a message per line that could not
     * @throws ElsaException if the file exists but could not be read at all
     */
    public LoadResult load() throws ElsaException {
        if (!Files.exists(filePath)) {
            return new LoadResult(new TaskList(), new ArrayList<>());
        }
        return decodeLines(readLines());
    }

    /**
     * Returns every line of the data file, in the order they appear in it.
     *
     * @return the lines of the file
     * @throws ElsaException if the file could not be read
     */
    private List<String> readLines() throws ElsaException {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            // Unreadable, a folder rather than a file, or not text at all:
            // nothing can be salvaged, so this is reported as a failure.
            throw new ElsaException("I could not read your saved tasks from "
                    + getFileName() + ". The reason given was: " + e);
        }
    }

    /**
     * Turns the lines of the data file into tasks, keeping a message for each
     * line that could not be understood.
     *
     * <p>Nothing here touches the disk, which is why it is static: the lines have
     * been read by the time it is called, and what one of them means is decided
     * by the format rather than by this class.
     *
     * @param lines the lines read from the file
     * @return the tasks the lines describe, and a message per line that failed
     */
    private static LoadResult decodeLines(List<String> lines) {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> problems = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // A blank line carries no task, so skip it rather than complain.
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(TaskFormat.decode(line));
            } catch (ElsaException e) {
                // Line numbers start at 1 for the user, as they do in an editor.
                problems.add("Line " + (i + 1) + ": " + e.getMessage());
            }
        }
        // Wrapped only at the end, so that the reading above works with a plain
        // list and the caller still receives the list in the form it will use it.
        return new LoadResult(new TaskList(tasks), problems);
    }

}
