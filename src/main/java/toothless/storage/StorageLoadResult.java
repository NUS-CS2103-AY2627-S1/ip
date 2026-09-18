package toothless.storage;

import toothless.task.TaskList;

/**
 * Contains tasks recovered from storage and the number of entries skipped.
 */
public class StorageLoadResult {
    private final TaskList taskList;
    private final int malformedLineCount;

    /**
     * Creates the result of reading a complete task data file.
     *
     * @param taskList valid tasks recovered from the file.
     * @param malformedLineCount number of invalid lines skipped.
     */
    public StorageLoadResult(TaskList taskList, int malformedLineCount) {
        this.taskList = taskList;
        this.malformedLineCount = malformedLineCount;
    }

    /**
     * Returns the valid tasks recovered from storage.
     *
     * @return recovered task list
     */
    public TaskList getTaskList() {
        return taskList;
    }

    /**
     * Returns how many saved lines could not be understood.
     *
     * @return malformed saved-line count
     */
    public int getMalformedLineCount() {
        return malformedLineCount;
    }
}
