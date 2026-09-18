package korvus.tasks;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import korvus.storage.Storable;
import korvus.storage.StorageParser;
import korvus.utils.DateTimeParser;

/**
 * Tasklist for tracking all tasks and performing task operations.
 * Able to store information to a file.
 */
public class Tasklist implements Storable<Tasklist> {
    private static String TASK_SEP = "<>";
    private static int TASK_NOT_FOUND = -1;
    private static int OPERATION_HISTORY_LIMIT = 10;

    private DateTimeParser dateTimeParser;
    private ArrayList<Task> tasklist;
    private ArrayList<TaskOperation> taskOperationStack;

    /**
     * Returns an instance of tasklist.
     *
     * @param dateTimeParser DateTimeParser that should be used for datetime formatting.
     */
    public Tasklist(DateTimeParser dateTimeParser) {
        tasklist = new ArrayList<>();
        this.dateTimeParser = dateTimeParser;
        this.taskOperationStack = new ArrayList<>(10);
    }

    /**
     * Returns a default instance of tasklist.
     */
    public Tasklist() {
        this(new DateTimeParser(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    /**
     * Returns the total number of tasks.
     *
     * @return Size of the tasklist.
     */
    public int getSize() {
        return tasklist.size();
    }

    /**
     * Returns the index of the queried Task.
     * This assumes that the task is in the tasklist.
     *
     * @param task Task to be found.
     * @return Index of the task in the tasklist.
     */
    public int getIndex(Task task) {
        return tasklist.indexOf(task);
    }

    /**
     * Adds the task into the tasklist.
     *
     * @param task Task data to be added.
     * @return String representation of the new Task in the tasklist.
     * @throws InvalidTaskException If the task data provided is invalid.
     */
    public String addTask(String task) throws InvalidTaskException {
        Task newTask = Task.generateTask(task, dateTimeParser);

        return addTask(newTask);
    }

    /**
     * Adds the task into the tasklist.
     *
     * @param task Task to be added.
     */
    public String addTask(Task task) throws InvalidTaskException {
        TaskOperation addTask = new TaskOperation(tasklist.size(), task, TaskOperation.OperationType.ADD);
        return doTaskOperation(addTask);
    }

    /**
     * Adds the task into the tasklist.
     *
     * @param operation TaskOperation with task information to be added.
     */
    public void addTask(TaskOperation operation) {
        tasklist.add(operation.getTask());
    }

    /**
     * Removes the task from the tasklist.
     *
     * @param sTask Task data to be deleted.
     * @return String representation of the deleted Task in the tasklist.
     * @throws InvalidTaskException If the task data provided is invalid.
     */
    public String deleteTask(String sTask) throws InvalidTaskException {
        int id = findTaskIdByName(sTask);

        //Failed to find task
        if (id == TASK_NOT_FOUND) {
            throw new InvalidTaskException(String.format("Task cannot be found.\nName: %s", sTask));
        } else {
            return deleteTask(id);
        }
    }

    /**
     * Removes the task from the tasklist.
     *
     * @param id Index of Task to be deleted.
     * @return String representation of the deleted Task in the tasklist.
     * @throws InvalidTaskException If the task data provided is invalid.
     */
    public String deleteTask(int id) throws InvalidTaskException {
        TaskOperation deleteTask = new TaskOperation(id, tasklist.get(id), TaskOperation.OperationType.DELETE);
        return doTaskOperation(deleteTask);
    }

    /**
     * Removes the task from the tasklist.
     *
     * @param operation TaskOperation with the information for deletion.
     */
    public void deleteTask(TaskOperation operation) {
        tasklist.remove(operation.getId());
    };

    /**
     * Marks the task from the tasklist as done.
     *
     * @param sTask Task data to be marked as done.
     * @return String representation of the Task provided in the tasklist after marking.
     * @throws InvalidTaskException If the task is already marked as done.
     */
    public String doTask(String sTask) throws InvalidTaskException {
        int id = findTaskIdByName(sTask);

        //Failed to find task
        if (id == TASK_NOT_FOUND) {
            throw new InvalidTaskException(String.format("Task cannot be found.\nInput: %s", sTask));
        } else {
            return doTask(id);
        }
    };

    /**
     * Marks the task from the tasklist as done.
     *
     * @param id Index of Task to be marked as done.
     * @return String representation of the Task provided in the tasklist after marking.
     * @throws InvalidTaskException If the task is already marked as done.
     */
    public String doTask(int id) throws InvalidTaskException {
        TaskOperation doTask = new TaskOperation(id, tasklist.get(id), TaskOperation.OperationType.DO);
        return doTaskOperation(doTask);
    };

    /**
     * Marks the task from the tasklist as done.
     *
     * @param operation TaskOperation with the information for marking as done.
     * @throws InvalidTaskException If the task is already marked as done.
     */
    public void doTask(TaskOperation operation) throws InvalidTaskException {
        boolean status = operation.getTask().doTask();

        if (!status) {
            throw new InvalidTaskException(String.format("Task has already been done\n%s", operation.getTask()));
        }
    };

    /**
     * Marks the task from the tasklist as not done.
     *
     * @param sTask Task data to be marked as not done.
     * @return String representation of the Task provided in the tasklist after marking.
     * @throws InvalidTaskException If the task is already marked as not done.
     */
    public String undoTask(String sTask) throws InvalidTaskException {
        int id = findTaskIdByName(sTask);

        //Failed to find task
        if (id == TASK_NOT_FOUND) {
            throw new InvalidTaskException(String.format("Task cannot be found.\nName: %s", sTask));
        } else {
            return undoTask(id);
        }
    };

    /**
     * Marks the task from the tasklist as not done.
     *
     * @param id Index of Task to be marked as not done.
     * @return String representation of the Task provided in the tasklist after marking.
     * @throws InvalidTaskException If the task is already marked as not done.
     */
    public String undoTask(int id) throws InvalidTaskException {
        TaskOperation undoTask = new TaskOperation(id, tasklist.get(id), TaskOperation.OperationType.UNDO);
        return doTaskOperation(undoTask);
    };

    /**
     * Marks the task from the tasklist as not done.
     *
     * @param operation TaskOperation with the information for marking as not done.
     * @throws InvalidTaskException If the task is already marked as not done.
     */
    public void undoTask(TaskOperation operation) throws InvalidTaskException {
        boolean status = operation.getTask().undoTask();

        if (!status) {
            throw new InvalidTaskException(String.format("Task has not been done\n%s", operation.getTask()));
        }
    };

    /**
     * Finds tasks in the tasklist given a query.
     *
     * @param keyWord Keyword to search in the tasklist.
     * @return String representation of a list of Tasks that satisfy the provided keyword.
     */
    public String findTask(String keyWord) {
        StringBuilder stringBuilder = new StringBuilder();
        String searchPattern = ".*(" + keyWord + ").*";

        for (int i = 0; i < tasklist.size(); i++) {
            if (tasklist.get(i).getName().matches(searchPattern)) {
                addNewLine(stringBuilder);
                stringBuilder.append(String.format("%d. %s", i + 1, tasklist.get(i)));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Runs the provided TaskOperation
     *
     * @param taskOperation TaskOperation to be run.
     * @return String representation of the Task after said operation.
     * @throws InvalidTaskException
     */
    public String doTaskOperation(TaskOperation taskOperation) throws InvalidTaskException {
        executeTaskOperation(taskOperation);
        addOperationToHistory(taskOperation);

        return taskOperation.getTask().toString();
    }

    /**
     * Undoes the previous TaskOperation
     *
     * @return String representation of the Task after said operation.
     * @throws InvalidTaskException If the TaskOperation provided is invalid.
     */
    public String undoTaskOperation() throws InvalidTaskException {
        if (taskOperationStack.isEmpty()) {
            throw new InvalidTaskException("No task operation to undo!");
        }

        TaskOperation reverseOperation = taskOperationStack.removeLast().getReverseOperation();
        executeTaskOperation(reverseOperation);

        return reverseOperation.getTask().toString();
    }

    /**
     * Executes the TaskOperation provided.
     *
     * @param taskOperation TaskOperation to be run.
     * @throws InvalidTaskException If the TaskOperation provided is invalid.
     */
    private void executeTaskOperation(TaskOperation taskOperation) throws InvalidTaskException {
        switch (taskOperation.getOperation()) {
            case DO -> doTask(taskOperation);
            case UNDO -> undoTask(taskOperation);
            case ADD -> addTask(taskOperation);
            case DELETE -> deleteTask(taskOperation);
            default -> {
                assert false;
                throw new InvalidTaskException("Not a valid task operation!");
            }
        }
    }

    /**
     * Adds the TaskOperation to the recent operations stack.
     *
     * @param operation TaskOperation to be added.
     */
    private void addOperationToHistory(TaskOperation operation) {
        taskOperationStack.add(operation);

        if (taskOperationStack.size() > OPERATION_HISTORY_LIMIT) {
            taskOperationStack.removeFirst();
        }
    }

    /**
     * Adds a new line to the StringBuilder if it is non-empty.
     *
     * @param stringBuilder StringBuilder to append a new line to.
     */
    private void addNewLine(StringBuilder stringBuilder) {
        if (!stringBuilder.isEmpty()) {
            stringBuilder.append('\n');
        }
    }

    /**
     * Returns the id of the task that has the same name as the query.
     *
     * @param sTask Name of the task to find.
     * @return Index of the task with the same name.
     */
    private int findTaskIdByName(String sTask) {
        int id = TASK_NOT_FOUND;
        for (int i = 0; i < tasklist.size(); i++) {
            if (tasklist.get(i).getName().equals(sTask)) {
                id = i;
                break;
            }
        }

        return id;
    }

    /**
     * Writes the tasklist into a String for storing.
     *
     * @return String containing all the task information in the tasklist.
     */
    @Override
    public String writeToString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < tasklist.size(); i++) {
            output.append(tasklist.get(i).writeToStore());
            if (i + 1 != tasklist.size()) {
                output.append(TASK_SEP);
            }
        }
        return output.toString();
    }

    /**
     * Reads from a StorageParser to populate the tasks in the tasklist.
     *
     * @param storageParser StorageParser to read from.
     * @return Boolean on whether there are any errors in the file operations.
     * @throws IOException If there is an error in reading the file.
     */
    @Override
    public boolean readFromParser(StorageParser<? extends Storable<Tasklist>> storageParser) throws IOException {
        boolean hasErrors = false;

        for (String sTask : storageParser.readStorage().split(TASK_SEP)) {
            try {
                tasklist.add(Task.readTaskFromFile(sTask, dateTimeParser));
            } catch (InvalidTaskException | IndexOutOfBoundsException e) {
                hasErrors = true;
            }
        }

        return hasErrors;
    }

    /**
     * Returns a String containing all tasks.
     *
     * @return String containing all tasks in a formatted list.
     */
    @Override
    public String toString() {
        StringBuilder tasks = new StringBuilder();
        for (int i = 0; i < tasklist.size(); i++) {
            tasks.append(String.format("%d. %s", i + 1, tasklist.get(i)));
            if (i != tasklist.size() - 1) {
                tasks.append("\n");
            }
        }
        return tasks.toString();
    }
}
