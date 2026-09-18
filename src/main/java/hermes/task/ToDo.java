package hermes.task;

import java.time.LocalDateTime;

/** A task with no date, so it is never due by any particular moment. */
public class ToDo extends Task {

    /** Creates a todo that is not yet completed. */
    public ToDo(String taskDescription) {
        super(taskDescription);
    }

    /** Creates a todo in a known state, used when loading from storage. */
    public ToDo(boolean isCompleted, String taskDescription) {
        super(isCompleted, taskDescription);
    }

    @Override
    public ToDo copy() {
        return new ToDo(this.isCompleted, this.taskDescription);
    }

    @Override
    public LocalDateTime getDueDateTime() {
        return null;
    }

    @Override
    public String getFileContent() {
        return String.format("%s | %d | %s",
                TaskType.TODO.getSymbol(),
                this.isCompleted ? 1 : 0,
                this.taskDescription);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
