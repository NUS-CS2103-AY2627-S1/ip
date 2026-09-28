package nicola;

/**
 * represent a simple todo task
 */
public class TodoTask extends Task {
    TodoTask(String description) {
        super(description);
    }

    @Override
    String getType() {
        return "T";
    }

    @Override
    String getDisplayText() {
        return getDescription();
    }

    @Override
    String serialize() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
