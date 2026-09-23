package max.task;

import max.data.TaskType;
import max.data.Status;
public class Todo extends Task {
  
    public Todo(String desc) {
        super(desc, TaskType.TODO);
    }

    public Todo(String desc, Status status) {
        super(desc, TaskType.TODO, status);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
