package ayre.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ayre.TaskList;
import ayre.exceptions.TaskLogCorruptedException;
import ayre.tasks.Task;

/**
 * Handles file IO, including reading the save file at the start of the program and loading that
 * information into the TaskList, as well as updating the file.
 * Relies on LiveTaskList to bind the file update to a TaskList mutation.
 */
public class Storage {

    // Named constants representing their index position in a well-formatted save file log
    private static final int IS_COMPLETE_FLAG_INDEX = 1;

    private final Path logPath;
    private final Path parentDirectory;

    /**
     * Initializes Path fields in Storage.
     *
     * @param path Relative path to where the save file should be created/accessed.
     */
    public Storage(String path) {
        this.logPath = Path.of(path);
        this.parentDirectory = logPath.getParent();
    }

    /**
     * Loads the save file from disk into a TaskList.
     * If the file does not exist, creates it and returns early.
     * Reads and processes the file line by line, dropping the line and logging a warning if issues are detected
     * with the contents.
     *
     * @return Record holding the resultant TaskList and the printout of any accumulated messages.
     */
    public LoadResult load() {
        if (!Files.exists(logPath)) {
            String warnings = this.createNewFile();
            return new LoadResult(new TaskList(), warnings);
        }
        try {
            LoadTasksResult result = this.loadTasks();
            TaskList tasks = result.tasks();
            List<String> warnings = result.warnings();
            if (!warnings.isEmpty()) {
                this.update(tasks); // force an update to clear corrupted data straight away
            }
            return new LoadResult(tasks, this.getWarningsAsString(warnings));
        } catch (IOException e) { // fatal error reading file, either perms changed or disk failure
            String warning = this.createLoadFailureWarning(e);
            return new LoadResult(new TaskList(), warning);
        }
    }

    /**
     * Updates the save file whenever it is mutated.
     *
     * @param tasks The post-update TaskList to be written into the save file.
     * @return An empty String if successful, or an error message to be appended to the result String of the command.
     */
    public String update(TaskList tasks) {
        try {
            Path tmpFile = Files.createTempFile(parentDirectory, "~$ayre", ".tmp");
            Files.write(tmpFile, tasks.toLog());
            try {
                // atomic rewrite of file to prevent save file tampering while process is running
                Files.move(tmpFile, logPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Files.deleteIfExists(tmpFile); // prevent Temp files from piling up if atomic move fails
                throw e;
            }
            return ""; // no issues, nothing to append
        } catch (IOException e) {
            // additional message appended to result message of executing the command
            return "\n<<Main System>> WARNING: MISSION LOG UPDATE FAILED; YOUR CHANGE WILL NOT BE SAVED TO DISK";
        }
    }

    /**
     * Creates the task log and returns the corresponding load message.
     *
     * @return A message describing successful log creation or the creation failure.
     */
    private String createNewFile() {
        StringBuilder warning = new StringBuilder();
        warning.append("~ First contact with Coral Collective established");
        try {
            this.createTaskLog();
        } catch (IOException e) {
            warning.append(e.getMessage())
                    .append("\n<<Main System>> WARNING: MISSION LOG CREATION FAILED; ")
                    .append("YOUR MISSION LIST WILL NOT BE SAVED TO DISK");
        }
        return warning.toString();
    }

    /**
     * Creates a new save file if not present.
     *
     * @throws IOException If unable to create the parent folder or file for some reason.
     */
    private void createTaskLog() throws IOException {
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.createFile(logPath);
    }

    /**
     * Builds the warning shown when the task log cannot be read.
     *
     * @param e The exception raised while reading the task log.
     * @return A user-facing warning describing the failed load operation.
     */
    private String createLoadFailureWarning(IOException e) {
        return e.getMessage()
                + "\n<<Main System>> WARNING: MISSION LOG FAILED TO LOAD; "
                + "YOUR MISSION LIST WILL NOT BE SAVED TO DISK";
    }

    /**
     * Groups the tasks recovered from the log with warnings found during parsing.
     *
     * @param tasks The successfully reconstructed tasks.
     * @param warnings The warnings produced for corrupted log lines.
     */
    private record LoadTasksResult(TaskList tasks, List<String> warnings) {}

    /**
     * Reads and parses every line in the task log.
     *
     * @return The reconstructed tasks and any warnings produced during parsing.
     * @throws IOException If the task log cannot be read.
     */
    private LoadTasksResult loadTasks() throws IOException {
        TaskList tasks = new TaskList();
        List<String> warnings = new ArrayList<>();
        List<String> linesList = this.loadLogAsList();
        for (int i = 0; i < linesList.size(); i++) {
            String[] logArgs = linesList.get(i).split("\\s+");
            try { // quietly handles errors to try to salvage anything that is intact
                Task task = LogParser.constructNewTask(i, logArgs); // goes through parsing and validation
                this.restoreCompletionStatus(task, logArgs);
                tasks.addTask(task);
            } catch (TaskLogCorruptedException e) {
                warnings.add(e.getMessage()); // add a warning but continue parsing the rest of the file
            }
        }
        return new LoadTasksResult(tasks, warnings);
    }

    /**
     * Reads the task log into a list while ensuring that the file stream is closed.
     *
     * @return The task log lines in their original order.
     * @throws IOException If the task log cannot be read.
     */
    private List<String> loadLogAsList() throws IOException {
        try (Stream<String> lines = Files.lines(logPath)) {
            return lines.toList();
        }
    }

    /**
     * Restores a task's completed state from its serialized completion flag.
     *
     * @param task The task whose completion state should be restored.
     * @param logArgs The tokenized log line containing the completion flag.
     */
    private void restoreCompletionStatus(Task task, String[] logArgs) {
        if (logArgs[IS_COMPLETE_FLAG_INDEX].equals("1")) {
            task.markComplete();
        }
    }

    /**
     * Converts the list of error messages into a readable, printable format.
     * Essentially a toString() for the list.
     *
     * @return A more visually readable printout of the list of warnings.
     */
    private String getWarningsAsString(List<String> warnings) {
        if (warnings.isEmpty()) {
            return "<<Main System: Mission Log Loaded Successfully>>";
        }
        StringBuilder warnString = new StringBuilder();
        for (String warning : warnings) {
            warnString.append(warning).append("\n");
        }
        return "<<Main System: Mission Log Corrupted>>\n" + warnString;
    }
}
