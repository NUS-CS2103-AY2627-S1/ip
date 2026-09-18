package com.fireinyu.themyth.storage;

import com.fireinyu.themyth.exceptions.ArgumentFormatException;
import com.fireinyu.themyth.exceptions.CorruptedTaskFileException;
import com.fireinyu.themyth.exceptions.TaskIndexException;
import com.fireinyu.themyth.tasks.DeadlineTask;
import com.fireinyu.themyth.tasks.EventTask;
import com.fireinyu.themyth.tasks.Task;
import com.fireinyu.themyth.tasks.TodoTask;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * List of Tasks that can be synced with a CSV file on disk.
 *
 * @see Task
 * @see FileLinesDisk
 */
public class TaskList extends CsvBackedList<Task> {
    private static final int COLUMN_TYPE = 0;
    private static final int COLUMN_COMPLETED = 1;
    private static final int COLUMN_CREATED = 2;
    private static final int COLUMN_MODIFIED = 3;
    private static final int COLUMN_DESCRIPTION = 4;
    private static final int COLUMN_START_OR_DEADLINE = 5;
    private static final int COLUMN_END = 6;

    /**
     * Initializes a TaskList.
     * It is initially not backed by any FileLinesDisk so it acts as an ArrayList of Tasks.
     * Call open() to sync to a FileLinesDisk.
     *
     * @see FileLinesDisk
     * @see Task
     */
    public TaskList() {
    }

    @Override
    protected Task parse(String... fields) {
        try {
            Task task = createTask(fields);
            if (Boolean.parseBoolean(fields[COLUMN_COMPLETED])) {
                task.mark();
            } else {
                task.unmark();
            }
            task.setAccessTimes(MythDateTime.parse(fields[COLUMN_CREATED]),
                    MythDateTime.parse(fields[COLUMN_MODIFIED]));
            return task;
        } catch (IndexOutOfBoundsException | ArgumentFormatException e) {
            throw corruptedTaskFile();
        }
    }

    /**
     * Creates the task subtype from its stored type code and description fields.
     */
    private Task createTask(String[] fields) {
        return switch (fields[COLUMN_TYPE]) {
            case "T" -> new TodoTask(fields[COLUMN_DESCRIPTION]);
            case "D" -> new DeadlineTask(fields[COLUMN_DESCRIPTION],
                    MythDateTime.parse(fields[COLUMN_START_OR_DEADLINE]));
            case "E" -> new EventTask(fields[COLUMN_DESCRIPTION],
                    MythDateTime.parse(fields[COLUMN_START_OR_DEADLINE]), MythDateTime.parse(fields[COLUMN_END]));
            default -> throw corruptedTaskFile();
        };
    }

    /**
     * Creates an error identifying the backing file, or the in-memory list if no file is open.
     */
    private CorruptedTaskFileException corruptedTaskFile() {
        String path = super.getDescriptor() != null ? super.getDescriptor().toString() : "in-memory";
        return new CorruptedTaskFileException(path);
    }

    @Override
    public Task get(int index) {
        try {
            return super.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskIndexException(index + 1, size());
        }
    }
}
