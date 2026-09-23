package max.task;
import java.time.LocalDate;
import max.data.Status;
import max.data.TaskType;

public abstract class Task {

    private String desc;
    private Status isDone = Status.NOT_DONE;
    private TaskType type;

    public Task(String task, TaskType type) {
        assert type != null : "Every task must have a task type";
        this.desc = task;
        this.type = type;
    }

    public Task(String task, TaskType type, Status isDone) {
        assert type != null : "Every task must have a task type";
        assert isDone != null : "Every task must have a completion status";
        this.desc = task;
        this.type = type;
        this.isDone = isDone;
    }
    
    public String getDescription() {
         return this.desc;
    }

    public void markAsDone() {
        this.isDone = Status.DONE;
    }
    public void notDone() {
        this.isDone = Status.NOT_DONE;
    }

    public boolean isOn(LocalDate date) {
        return false;
    }


    public String getItemString() {
                return this.type + " | " + this.isDone + " | " + this.desc;
    }
    
    @Override
    public String toString() {
        return this.type.toString() + " " + this.isDone + " " + this.desc;
    }

} 
