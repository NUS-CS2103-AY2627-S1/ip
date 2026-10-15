package spoon.exception;

/**
 * Thrown when task list already contains the task to be added.
 */
public class DuplicateTaskException extends SpoonException {

  // Constructor
  public DuplicateTaskException() {
        super("This task already exists! :(");
    }
}
