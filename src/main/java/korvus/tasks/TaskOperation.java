package korvus.tasks;

/**
 * TaskOperation Class for operations on an individual task.
 */
public class TaskOperation {
    /**
     * Types of Task Operations
     */
    public enum OperationType {
        DO, ADD, UNDO, DELETE;

        private static final OperationType[] operations = values();

        /**
         * Returns the inverse task operation.
         *
         * @return Inverse TaskOperation to the current type.
         */
        public OperationType reverse() {
            return switch(this) {
                case DO -> UNDO;
                case UNDO -> DO;
                case ADD -> DELETE;
                case DELETE -> ADD;
                default -> null;
            };
        }
    }
    private int id;
    private Task task;
    private OperationType operation;

    /**
     * Returns an instance of a TaskOperation.
     *
     * @param id Index of the Task object to be affected.
     * @param task Task object to be affected.
     * @param operationType Type of Task Operation.
     */
    public TaskOperation(int id, Task task, OperationType operationType) {
        this.id = id;
        this.task = task;
        this.operation = operationType;
    }

    /**
     * Returns the inverse task operation to the current TaskOperation.
     *
     * @return TaskOperation that does the opposite action.
     */
    public TaskOperation getReverseOperation() {
        return new TaskOperation(this.id, this.task, this.operation.reverse());
    }

    /**
     * Returns the Type of this TaskOperation.
     *
     * @return OperationType of this TaskOperation.
     */
    public OperationType getOperation() {
        return operation;
    }

    /**
     * Returns the task to be affected.
     *
     * @return Task that the TaskOperation is for.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the index of the task to be affected.
     *
     * @return Index of the Task that the TaskOperation is for.
     */
    public int getId() {
        return id;
    }
}
